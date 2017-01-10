package com.e8.resources;

import com.e8.palam.TestBase;
import com.kanishka.virustotal.dto.FileScanReport;
import com.kanishka.virustotal.exception.QuotaExceededException;
import com.kanishka.virustotal.exception.UnauthorizedAccessException;
import com.kanishka.virustotalv2.VirustotalPublicV2;
import com.securityx.modelfeature.dao.investigation.InvestigationClient;
import com.securityx.modelfeature.dao.investigation.VirusTotalClientImpl;
import org.junit.Test;
import static junit.framework.TestCase.assertEquals;
import org.mockito.Mockito;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static org.mockito.Mockito.when;

public class InvestigationFeatureTest extends TestBase {

    @Test
    public void virusTotalTest() throws UnsupportedEncodingException, UnauthorizedAccessException, QuotaExceededException {
        // Scan for an md5 that has no issues
        FileScanReport fsrForTesting = Mockito.mock(FileScanReport.class);
        when(fsrForTesting.getMd5()).thenReturn("a24099cc63c9499b80a362885483d4e6");
        when(fsrForTesting.getResponseCode()).thenReturn(0);
        when(fsrForTesting.getScanDate()).thenReturn("2016-07-04 12:54:50");
        when(fsrForTesting.getVerboseMessage()).thenReturn("verbose message");
        when(fsrForTesting.getPermalink()).thenReturn("http://somelink");
        when(fsrForTesting.getPositives()).thenReturn(0);
        when(fsrForTesting.getTotal()).thenReturn(40);

        // Scan for an md5 that has issues
        FileScanReport fsrWithIssues = Mockito.mock(FileScanReport.class);
        when(fsrWithIssues.getMd5()).thenReturn("99017f6eebbac24f351415dd410d522d");
        when(fsrWithIssues.getResponseCode()).thenReturn(1);
        when(fsrWithIssues.getScanDate()).thenReturn("2016-07-05 10:54:50");
        when(fsrWithIssues.getVerboseMessage()).thenReturn("Scan finished, information embedded");
        when(fsrWithIssues.getPermalink()).thenReturn("https://www.virustotal.com/file/52d3df0ed60c46f336c131bf2ca454f73bafdc4b04dfa2aea80746f5ba9e6d1c/analysis/1467716090/");
        when(fsrWithIssues.getPositives()).thenReturn(48);
        when(fsrWithIssues.getTotal()).thenReturn(54);

        VirustotalPublicV2 forTesting = Mockito.mock(VirustotalPublicV2.class);
        when(forTesting.getScanReport("a24099cc63c9499b80a362885483d4e6")).thenReturn(fsrForTesting);
        when(forTesting.getScanReport("99017f6eebbac24f351415dd410d522d")).thenReturn(fsrWithIssues);

        VirusTotalClientImpl virusTotalClient = new VirusTotalClientImpl(getConf());
        Map<String, String> results = virusTotalClient.getHashInfoInner("a24099cc63c9499b80a362885483d4e6", forTesting);
        assertEquals("VirusTotal", results.get(InvestigationClient.SOURCE));
        assertEquals("a24099cc63c9499b80a362885483d4e6", results.get(InvestigationClient.MD5));
        assertEquals(InvestigationClient.NO_ISSUES, results.get(InvestigationClient.RESPONSE_SUMMARY));
        assertEquals(0.0d, Double.parseDouble(results.get(InvestigationClient.CONFIDENCE_LEVEL)), 0.01);

        Map<String, String> resultsWithIssues = virusTotalClient.getHashInfoInner("99017f6eebbac24f351415dd410d522d", forTesting);
        assertEquals("VirusTotal", resultsWithIssues.get(InvestigationClient.SOURCE));
        assertEquals("99017f6eebbac24f351415dd410d522d", resultsWithIssues.get(InvestigationClient.MD5));
        assertEquals(InvestigationClient.MALWARE_INDICATED, resultsWithIssues.get(InvestigationClient.RESPONSE_SUMMARY));
        assertEquals(88.88d, Double.parseDouble(resultsWithIssues.get(InvestigationClient.CONFIDENCE_LEVEL)), 0.01);
    }
}
