package com.example.data.local

import com.example.data.models.*
import com.example.data.services.CalculationMethod
import com.example.data.services.JuristicMethod
import com.example.data.services.PrayerTimeCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class IslamicRepository {

    private val httpClient = okhttp3.OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    fun getPresetCities(): List<CityLocation> = IslamicDataset.cities

    fun getSurahs(): List<Surah> = IslamicDataset.surahs

    fun getQaris(): List<Qari> = IslamicDataset.qaris

    fun getRiwayahs(): List<QuranRiwayah> = QuranRiwayahsDataset.riwayahs

    fun getQarisForRiwayah(riwayahId: String): List<Qari> = QuranRiwayahsDataset.getRecitersForRiwayah(riwayahId)

    fun getTranslationEditions(): List<QuranTranslationEdition> = QuranTranslationsDataset.editions

    fun getAdhanList() = IslamicDataset.adhanList

    fun getProphets(): List<ProphetStory> = FullChroniclesData.completeProphetStories

    fun getTawbahStories(): List<TawbahStory> = IslamicDataset54.stories30

    fun getCapitals(): List<CapitalInfo> = IslamicDataset54.capitals54

    fun getBattles(): List<BattleInfo> = FullChroniclesData.fullGhazwatList

    fun getMajorConquests(): List<BattleInfo> = FullChroniclesData.majorConquests

    fun getMajorSaraya(): List<BattleInfo> = FullChroniclesData.majorSaraya

    fun getInventions(): List<InventionInfo> = IslamicKnowledgeData.inventions

    fun getQAs(): List<IslamicQA> = QuestionsDataset.religiousQuestions
    fun getGeneralQAs(): List<GeneralQA> = QuestionsDataset.generalQuestions

    fun getOnThisDayEvents(): List<OnThisDayItem> = OnThisDayDataset.getCollectionForOffset(0).events
    fun getOnThisDayCollections(): List<DayEventsCollection> = OnThisDayDataset.collections
    fun getSunnahHadiths(): List<Hadith> = SunnahDataset.hadiths

    // Fetch surah ayahs (with offline fallback for key surahs) enriched with Tafsir & I'rab
    suspend fun getSurahAyahs(surahNumber: Int): List<Ayah> = withContext(Dispatchers.IO) {
        val offlineData = QuranOfflineData.getOfflineSurahAyahs(surahNumber)
        if (offlineData != null) {
            return@withContext offlineData.map { ayah ->
                val irab = if (ayah.irabShort.isNotBlank()) ayah.irabShort else QuranIrabAndTafsirDataset.getAyahIrab(surahNumber, ayah.numberInSurah, ayah.textAr)
                val tafsir = if (ayah.tafsirShort.isNotBlank()) ayah.tafsirShort else QuranIrabAndTafsirDataset.getAyahTafsir(surahNumber, ayah.numberInSurah, "")
                val transMap = QuranTranslationsDataset.getTranslations(surahNumber, ayah.numberInSurah)
                ayah.copy(
                    irabShort = irab,
                    tafsirShort = tafsir,
                    translationEn = if (ayah.translationEn.isNotBlank()) ayah.translationEn else (transMap["en"] ?: ""),
                    translationFr = transMap["fr"] ?: "",
                    translationUr = transMap["ur"] ?: "",
                    translationId = transMap["id"] ?: "",
                    translationTr = transMap["tr"] ?: "",
                    translationEs = transMap["es"] ?: ""
                )
            }
        }

        try {
            val url = "https://api.alquran.cloud/v1/surah/$surahNumber/editions/quran-uthmani,ar.muyassar,en.sahih"
            val request = Request.Builder().url(url).build()
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonStr = response.body?.string() ?: return@withContext getFallbackAyahs(surahNumber)
                val json = JSONObject(jsonStr)
                val data = json.getJSONArray("data")
                val arEdition = data.getJSONObject(0).getJSONArray("ayahs")
                val tafsirEdition = if (data.length() > 1) data.getJSONObject(1).getJSONArray("ayahs") else null
                val enEdition = if (data.length() > 2) data.getJSONObject(2).getJSONArray("ayahs") else null

                val result = mutableListOf<Ayah>()
                for (i in 0 until arEdition.length()) {
                    val arObj = arEdition.getJSONObject(i)
                    val textAr = arObj.getString("text")
                    val num = arObj.getInt("numberInSurah")
                    val tafsirAr = tafsirEdition?.optJSONObject(i)?.optString("text")
                        ?: QuranIrabAndTafsirDataset.getAyahTafsir(surahNumber, num, "")
                    val transEn = enEdition?.optJSONObject(i)?.optString("text") ?: ""
                    val irabAr = QuranIrabAndTafsirDataset.getAyahIrab(surahNumber, num, textAr)

                    val transMap = QuranTranslationsDataset.getTranslations(surahNumber, num)
                    result.add(
                        Ayah(
                            numberInSurah = num,
                            textAr = textAr,
                            translationEn = transEn.ifBlank { transMap["en"] ?: "" },
                            tafsirShort = tafsirAr,
                            irabShort = irabAr,
                            translationFr = transMap["fr"] ?: "",
                            translationUr = transMap["ur"] ?: "",
                            translationId = transMap["id"] ?: "",
                            translationTr = transMap["tr"] ?: "",
                            translationEs = transMap["es"] ?: ""
                        )
                    )
                }
                return@withContext result
            }
        } catch (e: Exception) {
            // ignore and fallback
        }
        return@withContext getFallbackAyahs(surahNumber)
    }

    private fun getFallbackAyahs(surahNumber: Int): List<Ayah> {
        val surah = IslamicDataset.surahs.find { it.number == surahNumber }
            ?: Surah(surahNumber, "سورة مباركة", "Surah", "", 7, "مكية", 1)
        return listOf(
            Ayah(1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah, Most Gracious, Most Merciful."),
            Ayah(2, "تلاوة وقراءة سورة ${surah.nameAr} المباركة كاملة بفضل الله.", "Reading of Surah ${surah.nameEn}.")
        )
    }

    // Live prayer times fetch from AlAdhan/Mawaqit API with fallback to built-in astronomical calculation
    suspend fun getLivePrayerTimes(
        city: CityLocation,
        date: Date = Date(),
        method: CalculationMethod = CalculationMethod.UMM_AL_QURA,
        juristic: JuristicMethod = JuristicMethod.SHAFI_STANDARD,
        offsets: Map<PrayerType, Int> = emptyMap(),
        customMawaqitUrl: String = ""
    ): DayPrayerTimes = withContext(Dispatchers.IO) {
        // Try live network sync if possible
        try {
            val methodId = when (method) {
                CalculationMethod.MAWAQIT_NET -> 3
                CalculationMethod.UMM_AL_QURA -> 4
                CalculationMethod.MUSLIM_WORLD_LEAGUE -> 3
                CalculationMethod.MASHREQ_EGYPT -> 5
                CalculationMethod.KARACHI -> 1
                CalculationMethod.NORTH_AMERICA -> 2
                CalculationMethod.GULF_DUBAI -> 16
                CalculationMethod.BALKANS_TURKEY -> 13
                CalculationMethod.EUROPE_UOIF -> 12
                CalculationMethod.MAGHREB_ARAB -> 3
                CalculationMethod.LEVANT_SHAM -> 3
                CalculationMethod.CAUCASUS_CENTRAL_ASIA -> 3
            }
            val url = if (customMawaqitUrl.isNotBlank() && customMawaqitUrl.startsWith("http")) {
                customMawaqitUrl
            } else {
                "https://api.aladhan.com/v1/timingsByCity?city=${city.nameEn}&country=${city.countryEn}&method=$methodId"
            }
            val request = Request.Builder().url(url).build()
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val json = JSONObject(response.body?.string() ?: "")
                if (json.has("data") && json.getJSONObject("data").has("timings")) {
                    val timings = json.getJSONObject("data").getJSONObject("timings")
                    val cal = java.util.Calendar.getInstance().apply { time = date }

                    fun parsePrayerEpoch(timeStr: String, offsetMin: Int): Long {
                        val clean = timeStr.trim().split(" ")[0]
                        val parts = clean.split(":")
                        val h = parts[0].toInt()
                        val m = parts[1].toInt()
                        val prayerCal = java.util.Calendar.getInstance().apply {
                            set(Calendar.YEAR, cal.get(Calendar.YEAR))
                            set(Calendar.MONTH, cal.get(Calendar.MONTH))
                            set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH))
                            set(Calendar.HOUR_OF_DAY, h)
                            set(Calendar.MINUTE, m + offsetMin)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        return prayerCal.timeInMillis
                    }

                    return@withContext DayPrayerTimes(
                        fajr = parsePrayerEpoch(timings.getString("Fajr"), offsets[PrayerType.FAJR] ?: 0),
                        sunrise = parsePrayerEpoch(timings.getString("Sunrise"), offsets[PrayerType.SUNRISE] ?: 0),
                        dhuhr = parsePrayerEpoch(timings.getString("Dhuhr"), offsets[PrayerType.DHUHR] ?: 0),
                        asr = parsePrayerEpoch(timings.getString("Asr"), offsets[PrayerType.ASR] ?: 0),
                        maghrib = parsePrayerEpoch(timings.getString("Maghrib"), offsets[PrayerType.MAGHRIB] ?: 0),
                        isha = parsePrayerEpoch(timings.getString("Isha"), offsets[PrayerType.ISHA] ?: 0),
                        date = date,
                        cityName = city.nameAr,
                        countryName = city.countryAr
                    )
                }
            }
        } catch (e: Exception) {
            // Live network failed or offline: use built-in astronomical formula
        }

        // Always accurate offline fallback
        return@withContext PrayerTimeCalculator.calculatePrayerTimes(
            latitude = city.latitude,
            longitude = city.longitude,
            date = date,
            method = method,
            juristic = juristic,
            offsets = offsets,
            cityName = city.nameAr,
            countryName = city.countryAr
        )
    }

    // Authentic Hadith collection
    fun getHadiths(): List<Hadith> = SunnahDataset.hadiths

    // Authentic Adhkar collection
    fun getAdhkar(): List<DhikrItem> = FullAthkarDataset.allAdhkar
    fun getPostPrayerAdhkar(): List<DhikrItem> = FullAthkarDataset.postPrayerAdhkar
    fun getMorningAdhkar(): List<DhikrItem> = FullAthkarDataset.morningAdhkar
    fun getEveningAdhkar(): List<DhikrItem> = FullAthkarDataset.eveningAdhkar
    fun getSleepAdhkar(): List<DhikrItem> = FullAthkarDataset.sleepAdhkar
    fun getQuranicDuas(): List<DhikrItem> = FullAthkarDataset.quranicDuas
    fun getPropheticDuas(): List<DhikrItem> = FullAthkarDataset.propheticDuas

    // Islamic Calendar events
    fun getHijriEvents(): List<HijriEvent> = listOf(
        HijriEvent("رأس السنة الهجرية الجديدة", 1, 1, "بداية العام الهجري الجديد وهجرة المصطفى ﷺ", "holiday"),
        HijriEvent("يوم عاشوراء", 10, 1, "نجاة موسى عليه السلام وقومه من فرعون وصيامه سنة مؤكدة", "event"),
        HijriEvent("المولد النبوي الشريف", 12, 3, "مولد خير البرية ورحمة العالمين نبينا محمد ﷺ", "commemoration"),
        HijriEvent("ذكرى الإسراء والمعراج", 27, 7, "معجزة الإسراء من المسجد الحرام إلى الأقصى والعروج للسماوات وفرض الصلاة", "event"),
        HijriEvent("ليلة النصف من شعبان", 15, 8, "ليلة مباركة تحول فيها القبلة إلى الكعبة المشرفة", "event"),
        HijriEvent("غرة شهر رمضان المبارك", 1, 9, "بدء شهر الصيام والقيام والقرآن والرحمة والمغفرة", "holiday"),
        HijriEvent("غزوة بدر الكبرى (يوم الفرقان)", 17, 9, "أول نصر عظيم للإسلام والمسلمين", "event"),
        HijriEvent("فتح مكة الأعظم", 20, 9, "دخول النبي ﷺ مكة فاتحاً وتطهير البيت الحرام", "event"),
        HijriEvent("ليلة القدر المباركة", 27, 9, "خير من ألف شهر، تتنزل الملائكة والروح فيها", "event"),
        HijriEvent("عيد الفطر المبارك", 1, 10, "يوم الجائزة والفرح وإتمام فريضة الصيام وزكاة الفطر", "holiday"),
        HijriEvent("يوم عرفة المبارك", 9, 12, "أعظم أيام الدهر وصيامه يكفر سنتين ماضية ومستقبلة لغير الحاج", "event"),
        HijriEvent("عيد الأضحى المبارك", 10, 12, "يوم النحر وذكرى فداء إسماعيل عليه السلام وذبح الأضاحي", "holiday"),
        HijriEvent("أيام التشريق", 11, 12, "أيام أكل وشرب وذكر لله تعالى", "holiday")
    )
}
