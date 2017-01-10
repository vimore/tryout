package com.securityx.modelfeature.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.mongodb.MongoClient;
import com.securityx.modelfeature.FeatureService;
import com.securityx.modelfeature.utils.Constants;
import io.dropwizard.Configuration;
import io.dropwizard.util.Duration;
import io.dropwizard.validation.ValidationMethod;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FeatureServiceConfiguration extends Configuration {

    public static final Logger log = LoggerFactory.getLogger(FeatureServiceConfiguration.class);

    public static final String DEV_ENV = "DEV";

    @JsonProperty
    private String haiku = "";

    @NotEmpty
    private String zkQuorum;

    @NotEmpty
    private String solrQuorum;

    @NotEmpty
    private String environment;

    @NotEmpty
    private String includeBeaconingBehaviors;

    @NotEmpty
    private String includeC2Behaviors;

    @Valid
    @NotNull
    @JsonProperty
    private PhoenixConfiguration phoenix = new PhoenixConfiguration();

    @Valid
    @NotNull
    @JsonProperty
    private ThreadPoolConfiguration threadPool;

    @Valid
    @NotNull
    @JsonProperty
    private ConfigurationConstants configurationConstants;

    @Valid
    @NotNull
    @JsonProperty
    private EntityFusionConfiguration entityFusion = new EntityFusionConfiguration();

    @Valid
    @NotNull
    @JsonProperty
    private GraphiteConfiguration graphite = new GraphiteConfiguration();

    @Valid
    @NotNull
    @JsonProperty
    private SecurityTokenConfiguration securityToken = new SecurityTokenConfiguration();
    
    private MongoDBConfiguration mongoDB = new MongoDBConfiguration();

    @Valid
    @JsonProperty
    private String investigationApiKey;

    public String getInvestigationApiKey() {
        return investigationApiKey;
    }

    public void setInvestigationApiKey(String investigationApiKey) {
        this.investigationApiKey = investigationApiKey;
    }

    @Valid
    @JsonProperty
    private String investigationClient;

    public String getInvestigationClient() {
        return investigationClient;
    }

    public void setInvestigationClient(String investigationClient) {
        this.investigationClient = investigationClient;
    }

    @Valid
    @JsonProperty
    private String endpointLimit = "200";

    public int getEndpointLimit() {
        int result = 200;
        try {
           result = Integer.parseInt(endpointLimit);
        } catch (NumberFormatException nfe) {
            log.error("Could not parse endpointLimit [" + endpointLimit + "].  Must be an integer, defaulting to 200");
        }
        return result;
    }

    public void setEndpointLimit(String endpointLimit) {
        this.endpointLimit = endpointLimit;
    }

    @Valid
    @JsonProperty
    private String suppressStatisticsForModels="";

    public String getSuppressStatisticsForModels() {
        return suppressStatisticsForModels;
    }

    public List<Integer> getSuppressStatisticsForModelsList() {
        List<Integer> modelList = new ArrayList<>();
        String[] models = suppressStatisticsForModels.split(",");
        for (String modelStr : models) {
            try {
                modelList.add(Integer.parseInt(modelStr.trim()));
            } catch (NumberFormatException nfe) {
                log.error("Could not parse suppressStatisticsForModels entry [" + modelStr + "]. Must be an integer.");
            }
        }
        // We should always suppress the beacon model
        if (!modelList.contains(Constants.BeaconModelTuple()._1())) {
            modelList.add(Constants.BeaconModelTuple()._1());
        }
        return modelList;
    }

    public void setSuppressStatisticsForModels(String suppressStatisticsForModels) {
        this.suppressStatisticsForModels = suppressStatisticsForModels;
    }

    @JsonProperty
    public String getZkQuorum() {
        return zkQuorum;
    }

    @JsonProperty
    public void setZkQuorum(String zkQuorum) {
        this.zkQuorum = zkQuorum;
    }

    @JsonProperty
    public String getSolrQuorum() {
        return solrQuorum;
    }

    @JsonProperty
    public void setSolrQuorum(String solrQuorum) {
        this.solrQuorum = solrQuorum;
    }

    @JsonProperty
    public String getEnvironment() {return environment;}

    @JsonProperty
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    @JsonProperty
    public boolean includeC2Behaviors() {
        return Boolean.parseBoolean(includeC2Behaviors);
    }

    @JsonProperty
    public void setIncludeC2Behaviors(String includeC2Behaviors) {
        this.includeC2Behaviors = includeC2Behaviors;
    }

    @JsonProperty
    public boolean includeBeaconingBehaviors() {
        return Boolean.parseBoolean(includeBeaconingBehaviors);
    }

    @JsonProperty
    public void setIncludeBeaconingBehaviors(String includeBeaconingBehaviors) {
        this.includeBeaconingBehaviors = includeBeaconingBehaviors;
    }

    public PhoenixConfiguration getPhoenix() {
        return phoenix;
    }

    public void setPhoenix(PhoenixConfiguration phoenix) {
        this.phoenix = phoenix;
    }

    public ThreadPoolConfiguration getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ThreadPoolConfiguration threadPool) {
        this.threadPool = threadPool;
    }

    public GraphiteConfiguration getGraphite() {
        return graphite;
    }

    public void setGraphite(GraphiteConfiguration graphite) {
        this.graphite = graphite;
    }

    public MongoDBConfiguration getMongoDB() {
        return mongoDB;
    }

    public void setMongoDB(MongoDBConfiguration mongoDB) {
        this.mongoDB = mongoDB;
    }

    public EntityFusionConfiguration getEntityFusionConfiguration() {
        return entityFusion;
    }

    public void setEntityFusionConfiguration(EntityFusionConfiguration entityFusion) {
        this.entityFusion = entityFusion;
    }

    public ConfigurationConstants getConfigurationConstants() {
        return configurationConstants;
    }

    public void setConfigurationConstants(ConfigurationConstants configurationConstants) {
        this.configurationConstants = configurationConstants;
    }

    public String getHaiku() {
        return haiku;
    }

    // Global Status configuration section.
    @Valid
    @NotNull
    @JsonProperty
    private GlobalStatusConfiguration globalStatusConfiguration = new GlobalStatusConfiguration();

    @JsonProperty
    public GlobalStatusConfiguration getGlobalStatus() { return globalStatusConfiguration; }

    @JsonProperty
    public void setGlobalStatus(GlobalStatusConfiguration globalStatus) { this.globalStatusConfiguration = globalStatus; }

    // Global Risk Range configuration to determine High, Medium and Low risk range
    @Valid
    @NotNull
    @JsonProperty
    private RiskRangeConfiguration riskRanges = new RiskRangeConfiguration();

    public RiskRangeConfiguration getRiskRanges() {
        return riskRanges;
    }

    public void setRiskRanges(RiskRangeConfiguration riskRanges) {
        this.riskRanges = riskRanges;
    }

    // Cloudchamber configuration section.
    @Valid
    @NotNull
    @JsonProperty
    private CloudChamberConfiguration cloudchamber = new CloudChamberConfiguration();

    @JsonProperty
    public CloudChamberConfiguration getCloudchamber() { return cloudchamber; }

    @JsonProperty
    public void setCloudchamber(CloudChamberConfiguration cloudchamber) { this.cloudchamber = cloudchamber; }

    // Impala configuration section.
    @Valid
    @NotNull
    @JsonProperty
    private ImpalaConfiguration impala = new ImpalaConfiguration();

    @JsonProperty
    public ImpalaConfiguration getImpalaConfiguration() { return impala; }

    @JsonProperty
    public void setImpalaConfiguration(ImpalaConfiguration impalaConf) { this.impala = impalaConf; }

    public SecurityTokenConfiguration getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(SecurityTokenConfiguration securityToken) {
        this.securityToken = securityToken;
    }

    // Vendor info config section
    @Valid
    @NotNull
    @JsonProperty
    private List<VendorInfoConfiguration> vendors = new ArrayList<>();

    @JsonProperty
    public List<VendorInfoConfiguration> getVendors() {
        return vendors;
    }

    @JsonProperty
    public void setVendors(List<VendorInfoConfiguration> vendors) {
        this.vendors = vendors;
    }

    public static class VendorInfoConfiguration {

        @JsonProperty
        private String name;

        @JsonProperty
        private boolean enabled;

        @JsonProperty
        private String displayLabel;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getDisplayLabel() {
            return displayLabel;
        }

        public void setDisplayLabel(String displayLabel) {
            this.displayLabel = displayLabel;
        }
    }


    // Graphite configuration section.
    public static class GraphiteConfiguration {

        @JsonProperty
        private boolean enabled;

        @JsonProperty
        private String host;

        @JsonProperty
        private int port;

        @JsonProperty
        private Duration outputPeriod = Duration.minutes(1);

        @ValidationMethod()
        public boolean isValid() {
            if (!enabled) {
                return true;
            }
            if (port <= 0) {
                log.error("Port must be > 0");
                return false;
            }
            if (Strings.isNullOrEmpty(host)) {
                log.error("Host may not be blank");
                return false;
            }
            if (outputPeriod == null) {
                log.error("Output period must be specified");
                return false;
            }
            if (outputPeriod.toSeconds() < 1) {
                log.error("Output period must be >= 1 seconds");
                return false;
            }
            return true;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public Duration getOutputPeriod() {
            return outputPeriod;
        }
    }


    // Graphite configuration section.
    public static class ThreadPoolConfiguration {

        @JsonProperty
        private int corePoolSize;

        @JsonProperty
        private int maxPoolSize;

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }
    }

    // Mongo DB configuration section
    public static class MongoDBConfiguration {
        @JsonProperty
        private String mongoServer;

        @JsonProperty
        private int mongoPort;

        @JsonProperty
        private int maxWaitTime = 1000;

        @JsonProperty
        private int connectionTimeout = 5000;

        @JsonProperty
        private int socketTimeout = 5000;

        @JsonProperty
        private int serverSelectionTimeout = 1000;

        private MongoClient client = null;

        public String getMongoServer() {
            return mongoServer;
        }

        public void setMongoServer(String mongoServer) {
            this.mongoServer = mongoServer;
        }

        public int getMongoPort() {
            return mongoPort;
        }

        public void setMongoPort(int mongoPort) {
            this.mongoPort = mongoPort;
        }

        public int getMaxWaitTime() {
            return maxWaitTime;
        }

        public void setMaxWaitTime(int maxWaitTime) {
            this.maxWaitTime = maxWaitTime;
        }

        public int getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        public int getSocketTimeout() {
            return socketTimeout;
        }

        public void setSocketTimeout(int socketTimeout) {
            this.socketTimeout = socketTimeout;
        }

        public int getServerSelectionTimeout() {
            return serverSelectionTimeout;
        }

        public void setServerSelectionTimeout(int serverSelectionTimeout) {
            this.serverSelectionTimeout = serverSelectionTimeout;
        }

        public MongoClient getClient() {
            return client;
        }

        public void setClient(MongoClient client) {
            this.client = client;
        }
    }

    // Global Risk Range configuration section.
    public static class RiskRangeConfiguration{

        @JsonProperty
        private Map<String,Double> lowRisk;

        @JsonProperty
        private Map<String,Double> mediumRisk;

        @JsonProperty
        private Map<String,Double> highRisk;

        public Map<String, Double> getLowRisk() {
            return lowRisk;
        }

        public void setLowRisk(Map<String, Double> lowRisk) {
            this.lowRisk = lowRisk;
        }

        public Map<String, Double> getMediumRisk() {
            return mediumRisk;
        }

        public void setMediumRisk(Map<String, Double> mediumRisk) {
            this.mediumRisk = mediumRisk;
        }

        public Map<String, Double> getHighRisk() {
            return highRisk;
        }

        public void setHighRisk(Map<String, Double> highRisk) {
            this.highRisk = highRisk;
        }
    }

    @JsonProperty("fixNullValue")
    public FixNullValue fixNullValue = new FixNullValue();

    public static class FixNullValue{
        @JsonProperty("enable")
        public boolean enable = false;
        @JsonProperty("nullValue")
        public String nullValue = "NULL_VALUE";

        public boolean isEnabled() {
            return enable;
        }

        public void setEnabled(boolean enable) {
            this.enable = enable;
        }

        public String getNullValue() {
            return nullValue;
        }

        public void setNullValue(String nullValue) {
            this.nullValue = nullValue;
        }
    }

    public FixNullValue getFixNullValue() {
        return fixNullValue;
    }

    public void setFixNullValue(FixNullValue fixNullValue) {
        this.fixNullValue = fixNullValue;
    }

    @JsonProperty("autoCompleteCacheDir")
    public String autoCompleteCacheDir = "/tmp";

    public String getAutoCompleteCacheDir() {
        return autoCompleteCacheDir;
    }

    public void setAutoCompleteCacheDir(String autoCompleteCacheDir) {
        this.autoCompleteCacheDir = autoCompleteCacheDir;
    }
}

