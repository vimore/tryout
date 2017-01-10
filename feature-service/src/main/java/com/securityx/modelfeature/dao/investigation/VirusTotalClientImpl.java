package com.securityx.modelfeature.dao.investigation;

import com.google.common.annotations.VisibleForTesting;
import com.kanishka.virustotal.dto.FileScanReport;
import com.kanishka.virustotal.exception.APIKeyNotFoundException;
import com.kanishka.virustotal.exception.QuotaExceededException;
import com.kanishka.virustotal.exception.UnauthorizedAccessException;
import com.kanishka.virustotalv2.VirusTotalConfig;
import com.kanishka.virustotalv2.VirustotalPublicV2;
import com.kanishka.virustotalv2.VirustotalPublicV2Impl;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class VirusTotalClientImpl implements InvestigationClient {
    private static final Logger logger = LoggerFactory.getLogger(VirusTotalClientImpl.class);
    FeatureServiceConfiguration conf = null;
    private Pattern hashPattern = Pattern.compile("^[a-z0-9]+$");
    private static final int NUM_RETRIES = 3;
    private static final int RETRY_WAIT_MS = 10000;

    public VirusTotalClientImpl(FeatureServiceConfiguration conf) {
        this.conf = conf;
    }

    @Override
    public Map<String, String> getHashInfo(String hash) {
        Map<String, String> results = null;
        String apiKey = conf.getInvestigationApiKey();
        if (apiKey == null) {
            logger.warn("Attempt to get hash info, but no api key is set in the configuration");
        } else if (!hashPattern.matcher(hash).matches()) {
            logger.warn("Attempt to get hash info on invalid hash [" + hash + "]");
        } else {
            VirusTotalConfig.getConfigInstance().setVirusTotalAPIKey(apiKey);
            VirustotalPublicV2 virusTotalRef = null;
            try {
                virusTotalRef = new VirustotalPublicV2Impl();

                logger.info("Requesting VirusTotal report on md5 [" + hash + "]");
                results = getHashInfoInner(hash, virusTotalRef);
            } catch (APIKeyNotFoundException e) {
                logger.error("Error initializing VirusTotal client" + e.getLocalizedMessage(), e);
            } catch (QuotaExceededException e) {
                logger.error("VirusTotal quota exceeded: " + e.getLocalizedMessage(), e);
                results = new HashMap<String, String>();
                results.put(CLIENT_ERROR, "Request rate for MD5 information exceeded");
                results.put(CLIENT_ERROR_CODE, "429");
            } catch (UnauthorizedAccessException e) {
                logger.error("Cannot access VirusTotal", e);
            } catch (UnsupportedEncodingException e) {
                logger.error("Exception accessing VirusTotal", e);
            }
        }

        return results;
    }

    @VisibleForTesting
    public Map<String, String> getHashInfoInner(String hash, VirustotalPublicV2 virusTotalRef) throws UnsupportedEncodingException, UnauthorizedAccessException, QuotaExceededException {
        Map<String, String> results = new HashMap<String, String>();

        int responseCode = -1;
        FileScanReport report = null;
        for (int tries = 0; tries<NUM_RETRIES; tries++) {
            try {
                report = virusTotalRef.getScanReport(hash);
                responseCode = report.getResponseCode();
            } catch (NullPointerException npe) {
                logger.warn("Got NullPointerException from VirusTotal client for hash [" + hash + "] try [" + tries + "]");
                try {
                    Thread.sleep(RETRY_WAIT_MS);
                } catch (InterruptedException e) {
                    logger.error("Interrupted Virus Total call", e);
                }
            }
            if (responseCode != -1) {
                break;
            }
        }
        if (responseCode == -1) {
            // We failed all the tries. I don't know for sure that this is actually the result of exceeding the
            // request limit - that's supposed to result in a different exception.  But it seems consistent with
            // what we see, so that's what we're going to send back
            results.put(CLIENT_ERROR, "Request rate for MD5 information exceeded");
            results.put(CLIENT_ERROR_CODE, "429");
        } else {
            results.put(SOURCE, "VirusTotal");
            if (report.getMd5() != null) {
                results.put(MD5, report.getMd5());
            } else {
                // When the md5 is unknown, the client returns null for the md5 - presumably because it doesn't know if it's
                // an md5, sha1, etc.  But we do, so fill in that return value.
                results.put(MD5, hash);
            }
            results.put(RESPONSE_SUMMARY, responseCode == 0 ? NO_ISSUES : MALWARE_INDICATED);
            results.put(DATE, report.getScanDate());
            results.put(RESPONSE_CODE, String.valueOf(responseCode));
            results.put(MESSAGE, report.getVerboseMessage());
            results.put(EXTERNAL_LINK, report.getPermalink());
            int positives = 0;
            int total = 0;
            // We only call getPositives() and getTotal() because the client doesn't react gracefully when
            // there are no results.
            if (responseCode != 0) {
                positives = report.getPositives();
                total = report.getTotal();
            }
            results.put(POSITIVES, String.valueOf(positives));
            results.put(TOTAL, String.valueOf(total));
            // Calculate percentage of virus checkers that found an issue
            double percentage = 0.0;
            if (total > 0) {
                percentage = ((double) positives * 100.0) / (double) total;
            }
            results.put(CONFIDENCE_LEVEL, Double.toString(percentage));
        }

        return results;
    }

}
