package com.securityx.modelfeature.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanishka.virustotal.dto.FileScanReport;
import com.kanishka.virustotal.exception.APIKeyNotFoundException;
import com.kanishka.virustotal.exception.QuotaExceededException;
import com.kanishka.virustotal.exception.UnauthorizedAccessException;
import com.kanishka.virustotalv2.VirusTotalConfig;
import com.kanishka.virustotalv2.VirustotalPublicV2;
import com.kanishka.virustotalv2.VirustotalPublicV2Impl;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.investigation.InvestigationClient;
import com.securityx.modelfeature.dao.investigation.VirusTotalClientImpl;
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
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Path("/investigation")
@Produces(MediaType.APPLICATION_JSON)
public class InvestigationFeature {
    private static final Logger logger = LoggerFactory.getLogger(InvestigationFeature.class);
    FeatureServiceConfiguration conf = null;
    private ObjectMapper mapper = null;
    private InvestigationClient client = null;

    public InvestigationFeature(ObjectMapper mapper, FeatureServiceConfiguration conf) {
        this.mapper = mapper;
        this.conf = conf;
        String clientConf = conf.getInvestigationClient();
        if ("VirusTotal".equals(clientConf)) {
            // For now, VirusTotal is the only choice
            client = new VirusTotalClientImpl(conf);
        } else if (clientConf != null) {
            logger.error("Invalid investigation client [" + clientConf + "]");
            throw new IllegalArgumentException("Invalid investigation client [" + clientConf + "]");
        }
    }

    /**
     * This method returns information about a file hash, indicating whether files with that hash have been found to include viruses.
     *
     * @param hash the file hash to check
     * @return a map of information about the hash
     * @throws Exception
     */
    @ApiOperation(value="API for getting virus check information about a given file hash",httpMethod= "GET",notes="API for getting virus check information about a given file hash",
            produces = MediaType.APPLICATION_JSON, response=Response.class)
    @ApiResponses(value={@ApiResponse(code= 200, message = "Response, information about the file hash"),@ApiResponse(code= 401,message="UnAuthorized access"),
            @ApiResponse(code= 400, message = "Bad Request")})
    @GET
    @Path("/hashInfo")
    @Produces({ MediaType.APPLICATION_JSON })
    @Timed
    public Response getHashInfo(@QueryParam("hash") String hash) throws Exception {
        Map<String, String> results = null;
        if (client == null) {
            logger.warn("Calling hash investigation api, but no investigation client is enabled");
            String a = this.mapper.writeValueAsString(null);
            return Response.ok(a).build();
        } else {
            results = client.getHashInfo(hash);

            if (results.get(InvestigationClient.CLIENT_ERROR) == null) {
                String a = this.mapper.writeValueAsString(results);
                return Response.ok(a).build();
            } else {
                // For cases where some client error occurred, we expect a string explaining the error keyed
                // as "ClientError", and an integer error code keyed as "ClientErrorCode". This allows us
                // to pass back arbitrary errors that make sense for a given client.
                String errorCode = results.get(InvestigationClient.CLIENT_ERROR_CODE);
                return Response.status(Integer.parseInt(errorCode)).entity(results.get(InvestigationClient.CLIENT_ERROR)).build();
            }
        }
    }

}
