package com.e8.solr;

import com.e8.test.HBaseTestBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.PhoenixUtils;
import com.securityx.modelfeature.resources.UserService;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.xml.sax.InputSource;

import javax.validation.Validation;
import javax.validation.Validator;

//import org.apache.solr.core.SolrResourceLoader;
//import org.apache.solr.util.SystemIdResolver;

public class TestSchemaParsing extends HBaseTestBase{
/*
    @Test
    public void testSchemaParsing()throws Exception{
        String resourceName ="iam_mef";
        InputStream configInputStream = new URL("http://cluster5-srv2:8983/solr/iam_mef/admin/file?file=solrconfig.xml&contentType=text/xml;charset=utf-8").openStream();
        InputStream schemaInputStream = new URL("http://cluster5-srv2:8983/solr/iam_mef/admin/file?file=schema.xml&contentType=text/xml;charset=utf-8").openStream();
        InputSource schemaInputSource = new InputSource(schemaInputStream);
        InputSource configInputSource = new InputSource(configInputStream);
        schemaInputSource.setSystemId(SystemIdResolver.createSystemIdFromResourceName("iam_mef"));
        SolrConfig config = new SolrConfig("config", configInputSource);
        IndexSchema schema = new IndexSchema(config, resourceName, schemaInputSource);
    }
*/

}


