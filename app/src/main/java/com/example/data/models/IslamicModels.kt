package com.example.data.models

data class Surah(
    val number: Int,
    val nameAr: String,
    val nameEn: String,
    val translation: String,
    val versesCount: Int,
    val revelationType: String, // مكية / مدنية
    val pageNumber: Int
)

data class Ayah(
    val numberInSurah: Int,
    val textAr: String,
    val translationEn: String = "",
    val tafsirShort: String = "",
    val irabShort: String = "",
    val translationFr: String = "",
    val translationUr: String = "",
    val translationId: String = "",
    val translationTr: String = "",
    val translationEs: String = "",
    val riwayahTextOverride: String? = null
) {
    fun getTranslation(code: String): String {
        val result = when (code.lowercase()) {
            "fr" -> translationFr
            "ur" -> translationUr
            "id" -> translationId
            "tr" -> translationTr
            "es" -> translationEs
            else -> translationEn
        }
        return if (result.isNotBlank()) result else translationEn
    }
}

data class Qari(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val serverUrl: String,
    val riwayahId: String = "hafs"
)

data class Hadith(
    val id: Int,
    val bookAr: String,
    val bookEn: String,
    val chapterAr: String,
    val narratorAr: String,
    val textAr: String,
    val explanationAr: String = "",
    val gradeAr: String = "صحيح"
)

data class DhikrItem(
    val id: String,
    val category: String, // morning, evening, prayer, sleep, general
    val textAr: String,
    val benefitAr: String,
    val targetCount: Int,
    val sourceAr: String = ""
)

data class HijriEvent(
    val titleAr: String,
    val hijriDay: Int,
    val hijriMonth: Int,
    val descriptionAr: String,
    val category: String // holiday, event, commemoration
)
