package com.securityx.modelfeature;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.scala.DefaultScalaModule;
import com.mongodb.MongoClient;
import com.securityx.modelfeature.alert.AlertNotifier;
import com.securityx.modelfeature.alert.AlertNotifierImpl;
import com.securityx.modelfeature.alert.auditlog.AlertAuditLogger;
import com.securityx.modelfeature.alert.scheduler.AlertNotificationScheduler;
import com.securityx.modelfeature.alert.zookeeper.ZkAlertPathChildrenCacheListener;
import com.securityx.modelfeature.alert.zookeeper.ZookeeperClient;
import com.securityx.modelfeature.auth.SecurityProvider;
import com.securityx.modelfeature.auth.User;
import com.securityx.modelfeature.auth.UserAuthenticator;
import com.securityx.modelfeature.common.cache.AlertCache;
import com.securityx.modelfeature.common.cache.AutoCompleteCache;
import com.securityx.modelfeature.common.cache.FeatureResponseCache;
import com.securityx.modelfeature.dao.MongoUtils;
import com.securityx.modelfeature.resources.reporting.ReportingFeature;
import com.securityx.modelfeature.utils.Constants;
import com.securityx.modelfeature.utils.SearchUtils;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.health.ModelFeatureServiceHealth;
import com.securityx.modelfeature.resources.*;
import com.securityx.modelfeature.resources.alerts.AlertingFeature;
import com.securityx.modelfeature.resources.reporting.ReportingFeature;
import com.securityx.modelfeature.utils.Constants;
import com.securityx.modelfeature.utils.SearchUtils;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.jsonwebtoken.impl.crypto.MacProvider;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.security.Key;
import java.util.EnumSet;


public class FeatureService extends Application<FeatureServiceConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureService.class);

    //single instances
    private static ObjectMapper mapper;
    private static AlertNotifier alertNotifier;
    private static AlertNotificationScheduler alertNotificationScheduler;

    private final int CORE_POOL_SIZE = 10;
    private final int MAXIMUM_POOL_SIZE = 20;

    // Generate and holds a Security Token Key used in Security Token Authentication
    public static Key securityTokenKey = MacProvider.generateKey();

    public FeatureService() {
        super();
    }


    @Override
    public void initialize(final Bootstrap<FeatureServiceConfiguration> modelFeatureConfigurationBootstrap) {

        modelFeatureConfigurationBootstrap.addBundle(new AssetsBundle("/app1/", "/app1/", "index.html"));
        //modelFeatureConfigurationBootstrap.addBundle(new AssetsBundle("/assets/", "/html", "index.html"));
        //modelFeatureConfigurationBootstrap.addBundle(new AssetsBundle("/assets2/", "/html2", "index.html"));

    }

    @Override
    public void run(FeatureServiceConfiguration featureServiceConfiguration, Environment environment) throws Exception {
        //LOGGER.debug(" zk quorum is {} ", featureServiceConfiguration.getZkQuorum());

        environment.jersey().setUrlPattern("/service/*");
        mapper = new ObjectMapper();
        mapper.registerModule(new DefaultScalaModule());
        //Thread Pool
        FeatureServiceThreadPool featureServiceThreadPool = new FeatureServiceThreadPool(featureServiceConfiguration.getThreadPool().getCorePoolSize());

        //caches
        FeatureServiceCache featureServiceCache = new FeatureServiceCache(featureServiceConfiguration);
        AutoCompleteCache autoCompleteCache = new AutoCompleteCache(featureServiceConfiguration);
        FeatureResponseCache featureResponseCache = new FeatureResponseCache(featureServiceConfiguration);
        AlertCache alertCache = new AlertCache();

        //configure alerts:
        AlertAuditLogger alertLogger = new AlertAuditLogger(featureServiceConfiguration, featureServiceThreadPool);
        alertNotifier = new AlertNotifierImpl(alertLogger);
        alertNotificationScheduler = new AlertNotificationScheduler(alertNotifier, alertLogger, featureServiceThreadPool);
        ZkAlertPathChildrenCacheListener zkAlertPathChildrenCacheListener = new ZkAlertPathChildrenCacheListener(alertNotifier,
                alertNotificationScheduler, featureServiceCache, alertCache, featureServiceConfiguration, alertLogger);
        ZookeeperClient zkClient = new ZookeeperClient(featureServiceConfiguration.getZkQuorum(), zkAlertPathChildrenCacheListener);
        //managed Objects
        environment.lifecycle().manage(zkClient);
        environment.lifecycle().manage(featureServiceThreadPool);


        //clients
        SolrServerClient solrClient = new SolrServerClient(featureServiceConfiguration);
        CloudSolrServer webProxySolrClient = solrClient.getSolrServer(SearchUtils.getCollectionName(
                Constants.WebPeerAnomaliesModelTuple()._1()));
        CloudSolrServer iamSolrClient = solrClient.getSolrServer(SearchUtils.getCollectionName(
                Constants.ADPeerAnomaliesModelTuple()._1()));
        CloudSolrServer taniumUetSolrClient = solrClient.getSolrServer(Constants.TANIUM_UET_MEF_COLLECTION());
        CloudSolrServer taniumHetSolrClient = solrClient.getSolrServer(Constants.TANIUM_HET_MEF_COLLECTION());
        CloudSolrServer taniumHostInfoSolrClient = solrClient.getSolrServer(Constants.TANIUM_HOST_INFO_MEF_COLLECTION());

        // Mongo Client
        MongoClient client = MongoUtils.getClient(featureServiceConfiguration);
        featureServiceConfiguration.getMongoDB().setClient(client);

        environment.jersey().register(new AbnormalBehaviorFeature(mapper, webProxySolrClient, iamSolrClient, featureServiceConfiguration, featureServiceCache));
        environment.jersey().register(new AlertingFeature(mapper, featureServiceConfiguration, featureServiceCache, alertCache,
                alertNotifier, alertLogger, zkClient));
        environment.jersey().register(new ReportingFeature(mapper, featureServiceConfiguration));
        environment.jersey().register(new BeaconsFeature(mapper, featureServiceConfiguration));
        environment.jersey().register(new C2ModelFeature(mapper, featureServiceConfiguration, featureServiceCache));
        environment.jersey().register(new CloudChamberFeature(mapper, featureServiceConfiguration));
        environment.jersey().register(new CoordinateActivityFeature(mapper, featureServiceConfiguration));
        environment.jersey().register(new DetectorHomeFeature(mapper, featureServiceConfiguration, featureServiceCache, autoCompleteCache));
        environment.jersey().register(new EntityFeatures(mapper, featureServiceConfiguration, featureServiceCache));
        environment.jersey().register(new EntityInvestigatorFeature(mapper, featureServiceConfiguration, featureServiceCache, autoCompleteCache));
        environment.jersey().register(new FacetFeature(mapper, webProxySolrClient, iamSolrClient, featureServiceConfiguration, featureServiceCache));
        environment.jersey().register(new GlobalStatusFeature(mapper, featureServiceConfiguration, featureServiceCache));
        environment.jersey().register(new HostEntityPropertiesDetail(mapper, featureServiceConfiguration));
        environment.jersey().register(new HttpTimeSeriesFeature(mapper, featureServiceConfiguration));
        environment.jersey().register(new IOCFeature(mapper, webProxySolrClient, featureServiceConfiguration));
        environment.jersey().register(new KillchainHomeFeature(mapper, featureServiceConfiguration, featureServiceCache));
        environment.jersey().register(new LogFeatureResource(mapper, featureServiceConfiguration));
        environment.jersey().register(new ModelFeature(mapper, featureServiceConfiguration, featureServiceCache));
        environment.jersey().register(new PeerGroupFeature(mapper, featureServiceConfiguration, featureServiceCache));
        environment.jersey().register(new QueryOperatorFeature(mapper, featureServiceConfiguration, featureServiceCache));
        environment.jersey().register(new SearchFeature(mapper, webProxySolrClient, iamSolrClient, featureServiceConfiguration, featureServiceCache));
        environment.jersey().register(new SecurityEventTimeSeriesFeature(mapper, featureServiceConfiguration, featureServiceCache));
        environment.jersey().register(new TimeSeriesFeature(mapper, webProxySolrClient, iamSolrClient,taniumHostInfoSolrClient, taniumHetSolrClient, taniumUetSolrClient,
                featureServiceConfiguration, featureServiceCache, featureResponseCache));
        environment.jersey().register(new TopNHostEntityProperties(mapper, featureServiceConfiguration));
        environment.jersey().register(new UserEntityPropertiesDetail(mapper, featureServiceConfiguration));
        environment.jersey().register(new WebHistoryFeature(mapper, webProxySolrClient, iamSolrClient, featureServiceConfiguration, featureServiceCache));
        environment.jersey().register(new RawlogFeature(mapper, featureServiceConfiguration));

        environment.jersey().register(new UserService(featureServiceConfiguration));

        // Adds security provider so resource methods decorated with auth attribute will use this authenticator
        environment.jersey().register(new SecurityProvider<User>(new UserAuthenticator(), featureServiceConfiguration));

        environment.jersey().register(new InvestigationFeature(mapper, featureServiceConfiguration));
        environment.jersey().register(new Version());
        ModelFeatureServiceHealth health = new ModelFeatureServiceHealth("my health");
        environment.healthChecks().register(health.getClass().getName(), health );

        // export the Dropwizard Metrics as Prometheus Metrics
        CollectorRegistry collectorRegistry = new CollectorRegistry();
        collectorRegistry.register(new DropwizardExports(environment.metrics()));
        environment.admin()
                .addServlet("prometheusMetrics", new MetricsServlet(collectorRegistry))
                .addMapping("/prometheusMetrics");
        configureCors(environment);
    }

    private void configureCors(Environment environment) {
        FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        filter.setInitParameter("allowCredentials", "true");
    }

    public static void main(final String[] args) throws Exception {
        LOGGER.debug("Entering main.........");
        new FeatureService().run(args);
    }
}
