package com.example.data.models

/**
 * Model representing a Quran translation language edition (طبعات وتراجم معاني القرآن الكريم)
 */
data class QuranTranslationEdition(
    val code: String, // en, fr, ur, id, tr, es, de
    val languageNameAr: String,
    val languageNameNative: String,
    val translatorName: String,
    val flagEmoji: String
)
