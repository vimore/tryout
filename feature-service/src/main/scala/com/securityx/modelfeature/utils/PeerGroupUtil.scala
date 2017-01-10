package com.securityx.modelfeature.utils

import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{Map => MutableMap}

/**
 * Created by harish on 1/8/15.
 */
class PeerGroupUtil {

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[PeerGroupUtil])

  //Models
  private val adPeerGroupFacetMap: MutableMap[Int, AdFacetHelper] = MutableMap[Int, AdFacetHelper]()
  private val adNoveltyDetectorFacetMap: MutableMap[Int, AdNoveltyDectectorFacetHelper] = MutableMap[Int, AdNoveltyDectectorFacetHelper]()
  private val webPeerGroupFacetMap: MutableMap[Int, WebFacetHelper] = MutableMap[Int, WebFacetHelper]()
  private val webPeerGroupFacetMapImpala : MutableMap[Int, WebFacetHelper] = MutableMap[Int, WebFacetHelper]()

  object SortOrder extends Enumeration {
    type SortOder = Value
    val ASC, DESC = Value
  }

  populateMaps()


  def populateMaps() = {
    populateAdPeerGroupFacetMap()
    populateWebPeerGroupFacetMap()
    populateWebPeerGroupFacetMapImpala()
    populateAdNoveltyDetectorFacetMap()
  }

  //TODO: All of this can also be dumped into a configuration file.
  private def populateAdPeerGroupFacetMap() = {
    adPeerGroupFacetMap += 0 -> new AdFacetHelper("Security*4769*Success",Array("destinationServiceName"), null, "destinationUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 1 -> new AdFacetHelper("Security*4769*Success",Array("sourceAddress"), null, "destinationUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 2 -> new AdFacetHelper("Security*4769*Success",Array("sourceAddress"), null, "destinationUserName", SortOrder.DESC.toString)
    adPeerGroupFacetMap += 3 -> new AdFacetHelper("Security*4769*Failure",Array("destinationServiceName"), null, "destinationUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 4 -> new AdFacetHelper("Security*4769*Failure",Array("sourceAddress"), null, "destinationUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 5 -> new AdFacetHelper("Security*4769*Failure",Array("sourceAddress"), null, "destinationUserName", SortOrder.DESC.toString)
    adPeerGroupFacetMap += 6 -> new AdFacetHelper("Security*4624",Array("sourceAddress"), null, "destinationUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 7 -> new AdFacetHelper("Security*4624",Array("sourceUserName"), null, "destinationUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 8 -> new AdFacetHelper("Security*4624",Array("sourceAddress"), null, "destinationUserName", SortOrder.DESC.toString)
    adPeerGroupFacetMap += 9 -> new AdFacetHelper("Security*4624",Array("sourceAddress"), null, "sourceUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 10 -> new AdFacetHelper("Security*4624",Array("destinationUserName"), null, "sourceUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 11 -> new AdFacetHelper("Security*4624",Array("destinationUserName", "sourceAddress"), null, "sourceUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 12 -> new AdFacetHelper("Security*4625",Array("sourceAddress"), null, "destinationUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 13 -> new AdFacetHelper("Security*4625",Array("sourceUserName", "sourceAddress"), null, "destinationUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 14 -> new AdFacetHelper("Security*4625",Array("sourceAddress"), null, "destinationUserName", SortOrder.DESC.toString)
    adPeerGroupFacetMap += 15 -> new AdFacetHelper("Security*4625",Array("sourceAddress"), null, "destinationUserName", SortOrder.DESC.toString)
    adPeerGroupFacetMap += 16 -> new AdFacetHelper("Security*4625",Array("destinationUserName", "sourceAddress"), null, "sourceUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 17 -> new AdFacetHelper("Security*4625",Array("destinationUserName", "sourceAddress"), null, "destinationUserName", SortOrder.DESC.toString)
    adPeerGroupFacetMap += 18 -> new AdFacetHelper("Security*4768*Success",Array("sourceAddress"), null, "destinationUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 19 -> new AdFacetHelper("Security*4768*Success",Array("sourceAddress"), null, "destinationUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 20 -> new AdFacetHelper("Security*4768*Failure",Array("sourceAddress"), null, "destinationUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 21 -> new AdFacetHelper("Security*4768*Failure",Array("sourceAddress"), null, "destinationUserName", SortOrder.DESC.toString)
    adPeerGroupFacetMap += 22 -> new AdFacetHelper("Security*4672",Array("sourceAddress"), null, "destinationUserName", SortOrder.DESC.toString)
    adPeerGroupFacetMap += 23 -> new AdFacetHelper("Security*4648",Array("sourceUserName"), null, "destinationUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 24 -> new AdFacetHelper("Security*4648",Array("destinationNameOrIp"), null, "destinationUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 25 -> new AdFacetHelper("Security*4648",Array("sourceAddress"), null, "destinationUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 26 -> new AdFacetHelper("Security*4648",Array("sourceAddress"), null, "destinationUserName", SortOrder.DESC.toString)
    adPeerGroupFacetMap += 27 -> new AdFacetHelper("Security*4648",Array("destinationUserName"), null, "sourceUserName", SortOrder.DESC.toString)
    adPeerGroupFacetMap += 28 -> new AdFacetHelper("Security*4648",Array("destinationNameOrIp"), null, "sourceUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 29 -> new AdFacetHelper("Security*4648",Array("sourceAddress"), null, "sourceUserName", SortOrder.ASC.toString)
    adPeerGroupFacetMap += 30 -> new AdFacetHelper("Security*4648",Array("sourceAddress"), null, "sourceUserName", SortOrder.DESC.toString)

  }

  // Note: in items 2 and 13 below we are searching for "not-resolved", among other things.  But the '-' character is a solr reserved
  // character, and so it must be escaped by a \.
  private def populateWebPeerGroupFacetMap() = {
    webPeerGroupFacetMap += 0 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, null)
    webPeerGroupFacetMap += 1 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, "devicePolicyAction:(*DENIED* OR *denied* OR *block* OR *BLOCK*)")
    webPeerGroupFacetMap += 2 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, "deviceEventCategory:(*pending* OR *unavailable* OR *uncategorized* OR *unknown* OR *parked* OR *not\\-resolved*)")
    webPeerGroupFacetMap += 3 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, "deviceEventCategory:(*Suspicious* OR *suspicious* OR  *Malicious* OR *malicious* OR *unwanted* OR *Unwanted* OR *malware* OR *Malware* OR *malnet* OR *Malnet* OR *spy* OR *Spy* OR *hacking* OR *Hacking* OR *phishing* OR *Phishing* OR *questionable* OR *Questionable* OR *proxy\\-avoidence\\-and\\-anonym* OR *dynamic\\-dns*)")
    webPeerGroupFacetMap += 4 -> new WebFacetHelper(Array("destinationPort"),null, "sourceNameOrIp", SortOrder.DESC.toString, null)
    webPeerGroupFacetMap += 5 -> new WebFacetHelper( Array("destinationPort", "destinationNameOrIp"), null,"sourceNameOrIp", SortOrder.DESC.toString, "-destinationPort:80 AND -destinationPort:8080 AND -destinationPort:443")
    webPeerGroupFacetMap += 6 -> new WebFacetHelper( Array("destinationNameOrIp"), Array("bytesIn"), "sourceNameOrIp", SortOrder.DESC.toString, null)
    webPeerGroupFacetMap += 7 -> new WebFacetHelper( Array("destinationNameOrIp"), Array("bytesOut"), "sourceNameOrIp", SortOrder.DESC.toString, null)
    webPeerGroupFacetMap += 8 -> new WebFacetHelper(Array("destinationNameOrIp", "requestMethod"),null, "sourceNameOrIp", SortOrder.DESC.toString, "NOT requestMethod:(*POST* OR *post* OR *GET* OR *get*)")
    webPeerGroupFacetMap += 9 -> new WebFacetHelper(Array("destinationPort", "requestMethod"),null, "sourceNameOrIp", SortOrder.DESC.toString, null)
    webPeerGroupFacetMap += 10 -> new WebFacetHelper(Array("requestClientApplication"),null, "sourceNameOrIp", SortOrder.ASC.toString, null)
    webPeerGroupFacetMap += 11 -> new WebFacetHelper(Array("requestClientApplication"),null, "sourceNameOrIp", SortOrder.ASC.toString, null)
    webPeerGroupFacetMap += 12 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, "devicePolicyAction:(*DENIED* OR *denied* OR *block* OR *BLOCK*)")
    webPeerGroupFacetMap += 13 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, "deviceEventCategory:(*pending* OR *unavailable* OR *uncategorized* OR *unknown* OR *parked* OR *not\\-resolved*)")
    webPeerGroupFacetMap += 14 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, "deviceEventCategory:(*Suspicious* OR *suspicious* OR  *Malicious* OR *malicious* OR *unwanted* OR *Unwanted* OR *malware* OR *Malware* OR *malnet* OR *Malnet* OR *spy* OR *Spy* OR *hacking* OR *Hacking* OR *phishing* OR *Phishing* OR *questionable* OR *Questionable* OR *proxy\\-avoidence\\-and\\-anonym* OR *dynamic\\-dns*)")
    webPeerGroupFacetMap += 15 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, null)
    webPeerGroupFacetMap += 16 -> new WebFacetHelper(Array("destinationNameOrIp"),Array("bytesIn"), "sourceNameOrIp", SortOrder.DESC.toString, "requestMethod:(*POST* OR *post*)")
    webPeerGroupFacetMap += 17 -> new WebFacetHelper(Array("destinationNameOrIp"),Array("bytesOut"), "sourceNameOrIp", SortOrder.DESC.toString, "requestMethod:(*POST* OR *post*)")
    webPeerGroupFacetMap += 18 -> new WebFacetHelper(Array("cefSignatureId"),null, "sourceNameOrIp", SortOrder.DESC.toString, "cefSignatureId:*")
    webPeerGroupFacetMap += 19 -> new WebFacetHelper(Array("cefSignatureId"),null, "sourceNameOrIp", SortOrder.DESC.toString, "cefSignatureId:*")
    webPeerGroupFacetMap += 20 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, "cefSignatureId:(4*)")
    webPeerGroupFacetMap += 21 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, "cefSignatureId:(5*)")
  }

  private def populateWebPeerGroupFacetMapImpala() = {
    webPeerGroupFacetMapImpala += 0 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, null)
    webPeerGroupFacetMapImpala += 1 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, "devicePolicyAction IN ('DENIED', 'denied', 'block', 'BLOCK')")
    webPeerGroupFacetMapImpala += 2 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, "deviceEventCategory IN ('pending', 'unavailable', 'uncategorized','unknown', 'parked', 'not\\-resolved')")
    webPeerGroupFacetMapImpala += 3 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, "deviceEventCategory IN ('Suspicious', 'suspicious', 'Malicious', 'malicious', 'unwanted', 'Unwanted', 'malware', 'Malware', 'malnet', 'Malnet', 'spy', 'Spy', 'hacking','Hacking','phishing','Phishing','questionable','Questionable','proxy\\-avoidence\\-and\\-anonym','dynamic\\-dns')")
    webPeerGroupFacetMapImpala += 4 -> new WebFacetHelper(Array("destinationPort"),null, "sourceNameOrIp", SortOrder.DESC.toString, null)
    webPeerGroupFacetMapImpala += 5 -> new WebFacetHelper( Array("destinationPort", "destinationNameOrIp"), null,"sourceNameOrIp", SortOrder.DESC.toString, "destinationPort NOT IN (80, 8080, 443)")
    webPeerGroupFacetMapImpala += 6 -> new WebFacetHelper( Array("destinationNameOrIp"), Array("bytesIn"), "sourceNameOrIp", SortOrder.DESC.toString, null)
    webPeerGroupFacetMapImpala += 7 -> new WebFacetHelper( Array("destinationNameOrIp"), Array("bytesOut"), "sourceNameOrIp", SortOrder.DESC.toString, null)
    webPeerGroupFacetMapImpala += 8 -> new WebFacetHelper(Array("destinationNameOrIp", "requestMethod"),null, "sourceNameOrIp", SortOrder.DESC.toString, "NOT requestMethod IN ('POST','post','GET','get')")
    webPeerGroupFacetMapImpala += 9 -> new WebFacetHelper(Array("destinationPort", "requestMethod"),null, "sourceNameOrIp", SortOrder.DESC.toString, null)
    webPeerGroupFacetMapImpala += 10 -> new WebFacetHelper(Array("requestClientApplication"),null, "sourceNameOrIp", SortOrder.ASC.toString, null)
    webPeerGroupFacetMapImpala += 11 -> new WebFacetHelper(Array("requestClientApplication"),null, "sourceNameOrIp", SortOrder.ASC.toString, null)
    webPeerGroupFacetMapImpala += 12 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, "devicePolicyAction IN ('DENIED','denied','block','BLOCK')")
    webPeerGroupFacetMapImpala += 13 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, "deviceEventCategory IN ('pending','unavailable','uncategorized','unknown','parked','not\\-resolved')")
    webPeerGroupFacetMapImpala += 14 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, "deviceEventCategory IN ('Suspicious','suspicious','Malicious','malicious','unwanted','Unwanted','malware','Malware','malnet','Malnet','spy','Spy','hacking','Hacking','phishing','Phishing','questionable','Questionable','proxy\\-avoidence\\-and\\-anonym','dynamic\\-dns')")
    webPeerGroupFacetMapImpala += 15 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, null)
    webPeerGroupFacetMapImpala += 16 -> new WebFacetHelper(Array("destinationNameOrIp"),Array("bytesIn"), "sourceNameOrIp", SortOrder.DESC.toString, "requestMethod IN ('POST','post')")
    webPeerGroupFacetMapImpala += 17 -> new WebFacetHelper(Array("destinationNameOrIp"),Array("bytesOut"), "sourceNameOrIp", SortOrder.DESC.toString, "requestMethod IN ('POST','post')")
    webPeerGroupFacetMapImpala += 18 -> new WebFacetHelper(Array("cefSignatureId"),null, "sourceNameOrIp", SortOrder.DESC.toString, null)
    webPeerGroupFacetMapImpala += 19 -> new WebFacetHelper(Array("cefSignatureId"),null, "sourceNameOrIp", SortOrder.DESC.toString, null)
    webPeerGroupFacetMapImpala += 20 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, "cefSignatureId LIKE 4%")
    webPeerGroupFacetMapImpala += 21 -> new WebFacetHelper(Array("destinationNameOrIp"),null, "sourceNameOrIp", SortOrder.DESC.toString, "cefSignatureId LIKE 5%)")
  }

  private def populateAdNoveltyDetectorFacetMap() = {
    adNoveltyDetectorFacetMap += 501 -> new AdNoveltyDectectorFacetHelper("4624",Array("sourceLogonType"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 502 -> new AdNoveltyDectectorFacetHelper("4625",Array("sourceLogonType"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 503 -> new AdNoveltyDectectorFacetHelper("4625",Array("substatus"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 504 -> new AdNoveltyDectectorFacetHelper("4625",Array("substatus"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 505 -> new AdNoveltyDectectorFacetHelper("4768",Array("destinationServiceSecurityID"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 506 -> new AdNoveltyDectectorFacetHelper("4768",Array("status"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 507 -> new AdNoveltyDectectorFacetHelper("4768",Array("destinationServiceSecurityID", "status"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 508 -> new AdNoveltyDectectorFacetHelper("4769",Array("status"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 509 -> new AdNoveltyDectectorFacetHelper("4769",Array("status"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 510 -> new AdNoveltyDectectorFacetHelper("4769",Array("destinationServiceSecurityID", "status"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 511 -> new AdNoveltyDectectorFacetHelper("4672",Array("privileges"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 512 -> new AdNoveltyDectectorFacetHelper("4648",Array("sourceUserName"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 513 -> new AdNoveltyDectectorFacetHelper("4648",Array("destinationNameOrIp"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 514 -> new AdNoveltyDectectorFacetHelper("4648",Array("sourceUserName", "destinationNameOrIp"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 515 -> new AdNoveltyDectectorFacetHelper("4661",Array("destinationObjectType"), null, "sourceUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 516 -> new AdNoveltyDectectorFacetHelper("4661",Array("sourceProcessName"), null, "sourceUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 517 -> new AdNoveltyDectectorFacetHelper("4661",Array("desiredAccess"), null, "sourceUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 518 -> new AdNoveltyDectectorFacetHelper("4663",Array("destinationObjectType"), null, "sourceUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 519 -> new AdNoveltyDectectorFacetHelper("4663",Array("sourceProcessName"), null, "sourceUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 520 -> new AdNoveltyDectectorFacetHelper("4663",Array("desiredAccess"), null, "sourceUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 521 -> new AdNoveltyDectectorFacetHelper("4656",Array("destinationObjectType"), null, "sourceUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 522 -> new AdNoveltyDectectorFacetHelper("4656",Array("sourceProcessName"), null, "sourceUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 523 -> new AdNoveltyDectectorFacetHelper("4656",Array("desiredAccess"), null, "sourceUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 524 -> new AdNoveltyDectectorFacetHelper("4723",Array("sourceUserName", "sourceNtDomain"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 525 -> new AdNoveltyDectectorFacetHelper("4724",Array("sourceUserName", "sourceNtDomain"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 526 -> new AdNoveltyDectectorFacetHelper("4738",Array("sourceUserName", "sourceNtDomain"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 527 -> new AdNoveltyDectectorFacetHelper("4738",Array("adScriptPath"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 528 -> new AdNoveltyDectectorFacetHelper("4738",Array("adProfilePath"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 529 -> new AdNoveltyDectectorFacetHelper("4738",Array("adUserWorkstation"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 530 -> new AdNoveltyDectectorFacetHelper("4727",Array("cefSignatureId"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 531 -> new AdNoveltyDectectorFacetHelper("4728",Array("cefSignatureId"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 532 -> new AdNoveltyDectectorFacetHelper("4729",Array("adUserWorkstation"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 533 -> new AdNoveltyDectectorFacetHelper("4731",Array("cefSignatureId"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 534 -> new AdNoveltyDectectorFacetHelper("4732",Array("cefSignatureId"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 535 -> new AdNoveltyDectectorFacetHelper("4733",Array("cefSignatureId"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 536 -> new AdNoveltyDectectorFacetHelper("4756",Array("cefSignatureId"), null, "destinationUserName", SortOrder.DESC.toString)
    adNoveltyDetectorFacetMap += 537 -> new AdNoveltyDectectorFacetHelper("5140",Array("destinationShareName"), null, "sourceUserName", SortOrder.DESC.toString)

  }

  def getAdPeerGroupFacetMapMap() : MutableMap[Int, AdFacetHelper] = {
    adPeerGroupFacetMap
  }

  def getWebPeerGroupFacetMapMap() : MutableMap[Int, WebFacetHelper] = {
    webPeerGroupFacetMap
  }

  def getWebPeerGroupFacetImpalaMap() : MutableMap[Int, WebFacetHelper] = {
    webPeerGroupFacetMapImpala
  }

  def getAdNoveltyDetectorFacetMapMap() : MutableMap[Int, AdNoveltyDectectorFacetHelper] = {
    adNoveltyDetectorFacetMap
  }


}

case class PeerGroupFacetHelper(facetFields: Array[String], sumfields: Array[String], entityLabel: String, sortOrder: String){
}

class AdFacetHelper(cefSignatureId: String, facetFields: Array[String],sumfields: Array[String], entityLabel: String, sortOrder: String) extends PeerGroupFacetHelper(facetFields, sumfields, entityLabel, sortOrder) {
  val cefSignature: String = cefSignatureId
}

class AdNoveltyDectectorFacetHelper(cefSignatureId: String, facetFields: Array[String],sumfields: Array[String], entityLabel: String, sortOrder: String) extends PeerGroupFacetHelper(facetFields, sumfields, entityLabel, sortOrder) {
  val cefSignature: String = cefSignatureId
}

class WebFacetHelper(facetFields: Array[String], sumfields: Array[String], entityLabel: String, sortOrder: String, queryString: String) extends PeerGroupFacetHelper(facetFields, sumfields, entityLabel, sortOrder) {
 val query: String = queryString
}

