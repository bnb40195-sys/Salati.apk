package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testIslamicRepositoryDatasets_unifiedSectionsReady() {
    val repo = com.example.data.local.IslamicRepository()
    assertTrue("Battles should not be empty", repo.getBattles().isNotEmpty())
    assertTrue("Inventions should not be empty", repo.getInventions().isNotEmpty())
    assertTrue("Prophets should not be empty", repo.getProphets().isNotEmpty())
    assertTrue("Tawbah stories should not be empty", repo.getTawbahStories().isNotEmpty())
    assertTrue("Religious QAs should not be empty", repo.getQAs().isNotEmpty())
    assertTrue("General QAs should not be empty", repo.getGeneralQAs().isNotEmpty())
    assertTrue("Capitals should not be empty", repo.getCapitals().isNotEmpty())
    assertTrue("OnThisDay events should not be empty", repo.getOnThisDayEvents().isNotEmpty())
    assertTrue("Sunnah hadiths should not be empty", repo.getSunnahHadiths().isNotEmpty())
  }

  @Test
  fun testDrawerDestinations() {
    val destinations = com.example.ui.components.DrawerDestination.values()
    assertTrue(destinations.contains(com.example.ui.components.DrawerDestination.HOME))
    assertTrue(destinations.contains(com.example.ui.components.DrawerDestination.QURAN_AND_SUNNAH))
    assertTrue(destinations.contains(com.example.ui.components.DrawerDestination.QIBLA))
    assertTrue(destinations.contains(com.example.ui.components.DrawerDestination.SETTINGS))
    assertTrue(destinations.contains(com.example.ui.components.DrawerDestination.INVENTIONS_AND_BATTLES))
    assertTrue(destinations.contains(com.example.ui.components.DrawerDestination.PROPHETS_AND_TAWBAH))
    assertTrue(destinations.contains(com.example.ui.components.DrawerDestination.QUESTIONS_RELIGIOUS_AND_GENERAL))
    assertTrue(destinations.contains(com.example.ui.components.DrawerDestination.ATHKAR_DUAS))
  }

  @Test
  fun testRecitationEvaluation_detectsErrorsInRed() {
    val expected = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
    // User recited with an error on the last word
    val recited = "بسم الله الرحمن العظيم"
    val result = com.example.data.services.QuranRecitationEvaluator.evaluate(expected, recited)
    assertEquals(4, result.totalWords)
    assertEquals(3, result.correctWordsCount)
    assertEquals(1, result.errorWordsCount)
    assertTrue("Accuracy should be 75%", result.accuracyPercentage in 70..80)
    // Check word-by-word flags
    assertTrue("First word correct", result.words[0].isCorrect)
    assertTrue("Second word correct", result.words[1].isCorrect)
    assertTrue("Third word correct", result.words[2].isCorrect)
    assertFalse("Fourth word wrong (should be highlighted red)", result.words[3].isCorrect)
  }
}
