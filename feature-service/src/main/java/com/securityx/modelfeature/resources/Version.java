package com.securityx.modelfeature.resources;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.PropertyResourceBundle;

@Path("/version")
@Produces(MediaType.APPLICATION_JSON)
@Api(value ="version")
public class Version {
    private static final Logger LOGGER = LoggerFactory.getLogger(Version.class);

    @GET
    @Timed
    @Consumes("application/version.v1+json") // we don't expect the API to change. this annotation is here for consistency.
    @ApiOperation(value = "Find the version of code that was used for building this API server", response = HashMap.class, responseContainer = "Map")
    public HashMap<String, String> getVersion() throws Exception{
        try {
            PropertyResourceBundle bundle = new PropertyResourceBundle(Version.class.getClassLoader().getResourceAsStream("application.properties"));
            HashMap<String, String> hashMap = new HashMap<String, String>();
            Enumeration<String> keys = bundle.getKeys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                String value = bundle.getString(key);
                hashMap.put(key, value);
            }
            return hashMap;
        }catch(Exception ex){
            LOGGER.error("Could not load the application.properties file! Error: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
}
