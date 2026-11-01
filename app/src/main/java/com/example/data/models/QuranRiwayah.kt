package com.example.data.models

/**
 * Model representing canonical Quranic Riwayahs (روايات ومصاحف القرآن الكريم المتواترة)
 */
data class QuranRiwayah(
    val id: String, // hafs, warsh, qalun, duri, susi, shubah
    val nameAr: String,
    val nameEn: String,
    val rawiName: String, // اسم الراوي
    val imamName: String, // اسم الإمام القارئ
    val regionAr: String, // أماكن الانتشار
    val scriptTypeAr: String, // نوع الخط والمصحف
    val historicalBrief: String, // نبذة تاريخية
    val keyRules: List<String>, // أبرز القواعد والأصول (النقل، الإمالة، المدود، الإدغام...)
    val basmalahRule: String, // حكم البسملة بين السورتين وفي الفاتحة
    val defaultQariId: String // القارئ الافتراضي للرواية
)

/**
 * Difference in reading / text between Riwayahs for a specific Ayah (فرش الحروف وفروق القراءات)
 */
data class RiwayahDifference(
    val surahNumber: Int,
    val ayahNumber: Int,
    val hafsText: String,
    val riwayahText: String,
    val riwayahId: String,
    val explanationAr: String,
    val ruleCategoryAr: String // فرش حروف، إمالة، نقل، إدغام، مد بدل، تغليظ لام...
)
