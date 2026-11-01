package com.example.data.models

data class ProphetStory(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val titleAr: String,
    val summaryAr: String,
    val fullStoryAr: String,
    val versesMentioned: String,
    val chronologicalOrder: Int = 1,
    val keyLessons: String = "",
    val miraclesAr: String = ""
)

data class TawbahStory(
    val id: String,
    val titleAr: String,
    val personAr: String,
    val detailsAr: String,
    val sourceAr: String,
    val moralLesson: String = ""
)

data class CapitalInfo(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val countryAr: String,
    val countryEn: String,
    val currencyAr: String,
    val population: String,
    val utcOffset: String,
    val howIslamArrivedAr: String,
    val latitude: Double,
    val longitude: Double
)

data class BattleInfo(
    val id: String,
    val nameAr: String,
    val yearH: String,
    val yearG: String,
    val leaderMuslims: String,
    val sidesAr: String,
    val detailsAr: String,
    val resultAr: String,
    val causesAr: String = "",
    val eventsAr: String = "",
    val lessonsAr: String = "",
    val quranVerses: String = "",
    val isMajor: Boolean = false
)

data class InventionInfo(
    val id: String,
    val nameAr: String,
    val inventorAr: String,
    val placeAndEraAr: String,
    val howItWorksAr: String,
    val impactAr: String,
    val fieldAr: String = "عام"
)

data class IslamicQA(
    val id: String,
    val questionAr: String,
    val answerAr: String,
    val categoryAr: String,
    val evidenceAr: String = "",
    val isCustom: Boolean = false
)

data class GeneralQA(
    val id: String,
    val questionAr: String,
    val answerAr: String,
    val categoryAr: String,
    val hintAr: String = "",
    val difficultyAr: String = "متوسط"
)

data class AppSectionModel(
    val id: String,
    val titleAr: String,
    val subtitleAr: String,
    val isEnabled: Boolean = true,
    val isCustom: Boolean = false
)

data class WallpaperModel(
    val id: String,
    val nameAr: String,
    val descriptionAr: String,
    val colorHex: Long,
    val imageUrl: String? = null,
    val isPreset: Boolean = true
)

data class AdhanAudioModel(
    val id: String,
    val nameAr: String,
    val descriptionAr: String,
    val audioUrl: String,
    val category: String = "adhan", // adhan, friday, reminder, salawat
    val isPreset: Boolean = true
)
