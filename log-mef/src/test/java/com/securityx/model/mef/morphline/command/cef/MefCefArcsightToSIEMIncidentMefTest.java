package com.securityx.model.mef.morphline.command.cef;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class MefCefArcsightToSIEMIncidentMefTest extends LogCollectionAbstractTest {

    private Logger logger = LoggerFactory.getLogger(MefCefArcsightToSIEMIncidentMefTest.class);

    public MefCefArcsightToSIEMIncidentMefTest() {
        super(MefCefArcsightToSIEMIncidentMefTest.class.toString());
        this.morphlineId = "cefincidentfile";
        //this.morphlineId = "ceffromsyslogmissinghost";
        this.confFile = "siemincidentmef-cef.conf";
    }

    @Test
    public void test0() throws FileNotFoundException {
        String line = "CEF:0|ArcSight|ArcSight|6.0.0.1333.0|rule:105|Possible Network Sweep|High| eventId=-2305843008255647244 type=2 start=1449176771000 end=1449176781000 mrt=1449176832393 sessionId=0 generatorID=3zlyoaVEBABCAA5yQuWRKBA\\=\\= categoryBehavior=/Execute/Response categoryDeviceGroup=/Security Information Manager modelConfidence=0 severity=10 relevance=10 assetCriticality=0 priority=9 art=1449176814276 cat=/Rule/Fire deviceSeverity=Warning rt=1449176781806 src=65.85.126.60 sourceZoneID=MFRvYWj4BABCCAoMqnujYjg\\=\\= sourceZoneURI=/All Zones/Custom Zones/Terminal Server sourceGeoCountryCode=IN slong=78.4553694444 slat=20.5080833333 dpt=22 fname=Possible Network Sweep filePath=/All Rules/Real-time Rules/Intrusion Monitoring/Worm Outbreak/Possible Network Sweep fileType=Rule cnt=20 ruleThreadId=UMyqaVEBABDzqAm2yfxISA\\=\\= cs2=<Resource URI\\=\"/All Rules/Real-time Rules/Intrusion Monitoring/Worm Outbreak/Possible Network Sweep\" ID\\=\"5VWTDCf8AABCSRjFkAIznMA\\=\\=\"/> c6a4=fe80:0:0:0:2073:3a99:96b0:491b locality=1 cs2Label=Configuration Resource c6a4Label=Agent IPv6 Address ahost=ESM60c agt=10.160.142.12 av=5.2.5.6403.0 atz=America/Los_Angeles aid=3zlyoaVEBABCAA5yQuWRKBA\\=\\= at=superagent_ng dvchost=ESM60c dvc=10.160.142.12 deviceZoneID=MFRvYWj4BABCCAoMqnujYjg\\=\\= deviceZoneURI=/All Zones/Custom Zones/Terminal Server deviceAssetId=4tSCTm0IBABDgy99le+embw\\=\\= dtz=America/Los_Angeles deviceFacility=Rules Engine eventAnnotationStageUpdateTime=1449176832847 eventAnnotationModificationTime=1449176832847 eventAnnotationAuditTrail=1,1449176832847,root,Queued,,,,\\n1,1449170786694,root,Queued,,,,\\n eventAnnotationVersion=1 eventAnnotationFlags=0 eventAnnotationEndTime=1449176781000 eventAnnotationManagerReceiptTime=1449176832393 _cefVer=0.1 ad.arcSightEventPath=32JezIVEBABCJFtP3axXseg\\=\\=";
        Record r = new Record();
        r.put("message", line);
        boolean result = doTest(r);

        assertEquals(true, result);
        Record out = outCommand.getRecord(0);
        assertEquals("_cefVer", "0.1", out.get("_cefVer").get(0));
        assertEquals("ad.arcSightEventPath", "32JezIVEBABCJFtP3axXseg\\=\\=", out.get("ad.arcSightEventPath").get(0));
        assertEquals("agentAddress", "10.160.142.12", out.get("agentAddress").get(0));
        assertEquals("agentHostName", "ESM60c", out.get("agentHostName").get(0));
        assertEquals("agt", "10.160.142.12", out.get("agt").get(0));
        assertEquals("ahost", "ESM60c", out.get("ahost").get(0));
        assertEquals("aid", "3zlyoaVEBABCAA5yQuWRKBA\\=\\=", out.get("aid").get(0));
        assertEquals("art", "1449176814276", out.get("art").get(0));
        assertEquals("assetCriticality", "0", out.get("assetCriticality").get(0));
        assertEquals("at", "superagent_ng", out.get("at").get(0));
        assertEquals("atz", "America/Los_Angeles", out.get("atz").get(0));
        assertEquals("av", "5.2.5.6403.0", out.get("av").get(0));
        assertEquals("baseEventCount", 20, out.get("baseEventCount").get(0));
        assertEquals("c6a4", "fe80:0:0:0:2073:3a99:96b0:491b", out.get("c6a4").get(0));
        assertEquals("c6a4Label", "Agent IPv6 Address", out.get("c6a4Label").get(0));
        assertEquals("cat", "/Rule/Fire", out.get("cat").get(0));
        assertEquals("categoryBehavior", "/Execute/Response", out.get("categoryBehavior").get(0));
        assertEquals("categoryDeviceGroup", "/Security Information Manager", out.get("categoryDeviceGroup").get(0));
        assertEquals("cefDeviceProduct", "ArcSight", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "ArcSight", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "6.0.0.1333.0", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventName", "Possible Network Sweep", out.get("cefEventName").get(0));
        assertEquals("cefEventSeverity", "High", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "rule:105", out.get("cefSignatureId").get(0));
        assertEquals("cnt", "20", out.get("cnt").get(0));
        assertEquals("cs2", "<Resource URI\\=\"/All Rules/Real-time Rules/Intrusion Monitoring/Worm Outbreak/Possible Network Sweep\" ID\\=\"5VWTDCf8AABCSRjFkAIznMA\\=\\=\"/>", out.get("cs2").get(0));
        assertEquals("cs2Label", "Configuration Resource", out.get("cs2Label").get(0));
        assertEquals("destinationPort", 22, out.get("destinationPort").get(0));
        assertEquals("deviceAddress", "10.160.142.12", out.get("deviceAddress").get(0));
        assertEquals("deviceAssetId", "4tSCTm0IBABDgy99le+embw\\=\\=", out.get("deviceAssetId").get(0));
        assertEquals("deviceEventCategory", "/Rule/Fire", out.get("deviceEventCategory").get(0));
        assertEquals("deviceFacility", "Rules Engine", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "ESM60c", out.get("deviceHostName").get(0));
        assertEquals("deviceReceiptTime", 1449176781806L, out.get("deviceReceiptTime").get(0));
        assertEquals("deviceSeverity", "Warning", out.get("deviceSeverity").get(0));
        assertEquals("deviceZoneID", "MFRvYWj4BABCCAoMqnujYjg\\=\\=", out.get("deviceZoneID").get(0));
        assertEquals("deviceZoneURI", "/All Zones/Custom Zones/Terminal Server", out.get("deviceZoneURI").get(0));
        assertEquals("dpt", "22", out.get("dpt").get(0));
        assertEquals("dtz", "America/Los_Angeles", out.get("dtz").get(0));
        assertEquals("dvc", "10.160.142.12", out.get("dvc").get(0));
        assertEquals("dvchost", "ESM60c", out.get("dvchost").get(0));
        assertEquals("end", "1449176781000", out.get("end").get(0));
        assertEquals("endTime", 1449176781000L, out.get("endTime").get(0));
        assertEquals("eventAnnotationAuditTrail", "1,1449176832847,root,Queued,,,,\\n1,1449170786694,root,Queued,,,,\\n", out.get("eventAnnotationAuditTrail").get(0));
        assertEquals("eventAnnotationEndTime", "1449176781000", out.get("eventAnnotationEndTime").get(0));
        assertEquals("eventAnnotationFlags", "0", out.get("eventAnnotationFlags").get(0));
        assertEquals("eventAnnotationManagerReceiptTime", "1449176832393", out.get("eventAnnotationManagerReceiptTime").get(0));
        assertEquals("eventAnnotationModificationTime", "1449176832847", out.get("eventAnnotationModificationTime").get(0));
        assertEquals("eventAnnotationStageUpdateTime", "1449176832847", out.get("eventAnnotationStageUpdateTime").get(0));
        assertEquals("eventAnnotationVersion", "1", out.get("eventAnnotationVersion").get(0));
        assertEquals("eventId", -2305843008255647244L, out.get("eventId").get(0));
        assertEquals("filePath", "/All Rules/Real-time Rules/Intrusion Monitoring/Worm Outbreak/Possible Network Sweep", out.get("filePath").get(0));
        assertEquals("fileType", "Rule", out.get("fileType").get(0));
        assertEquals("fname", "Possible Network Sweep", out.get("fname").get(0));
        assertEquals("generatorID", "3zlyoaVEBABCAA5yQuWRKBA\\=\\=", out.get("generatorID").get(0));
        assertEquals("locality", "1", out.get("locality").get(0));
        assertEquals("logSourceType", "SIEMIncidentMef", out.get("logSourceType").get(0));
        assertEquals("modelConfidence", "0", out.get("modelConfidence").get(0));
        assertEquals("mrt", "1449176832393", out.get("mrt").get(0));
        assertEquals("priority", "9", out.get("priority").get(0));
        assertEquals("relevance", "10", out.get("relevance").get(0));
        assertEquals("rt", "1449176781806", out.get("rt").get(0));
        assertEquals("ruleThreadId", "UMyqaVEBABDzqAm2yfxISA\\=\\=", out.get("ruleThreadId").get(0));
        assertEquals("sessionId", "0", out.get("sessionId").get(0));
        assertEquals("severity", "10", out.get("severity").get(0));
        assertEquals("slat", "20.5080833333", out.get("slat").get(0));
        assertEquals("slong", "78.4553694444", out.get("slong").get(0));
        assertEquals("sourceAddress", "65.85.126.60", out.get("sourceAddress").get(0));
        assertEquals("sourceGeoCountryCode", "IN", out.get("sourceGeoCountryCode").get(0));
        assertEquals("sourceZoneID", "MFRvYWj4BABCCAoMqnujYjg\\=\\=", out.get("sourceZoneID").get(0));
        assertEquals("sourceZoneURI", "/All Zones/Custom Zones/Terminal Server", out.get("sourceZoneURI").get(0));
        assertEquals("src", "65.85.126.60", out.get("src").get(0));
        assertEquals("start", "1449176771000", out.get("start").get(0));
        assertEquals("startTime", 1449176771000L, out.get("startTime").get(0));
        assertEquals("type", 2, out.get("type").get(0));
    }



}
