package com.e8.alert;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.securityx.modelfeature.FeatureServiceCache;
import com.securityx.modelfeature.FeatureServiceThreadPool;
import com.securityx.modelfeature.alert.EmailAlertListener;
import com.securityx.modelfeature.alert.auditlog.AlertAuditLogger;
import com.securityx.modelfeature.common.inputs.AlertDefinition;
import com.securityx.modelfeature.common.inputs.AlertDestination;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.SuppressionDao;
import com.securityx.modelfeature.utils.CEF;
import com.securityx.modelfeature.utils.CefExtension;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class EmailAlertListenerTest {

    private static final ObjectMapper mapper = Jackson.newObjectMapper();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static FeatureServiceConfiguration conf = null;
    private static FeatureServiceCache featureServiceCache;
    private static AlertAuditLogger alertLogger;
    private static FeatureServiceThreadPool threadPool;
    private static AlertDestination alertDestination;
    private static AlertDefinition alertDefinition;
    @BeforeClass
    public static void setup() throws Exception{
        String confFile = System.getProperty("user.dir") + "/src/main/config/dev_cfg.yml";
        conf = new ConfigurationFactory<FeatureServiceConfiguration>(FeatureServiceConfiguration.class, validator, mapper, "dw").build(new File(confFile));
        featureServiceCache = new FeatureServiceCache(conf);
        threadPool = new FeatureServiceThreadPool(10);
        alertLogger = new AlertAuditLogger(conf, threadPool);
        alertDestination = new AlertDestination();
        alertDefinition = new AlertDefinition();
    }
    @Test
    public void  testAlertSorting() throws Exception{

        EmailAlertListener listener = new EmailAlertListener(alertDestination, featureServiceCache.getEmailSenderConfiguration(), alertLogger);
        List<CEF> cefList = new ArrayList<>();
        for(int i=0; i<10;i++){
            String vendor ="testVendor"+i;
            String product ="testProduct"+i;
            String deviceVersion ="testDeviceVersion"+i;
            String signatureId = "123456"+i;
            String name = "testName"+i;
            double severity = (double)i/10;
            CefExtension cefExtension = new CefExtension(new HashMap<String, String>());
            CEF cef = new CEF(vendor, product, signatureId, name, severity, cefExtension, featureServiceCache);
            cefList.add(cef);
        }
        // the code is not really testable
        //listener.sendEmail(alertDefinition, cefList);
        List<Map<String, String>> list = Lists.newArrayList();
        for (CEF cef : cefList) {
            listener.populateListOfAlerts(cef, list);
        }
        listener.sortListOfAlerts(list);
        assertTrue(list.get(0).get("risk").equals("9.0"));
        assertTrue(list.get(1).get("risk").equals("8.0"));
        assertTrue(list.get(2).get("risk").equals("7.0"));
        assertTrue(list.get(3).get("risk").equals("6.0"));
        assertTrue(list.get(4).get("risk").equals("5.0"));
        assertTrue(list.get(5).get("risk").equals("4.0"));
    }
}
