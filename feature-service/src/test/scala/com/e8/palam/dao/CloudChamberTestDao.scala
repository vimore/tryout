package com.e8.palam.dao

import com.e8.palam.TestBase
import com.e8.test.MockCloudChamberServer
import com.securityx.modelfeature.dao.CloudChamberDao
import org.junit.{After, Before, Test}


class CloudChamberTestDao extends TestBase{

  private var cloudChamberDao: CloudChamberDao = null
  private var mockServer: MockCloudChamberServer = null

  @Before
  override def setup() = {
    super.setup()
    conf.getCloudchamber.setHost("127.0.0.1")
    conf.getCloudchamber.setScheme("http")
    conf.getCloudchamber.setPort("8290")
    cloudChamberDao = new CloudChamberDao(conf)
    mockServer = new MockCloudChamberServer( conf.getCloudchamber.getHost, conf.getCloudchamber.getPort.toInt)
    mockServer.start()
  }

  @After
  def teardown() ={
    mockServer.stop()
  }
  @Test
  def testGetValidDomainCreationDate() ={
    val date = cloudChamberDao.getDomainCreationDate("www.amazon.com")
    assert(date != null)
    assert(!date.isEmpty)
    assert(date.equals("1994-11-01T05:00:00.000Z"))
  }

  @Test
  def testGetWhoisByDomain() = {
    val domainInfo = cloudChamberDao.getDomainWhois("www.amazon.com")
    assert(domainInfo != null)
    assert(!domainInfo.isEmpty)
    assert(domainInfo.contains("Amazon"))
  }

  @Test
  def testGetWhoisByIp() = {
    val domainInfo = cloudChamberDao.getWhoisByIp("74.125.239.110")
    assert(domainInfo != null)
    assert(!domainInfo.isEmpty)
    assert(domainInfo.contains("Google"))
  }

  @Test
  def testGetInValidDomainCreationDate() ={
    val date = cloudChamberDao.getDomainCreationDate("xec.esc")
    assert(date == null)
  }

  @Test
  def testGetCloudChamberStatus() ={
    val isUp = cloudChamberDao.getCloudChamberStatus()
    assert(isUp != false)
    assert(isUp == true)
  }
}
