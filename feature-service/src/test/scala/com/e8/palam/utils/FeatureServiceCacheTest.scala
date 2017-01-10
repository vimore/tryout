package com.e8.palam.utils

import com.e8.palam.TestBase
import org.junit.{Before, Test}
import org.junit.Assert._


class FeatureServiceCacheTest extends TestBase {

  @Before
  override def setup() = {
    super.setup()
  }

  @Test
  def testSecurityEventCounts(): Unit = {
    val securityEventMap = cache.getSecurityEventDataMap
    val modelsMap = cache.getModelsMap

    val secEventConfigs = securityEventMap.values.flatten
    // This will change any time we add a new card so this test will need to be updated
    assert(secEventConfigs.size == 161)

    val modelGroups = secEventConfigs.groupBy(_.getModel)
    // We should have some cards for every model, so the number of model groups here should match the
    // number of models.
    // Note: We've removed the old tanium model (model 5) from securityEvents.yml, but it's still in modelsMap.
    // So, while it's kind of gross, subtract it from the size to do this comparison
    assert(modelGroups.size == modelsMap.size - 1)

    // Check to see that we have the expected number of cards for each model
    // Again, every time we add new cards to a model, we will need changes to expectedNumberOfCards
    val expectedNumberOfCards = collection.immutable.HashMap(0->1, 1->14, 2->22, 3->31, 4->22, 5->1, 6->31, 7->37, 8->1, 9->1, 10->1)
    modelGroups.keys.foreach(key =>
      if (key == 0) {
        assert(modelGroups(key).size == expectedNumberOfCards(key))
      } else if (key == 1) {
        assert(modelGroups(key).size == expectedNumberOfCards(key))
      } else if (key == 2) {
        assert(modelGroups(key).size == expectedNumberOfCards(key))
      } else if (key == 3) {
        assert(modelGroups(key).size == expectedNumberOfCards(key))
      } else if (key == 4) {
        assert(modelGroups(key).size == expectedNumberOfCards(key))
      } else if (key == 5) {
        assert(modelGroups(key).size == expectedNumberOfCards(key))
      } else if (key == 6) {
        assert(modelGroups(key).size == expectedNumberOfCards(key))
      } else if (key == 7) {
        assert(modelGroups(key).size == expectedNumberOfCards(key))
      } else if (key == 8) {
        assert(modelGroups(key).size == expectedNumberOfCards(key))
      } else if (key == 9) {
        assert(modelGroups(key).size == expectedNumberOfCards(key))
      } else if (key == 10) {
        assert(modelGroups(key).size == expectedNumberOfCards(key))
      } else {
        assertTrue("Got unexpected model group key [" + key + "]", false)
      }
    )

    assert(securityEventMap.size == 68)
  }

  @Test
  def testCefGetHostname(): Unit = {
    val securityEventMap = cache.getSecurityEventDataMap

    println(cache.getCefConfiguration.getDvcHost)

    //assert(securityEventMap.size == 68)
  }


}
