package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class CustomContentManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("salati_custom_content_v2", Context.MODE_PRIVATE)

    // 1. App Sections
    private val _sections = MutableStateFlow<List<AppSectionModel>>(loadSections())
    val sections: StateFlow<List<AppSectionModel>> = _sections.asStateFlow()

    // 2. Wallpapers
    private val _wallpapers = MutableStateFlow<List<WallpaperModel>>(loadWallpapers())
    val wallpapers: StateFlow<List<WallpaperModel>> = _wallpapers.asStateFlow()

    // 3. Sounds & Adhan
    private val _adhanAudios = MutableStateFlow<List<AdhanAudioModel>>(loadAdhanAudios())
    val adhanAudios: StateFlow<List<AdhanAudioModel>> = _adhanAudios.asStateFlow()

    // 4. Tawbah Stories
    private val _tawbahStories = MutableStateFlow<List<TawbahStory>>(loadTawbahStories())
    val tawbahStories: StateFlow<List<TawbahStory>> = _tawbahStories.asStateFlow()

    // 5. Prophet Stories
    private val _prophetStories = MutableStateFlow<List<ProphetStory>>(loadProphetStories())
    val prophetStories: StateFlow<List<ProphetStory>> = _prophetStories.asStateFlow()

    // 6. Islamic Q&A
    private val _islamicQAs = MutableStateFlow<List<IslamicQA>>(loadIslamicQAs())
    val islamicQAs: StateFlow<List<IslamicQA>> = _islamicQAs.asStateFlow()

    // ==========================================
    // 1. SECTIONS MANAGEMENT
    // ==========================================
    private fun getDefaultSections(): List<AppSectionModel> = listOf(
        AppSectionModel("main_prayer", "مواقيت الصلاة والعد التنازلي", "عرض الصلوات الخمس والشروق مع الوقت المتبقي لأقرب صلاة", isEnabled = true),
        AppSectionModel("adhan_audio", "الأذان والتنبيهات الصوتية", "اختيار المؤذن، تكرار الأذان، وأذان الجمعة الأول والثاني", isEnabled = true),
        AppSectionModel("athkar_duas", "الأذكار والأدعية الشاملة", "جهة كاملة للأذكار وجهة كاملة للأدعية مع عداد الاستغفار والتسبيح المدمج", isEnabled = true),
        AppSectionModel("quran", "القرآن الكريم والتلاوات", "قراءة السور وتلاوات كبار القراء وتفسير الآيات", isEnabled = true),
        AppSectionModel("ghazwat", "السرد الكامل لغزوات النبي ﷺ", "27 غزوة كاملة والفتوحات الإسلامية الكبرى", isEnabled = true),
        AppSectionModel("qibla", "بوصلة اتجاه القبلة", "تحديد زاوية الكعبة المشرفة بدقة بواسطة مستشعر البوصلة", isEnabled = true),
        AppSectionModel("calendar", "التقويم الهجري والميلادي", "مواقيت الشهر كاملة وأطوار القمر والمناسبات الإسلامية", isEnabled = true),
        AppSectionModel("on_this_day", "في مثل هذا اليوم إسلامياً", "أبرز الأحداث التاريخية والمعارك في تاريخ اليوم", isEnabled = true),
        AppSectionModel("widgets", "استوديو الويدجت الـ 5", "ويدجت الصلاة والتقويم والسبحة والحديث الشريف", isEnabled = true)
    )

    private fun loadSections(): List<AppSectionModel> {
        val jsonStr = prefs.getString("custom_sections_v2", null) ?: return getDefaultSections()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<AppSectionModel>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.getString("id")
                // Exclude removed sections (knowledge and standalone tasbih)
                if (id == "knowledge" || id == "tasbih") continue
                list.add(
                    AppSectionModel(
                        id = id,
                        titleAr = obj.getString("titleAr"),
                        subtitleAr = obj.optString("subtitleAr", ""),
                        isEnabled = obj.optBoolean("isEnabled", true),
                        isCustom = obj.optBoolean("isCustom", false)
                    )
                )
            }
            if (list.none { it.id == "athkar_duas" }) {
                list.add(2, AppSectionModel("athkar_duas", "الأذكار والأدعية الشاملة", "جهة كاملة للأذكار وجهة كاملة للأدعية مع عداد الاستغفار والتسبيح المدمج", isEnabled = true))
            }
            if (list.isEmpty()) getDefaultSections() else list
        } catch (e: Exception) {
            getDefaultSections()
        }
    }

    private fun saveSections(list: List<AppSectionModel>) {
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("titleAr", item.titleAr)
            obj.put("subtitleAr", item.subtitleAr)
            obj.put("isEnabled", item.isEnabled)
            obj.put("isCustom", item.isCustom)
            array.put(obj)
        }
        prefs.edit().putString("custom_sections_v2", array.toString()).apply()
        _sections.value = list
    }

    fun toggleSection(id: String, enabled: Boolean) {
        val updated = _sections.value.map {
            if (it.id == id) it.copy(isEnabled = enabled) else it
        }
        saveSections(updated)
    }

    fun updateSection(id: String, title: String, subtitle: String) {
        val updated = _sections.value.map {
            if (it.id == id) it.copy(titleAr = title.trim(), subtitleAr = subtitle.trim()) else it
        }
        saveSections(updated)
    }

    fun deleteSection(id: String) {
        val updated = _sections.value.filter { it.id != id }
        saveSections(updated)
    }

    fun addCustomSection(title: String, subtitle: String) {
        val newSection = AppSectionModel(
            id = "custom_sec_" + UUID.randomUUID().toString().take(8),
            titleAr = title.trim(),
            subtitleAr = subtitle.trim(),
            isEnabled = true,
            isCustom = true
        )
        val updated = _sections.value + newSection
        saveSections(updated)
    }

    fun resetSections() {
        saveSections(getDefaultSections())
    }

    // ==========================================
    // 2. WALLPAPERS MANAGEMENT
    // ==========================================
    private fun getDefaultWallpapers(): List<WallpaperModel> = listOf(
        WallpaperModel("emerald", "الزمرد النبوي الملكي", "لون أخضر داكن مهيب مع لمسات ذهبية وقباب إسلامية", 0xFF064E3B),
        WallpaperModel("gold", "الذهب الكلاسيكي الفاخر", "طابع مذهب فاخر مستوحى من كسوة الكعبة المشرفة", 0xFFB45309),
        WallpaperModel("midnight", "سماء الليل النجمية", "كحلي داكن هادئ ومريح للعين مع ضوء القمر والنجوم", 0xFF0F172A),
        WallpaperModel("desert", "رمال الصحراء الدافئة", "درجات دافئة ترابية أصيلة من ربوع شبه الجزيرة العربية", 0xFF78350F),
        WallpaperModel("marble", "رخام المسجد النبوي الأبيض", "رخام ناصع مع نقوش نبوية وزخارف كلاسيكية راقية", 0xFFE2E8F0),
        WallpaperModel("andalus", "الأندلس الأزرق العتيق", "نقوش قصر الحمراء وزخارف مائية مريحة للروح", 0xFF1E3A8A),
        WallpaperModel("quds", "قباب القدس الشريف", "طابع المسجد الأقصى المبارك وقبة الصخرة المشرفة", 0xFF14532D)
    )

    private fun loadWallpapers(): List<WallpaperModel> {
        val jsonStr = prefs.getString("custom_wallpapers_v2", null) ?: return getDefaultWallpapers()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<WallpaperModel>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    WallpaperModel(
                        id = obj.getString("id"),
                        nameAr = obj.getString("nameAr"),
                        descriptionAr = obj.optString("descriptionAr", ""),
                        colorHex = obj.optLong("colorHex", 0xFF064E3B),
                        imageUrl = obj.optString("imageUrl").takeIf { !it.isNullOrBlank() },
                        isPreset = obj.optBoolean("isPreset", false)
                    )
                )
            }
            if (list.isEmpty()) getDefaultWallpapers() else list
        } catch (e: Exception) {
            getDefaultWallpapers()
        }
    }

    private fun saveWallpapers(list: List<WallpaperModel>) {
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("nameAr", item.nameAr)
            obj.put("descriptionAr", item.descriptionAr)
            obj.put("colorHex", item.colorHex)
            obj.put("imageUrl", item.imageUrl ?: "")
            obj.put("isPreset", item.isPreset)
            array.put(obj)
        }
        prefs.edit().putString("custom_wallpapers_v2", array.toString()).apply()
        _wallpapers.value = list
    }

    fun addCustomWallpaper(name: String, description: String, colorHex: Long, imageUrl: String? = null) {
        val newWp = WallpaperModel(
            id = "custom_wp_" + UUID.randomUUID().toString().take(8),
            nameAr = name.trim(),
            descriptionAr = description.trim(),
            colorHex = colorHex,
            imageUrl = imageUrl?.trim()?.takeIf { it.isNotEmpty() },
            isPreset = false
        )
        val updated = _wallpapers.value + newWp
        saveWallpapers(updated)
    }

    fun updateWallpaper(id: String, name: String, description: String, colorHex: Long, imageUrl: String? = null) {
        val updated = _wallpapers.value.map {
            if (it.id == id) {
                it.copy(
                    nameAr = name.trim(),
                    descriptionAr = description.trim(),
                    colorHex = colorHex,
                    imageUrl = imageUrl?.trim()?.takeIf { u -> u.isNotEmpty() }
                )
            } else it
        }
        saveWallpapers(updated)
    }

    fun deleteWallpaper(id: String) {
        val updated = _wallpapers.value.filter { it.id != id }
        saveWallpapers(updated)
    }

    fun resetWallpapers() {
        saveWallpapers(getDefaultWallpapers())
    }

    // ==========================================
    // 3. SOUNDS & ADHAN MANAGEMENT
    // ==========================================
    private fun getDefaultAdhanAudios(): List<AdhanAudioModel> = listOf(
        AdhanAudioModel("makkah", "أذان الحرم المكي الشريف", "تسجيل ندي ومؤثر من مكة المكرمة", "https://cdn.aladhan.com/audio/adhans/makkah.mp3", "adhan"),
        AdhanAudioModel("madinah", "أذان المسجد النبوي بالمدينة", "أذان المدينة المنورة الشريفة", "https://cdn.aladhan.com/audio/adhans/madinah.mp3", "adhan"),
        AdhanAudioModel("quds", "أذان المسجد الأقصى المبارك", "نداء القدس الشريف بروحانية عالية وخشوع", "https://cdn.aladhan.com/audio/adhans/aqsa.mp3", "adhan"),
        AdhanAudioModel("afasy", "أذان الشيخ مشاري العفاسي", "تلاوة وأذان هادئ خاشع بصوت ندي", "https://cdn.aladhan.com/audio/adhans/afasy.mp3", "adhan"),
        AdhanAudioModel("abdulbasit", "أذان الشيخ عبد الباسط عبد الصمد", "تسجيل الإذاعة المصرية التاريخي النادر", "https://cdn.aladhan.com/audio/adhans/abdulbasit.mp3", "adhan"),
        AdhanAudioModel("friday_1", "أذان الجمعة الأول", "أذان التنبيه الأول لصلاة الجمعة المباركة", "https://cdn.aladhan.com/audio/adhans/friday1.mp3", "friday"),
        AdhanAudioModel("friday_2", "أذان الجمعة الثاني", "أذان صعود الخطيب للمنبر في الجمعة", "https://cdn.aladhan.com/audio/adhans/makkah.mp3", "friday"),
        AdhanAudioModel("pre_prayer_ayah", "تنبيه: إن الصلاة كانت على المؤمنين كتاباً موقوتاً", "تنبيه صوتي مؤثر قبل موعد الصلاة", "https://cdn.aladhan.com/audio/adhans/reminder.mp3", "reminder"),
        AdhanAudioModel("pre_takbeer", "تكبيرات التهيئة قبل الصلاة", "نداء تهيئة خاشع للدخول في الصلاة", "https://cdn.aladhan.com/audio/adhans/takbeer.mp3", "reminder"),
        AdhanAudioModel("iqama_takbeer", "إقامة الصلاة (قد قامت الصلاة)", "تكبيرات وإقامة الصلاة بصوت خاشع", "https://cdn.aladhan.com/audio/adhans/iqama.mp3", "iqama"),
        AdhanAudioModel("iqama_calm", "تنبيه الإقامة الهادئ", "نغمة روحانية هادئة للتذكير بالإقامة بعد الصلاة", "https://cdn.aladhan.com/audio/adhans/iqama_calm.mp3", "iqama"),
        AdhanAudioModel("athkar_morning_voice", "تنبيه أذكار الصباح", "أصبحنا وأصبح الملك لله والحمد لله", "https://cdn.aladhan.com/audio/adhans/athkar_morning.mp3", "athkar"),
        AdhanAudioModel("athkar_evening_voice", "تنبيه أذكار المساء", "أمسينا وأمسى الملك لله والحمد لله", "https://cdn.aladhan.com/audio/adhans/athkar_evening.mp3", "athkar"),
        AdhanAudioModel("salawat_nabawi", "الصلاة على النبي ﷺ", "صوت ندي خاشع: اللهم صل وسلم على نبينا محمد", "https://cdn.aladhan.com/audio/adhans/salawat.mp3", "salawat")
    )

    private fun loadAdhanAudios(): List<AdhanAudioModel> {
        val jsonStr = prefs.getString("custom_adhans_v2", null) ?: return getDefaultAdhanAudios()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<AdhanAudioModel>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    AdhanAudioModel(
                        id = obj.getString("id"),
                        nameAr = obj.getString("nameAr"),
                        descriptionAr = obj.optString("descriptionAr", ""),
                        audioUrl = obj.getString("audioUrl"),
                        category = obj.optString("category", "adhan"),
                        isPreset = obj.optBoolean("isPreset", false)
                    )
                )
            }
            if (list.isEmpty()) getDefaultAdhanAudios() else list
        } catch (e: Exception) {
            getDefaultAdhanAudios()
        }
    }

    private fun saveAdhanAudios(list: List<AdhanAudioModel>) {
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("nameAr", item.nameAr)
            obj.put("descriptionAr", item.descriptionAr)
            obj.put("audioUrl", item.audioUrl)
            obj.put("category", item.category)
            obj.put("isPreset", item.isPreset)
            array.put(obj)
        }
        prefs.edit().putString("custom_adhans_v2", array.toString()).apply()
        _adhanAudios.value = list
    }

    fun addCustomSound(name: String, description: String, audioUrl: String, category: String) {
        val newSound = AdhanAudioModel(
            id = "custom_snd_" + UUID.randomUUID().toString().take(8),
            nameAr = name.trim(),
            descriptionAr = description.trim(),
            audioUrl = audioUrl.trim(),
            category = category,
            isPreset = false
        )
        val updated = _adhanAudios.value + newSound
        saveAdhanAudios(updated)
    }

    fun updateSound(id: String, name: String, description: String, audioUrl: String, category: String) {
        val updated = _adhanAudios.value.map {
            if (it.id == id) {
                it.copy(
                    nameAr = name.trim(),
                    descriptionAr = description.trim(),
                    audioUrl = audioUrl.trim(),
                    category = category
                )
            } else it
        }
        saveAdhanAudios(updated)
    }

    fun deleteSound(id: String) {
        val updated = _adhanAudios.value.filter { it.id != id }
        saveAdhanAudios(updated)
    }

    fun resetSounds() {
        saveAdhanAudios(getDefaultAdhanAudios())
    }

    // ==========================================
    // 4. TAWBAH STORIES MANAGEMENT
    // ==========================================
    private fun loadTawbahStories(): List<TawbahStory> {
        val jsonStr = prefs.getString("custom_tawbah_v2", null)
        if (jsonStr == null) {
            return IslamicDataset54.stories30
        }
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<TawbahStory>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    TawbahStory(
                        id = obj.getString("id"),
                        titleAr = obj.getString("titleAr"),
                        personAr = obj.getString("personAr"),
                        detailsAr = obj.getString("detailsAr"),
                        sourceAr = obj.optString("sourceAr", "سير أعلام النبلاء"),
                        moralLesson = obj.optString("moralLesson", "عظمة رحمة الله ومغفرته")
                    )
                )
            }
            if (list.isEmpty()) IslamicDataset54.stories30 else list
        } catch (e: Exception) {
            IslamicDataset54.stories30
        }
    }

    private fun saveTawbahStories(list: List<TawbahStory>) {
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("titleAr", item.titleAr)
            obj.put("personAr", item.personAr)
            obj.put("detailsAr", item.detailsAr)
            obj.put("sourceAr", item.sourceAr)
            obj.put("moralLesson", item.moralLesson)
            array.put(obj)
        }
        prefs.edit().putString("custom_tawbah_v2", array.toString()).apply()
        _tawbahStories.value = list
    }

    fun addCustomTawbahStory(title: String, person: String, details: String, source: String = "كتاب التوابين", moralLesson: String = "باب التوبة مفتوح لكل عبد") {
        val newStory = TawbahStory(
            id = "custom_tw_" + UUID.randomUUID().toString().take(8),
            titleAr = title.trim(),
            personAr = person.trim(),
            detailsAr = details.trim(),
            sourceAr = source.trim(),
            moralLesson = moralLesson.trim()
        )
        val updated = listOf(newStory) + _tawbahStories.value
        saveTawbahStories(updated)
    }

    fun updateTawbahStory(id: String, title: String, person: String, details: String, source: String, moralLesson: String) {
        val updated = _tawbahStories.value.map {
            if (it.id == id) {
                it.copy(
                    titleAr = title.trim(),
                    personAr = person.trim(),
                    detailsAr = details.trim(),
                    sourceAr = source.trim(),
                    moralLesson = moralLesson.trim()
                )
            } else it
        }
        saveTawbahStories(updated)
    }

    fun deleteTawbahStory(id: String) {
        val updated = _tawbahStories.value.filter { it.id != id }
        saveTawbahStories(updated)
    }

    fun resetTawbahStories() {
        saveTawbahStories(IslamicDataset54.stories30)
    }

    // ==========================================
    // 5. PROPHET STORIES MANAGEMENT
    // ==========================================
    private fun loadProphetStories(): List<ProphetStory> {
        val jsonStr = prefs.getString("custom_prophets_v2", null)
        if (jsonStr == null) {
            return FullChroniclesData.completeProphetStories
        }
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<ProphetStory>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ProphetStory(
                        id = obj.getString("id"),
                        nameAr = obj.getString("nameAr"),
                        nameEn = obj.optString("nameEn", "Prophet"),
                        titleAr = obj.optString("titleAr", "نبي الله ورسوله"),
                        summaryAr = obj.optString("summaryAr", ""),
                        fullStoryAr = obj.getString("fullStoryAr"),
                        versesMentioned = obj.optString("versesMentioned", "القرآن الكريم"),
                        chronologicalOrder = obj.optInt("chronologicalOrder", 99),
                        keyLessons = obj.optString("keyLessons", ""),
                        miraclesAr = obj.optString("miraclesAr", "")
                    )
                )
            }
            if (list.isEmpty()) FullChroniclesData.completeProphetStories else list
        } catch (e: Exception) {
            FullChroniclesData.completeProphetStories
        }
    }

    private fun saveProphetStories(list: List<ProphetStory>) {
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("nameAr", item.nameAr)
            obj.put("nameEn", item.nameEn)
            obj.put("titleAr", item.titleAr)
            obj.put("summaryAr", item.summaryAr)
            obj.put("fullStoryAr", item.fullStoryAr)
            obj.put("versesMentioned", item.versesMentioned)
            obj.put("chronologicalOrder", item.chronologicalOrder)
            obj.put("keyLessons", item.keyLessons)
            obj.put("miraclesAr", item.miraclesAr)
            array.put(obj)
        }
        prefs.edit().putString("custom_prophets_v2", array.toString()).apply()
        _prophetStories.value = list
    }

    fun addCustomProphetStory(
        nameAr: String,
        titleAr: String,
        verses: String,
        miracles: String,
        lessons: String,
        fullStory: String
    ) {
        val newProphet = ProphetStory(
            id = "custom_pr_" + UUID.randomUUID().toString().take(8),
            nameAr = nameAr.trim(),
            nameEn = nameAr.trim(),
            titleAr = titleAr.trim(),
            summaryAr = fullStory.take(150) + "...",
            fullStoryAr = fullStory.trim(),
            versesMentioned = verses.trim().ifEmpty { "القرآن الكريم" },
            chronologicalOrder = (_prophetStories.value.maxOfOrNull { it.chronologicalOrder } ?: 25) + 1,
            keyLessons = lessons.trim(),
            miraclesAr = miracles.trim()
        )
        val updated = _prophetStories.value + newProphet
        saveProphetStories(updated)
    }

    fun updateProphetStory(
        id: String,
        nameAr: String,
        titleAr: String,
        summaryAr: String,
        fullStoryAr: String,
        miraclesAr: String,
        keyLessons: String
    ) {
        val updated = _prophetStories.value.map {
            if (it.id == id) {
                it.copy(
                    nameAr = nameAr.trim(),
                    titleAr = titleAr.trim(),
                    summaryAr = summaryAr.trim(),
                    fullStoryAr = fullStoryAr.trim(),
                    miraclesAr = miraclesAr.trim(),
                    keyLessons = keyLessons.trim()
                )
            } else it
        }
        saveProphetStories(updated)
    }

    fun deleteProphetStory(id: String) {
        val updated = _prophetStories.value.filter { it.id != id }
        saveProphetStories(updated)
    }

    fun resetProphetStories() {
        saveProphetStories(FullChroniclesData.completeProphetStories)
    }

    // ==========================================
    // 6. ISLAMIC Q&A MANAGEMENT
    // ==========================================
    private fun loadIslamicQAs(): List<IslamicQA> {
        val jsonStr = prefs.getString("custom_qa_v3", null)
        if (jsonStr == null) {
            return QuestionsDataset.religiousQuestions
        }
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<IslamicQA>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    IslamicQA(
                        id = obj.getString("id"),
                        questionAr = obj.getString("questionAr"),
                        answerAr = obj.getString("answerAr"),
                        categoryAr = obj.optString("categoryAr", "فقه وعقيدة"),
                        evidenceAr = obj.optString("evidenceAr", ""),
                        isCustom = obj.optBoolean("isCustom", false)
                    )
                )
            }
            if (list.isEmpty()) QuestionsDataset.religiousQuestions else list
        } catch (e: Exception) {
            QuestionsDataset.religiousQuestions
        }
    }

    private fun saveIslamicQAs(list: List<IslamicQA>) {
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("questionAr", item.questionAr)
            obj.put("answerAr", item.answerAr)
            obj.put("categoryAr", item.categoryAr)
            obj.put("evidenceAr", item.evidenceAr)
            obj.put("isCustom", item.isCustom)
            array.put(obj)
        }
        prefs.edit().putString("custom_qa_v2", array.toString()).apply()
        _islamicQAs.value = list
    }

    fun addCustomQA(question: String, answer: String, category: String, evidence: String = "") {
        val newQA = IslamicQA(
            id = "custom_qa_" + UUID.randomUUID().toString().take(8),
            questionAr = question.trim(),
            answerAr = answer.trim(),
            categoryAr = category.trim().ifEmpty { "فقه وعقيدة" },
            evidenceAr = evidence.trim(),
            isCustom = true
        )
        val updated = listOf(newQA) + _islamicQAs.value
        saveIslamicQAs(updated)
    }

    fun deleteQA(id: String) {
        val updated = _islamicQAs.value.filter { it.id != id }
        saveIslamicQAs(updated)
    }

    fun resetQAs() {
        saveIslamicQAs(QuestionsDataset.religiousQuestions)
    }
}
