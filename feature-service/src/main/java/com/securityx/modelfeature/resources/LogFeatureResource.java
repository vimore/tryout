package com.securityx.modelfeature.resources;


import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/log")
@Produces(MediaType.APPLICATION_JSON)
public class LogFeatureResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogFeatureResource.class);

    private final FeatureServiceConfiguration conf;
    private ObjectMapper mapper;

    public LogFeatureResource (ObjectMapper mapper, FeatureServiceConfiguration conf) {
        this.conf = conf;
        this.mapper = mapper.copy();
        this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @GET
    @Path("/records/")
    @Timed
    @Deprecated
    public List<String> getRecords(@QueryParam("customerId") String customerId,
                                   @QueryParam("startTimestamp") Long startTimestamp,
                                   @QueryParam("endTimestamp") Long endTimestamp) {


        return Arrays.asList("Output1", "Output2");
    }

    /**
     * Returns a Json string containing the configuration of the API server.  This can then be
     * deserialized into a HashMap to allow the UI to see the configuration of the API server.
     *
     * @return Json String of the API server configuration
     */
    @ApiOperation(value="API for getting the API server configuration information",httpMethod= "GET",
            notes="API for getting the API server configuration information",
            produces = MediaType.APPLICATION_JSON, response=Response.class)
    @ApiResponses(value={@ApiResponse(code= 200, message = "Response, a serialized version of the configuration with which the API server was started"),
            @ApiResponse(code= 401,message="UnAuthorized access"),
            @ApiResponse(code= 400, message = "Bad Request")})
    @GET
    @Path("/config/")
    @Produces({ MediaType.APPLICATION_JSON })
    @Timed
    public String getConfig() {
        String configJson = null;
        try {
            configJson = mapper.writeValueAsString(conf);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error serializing config", e);
        }
        return configJson;
    }
}
