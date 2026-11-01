package com.example.data.local

import com.example.data.models.Qari
import com.example.data.models.QuranRiwayah
import com.example.data.models.RiwayahDifference

object QuranRiwayahsDataset {

    val riwayahs = listOf(
        QuranRiwayah(
            id = "hafs",
            nameAr = "رواية حفص عن عاصم",
            nameEn = "Hafs 'an 'Asim",
            rawiName = "الإمام أبو عمر حفص بن سليمان بن المغيرة الأسدي الكوفي (ت 180 هـ)",
            imamName = "الإمام عاصم بن بهدلة أبي النَّجود الأسدي الكوفي (ت 127 هـ)",
            regionAr = "المشرق العربي والعالم الإسلامي كافة (المصحف الأكثر انتشاراً)",
            scriptTypeAr = "الرسم العثماني - ضبط مجمع الملك فهد لطباعة المصحف الشريف",
            historicalBrief = "أوسع القراءات والروايات انتشاراً في العالم الإسلامي اليوم. أخذ عاصم عن أبي عبد الرحمن السلمي عن عثمان وعلي وابن مسعود وأبي بن كعب عن رسول الله ﷺ.",
            keyRules = listOf(
                "تحقيق الهمزات المفردة والمزدوجة إلا في مواضع يسيرة محددة.",
                "قراءة {مَالِكِ يَوْمِ الدِّينِ} بإثبات الألف بعد الميم.",
                "توسط المد المتصل والمنفصل (4 أو 5 حركات من الشاطبية).",
                "قراءة {الصِّرَاطَ} بالصاد الخالصة دون إشمام أو إبدال بالسين.",
                "إثبات البسملة كآية أولى في سورة الفاتحة وعدّها سبع آيات بجمع الأخيرة."
            ),
            basmalahRule = "تعد البسملة آية مستقلة في أول سورة الفاتحة (الآية 1)، وتُقرأ في أوائل سائر السور عدا سورة التوبة.",
            defaultQariId = "alafasy"
        ),
        QuranRiwayah(
            id = "warsh",
            nameAr = "رواية ورش عن نافع",
            nameEn = "Warsh 'an Nafi'",
            rawiName = "الإمام أبو سعيد عثمان بن سعيد المصري الملقب بـ «ورش» (ت 197 هـ)",
            imamName = "الإمام نافع بن عبد الرحمن بن أبي نعيم المدني (ت 169 هـ)",
            regionAr = "المغرب العربي (المغرب، الجزائر، موريتانيا) وغرب ووسط إفريقيا",
            scriptTypeAr = "الرسم العثماني - الخط المغربي الأصيل",
            historicalBrief = "رواية أهل المدينة المنورة التي رحل إليها الإمام ورش وتلقاها عن إمام دار الهجرة نافع بن أبي نعيم، واعتمدها أهل المغرب وإفريقيا لقرون طويلة حتى اليوم.",
            keyRules = listOf(
                "نقل حركة الهمزة إلى الساكن الصحيح قبلها مع حذف الهمزة (مثل: {قَدَ اَفْلَحَ}، {الاَخِرَة}).",
                "قراءة {مَلِكِ يَوْمِ الدِّينِ} بحذف الألف بعد الميم.",
                "ترقيق الراءات ذوات الأسباب (إذا سُبقت بياء ساكنة أو كسرة لازمة).",
                "تغليظ اللامات المفتوحة إذا سبقت بالصاد أو الطاء أو الظاء المفتوحة أو الساكنة (مثل: {الصَّلَاة}، {طَلَّق}).",
                "إمالة الألفات ذوات الياء وما رُسم بالياء إمالة صغرى (تقليل).",
                "مد البدل بثلاثة أوجه: القصر (حركتان)، التوسط (4 حركات)، الطول (6 حركات)."
            ),
            basmalahRule = "البسملة ليست آية معدودة في أول الفاتحة عند أهل المدينة، وتبدأ الفاتحة بـ {الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ} (الآية 1)، وتعد {صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ} آية سادسة، وما بعدها آية سابعة.",
            defaultQariId = "husary_warsh"
        ),
        QuranRiwayah(
            id = "qalun",
            nameAr = "رواية قالون عن نافع",
            nameEn = "Qalun 'an Nafi'",
            rawiName = "الإمام أبو موسى عيسى بن مينا المدني الملقب بـ «قالون» (ت 220 هـ)",
            imamName = "الإمام نافع بن عبد الرحمن بن أبي نعيم المدني (ت 169 هـ)",
            regionAr = "ليبيا، تونس، أجزاء من تشاد وإفريقيا",
            scriptTypeAr = "الرسم العثماني - مصحف قالون المعتمد بمجمع الرسم والضبط",
            historicalBrief = "الراوي الأول لإمام المدينة نافع وربيبه، لقبه نافع بـ «قالون» لجودة قراءته (وهي كلمة رومية تعني الجيد). تعد الرواية الرسمية المعتمدة في ليبيا وتونس.",
            keyRules = listOf(
                "قراءة {مَلِكِ يَوْمِ الدِّينِ} بحذف الألف.",
                "تخيير في صلة ميم الجمع بواو لفظية مع إسكانها ({عَلَيْهِمُو}).",
                "تسهيل الهمزة الثانية من الهمزتين من كلمة مع إدخال ألف بينهما.",
                "قصر وتوسط المد المنفصل وتوسط المد المتصل.",
                "إسكان الهاء في {يُؤَدِّهْ إِلَيْكَ}، {نُوَلِّهْ مَا تَوَلَّى}."
            ),
            basmalahRule = "لا تعد البسملة آية في أول الفاتحة كقراءة نافع، وعدّ آيات الفاتحة يبدأ من الحمد لله رب العالمين.",
            defaultQariId = "husary_qalun"
        ),
        QuranRiwayah(
            id = "duri",
            nameAr = "رواية الدوري عن أبي عمرو",
            nameEn = "Al-Duri 'an Abi 'Amr",
            rawiName = "الإمام أبو عمر حفص بن عمر بن عبد العزيز الدوري البغدادي (ت 246 هـ)",
            imamName = "الإمام أبو عمرو بن العلاء البصري (ت 154 هـ)",
            regionAr = "السودان، الصومال، إرتريا، تشاد، وأجزاء من شرق إفريقيا واليمن",
            scriptTypeAr = "الرسم العثماني - مصحف الدوري المعتمد بمصحف إفريقيا",
            historicalBrief = "قراءة الإمام أبي عمرو البصري أحد القراء السبعة وشيخ العربية في البصرة، برواية حفص الدوري وهو أول من جمع القراءات وضبطها وله رواية أخرى عن الكسائي.",
            keyRules = listOf(
                "قراءة {مَلِكِ يَوْمِ الدِّينِ} بحذف الألف.",
                "إدغام الحرفين المتقاربين والمتماثلين في كلمات كثيرة.",
                "إمالة الألفات المتطرفة قبل راء مكسورة (مثل: {النَّارِ}، {الأَبْصَارِ}).",
                "تسهيل الهمز وإسقاط إحدى الهمزتين المتفقتين في الحركة من كلمتين.",
                "اختلاس واختصار بعض الحركات في الأفعال المخصوصة."
            ),
            basmalahRule = "بين كل سورتين: البسملة أو السكت بلا بسملة أو الوصل، والبسملة ليست آية من الفاتحة.",
            defaultQariId = "fateh_duri"
        ),
        QuranRiwayah(
            id = "susi",
            nameAr = "رواية السوسي عن أبي عمرو",
            nameEn = "Al-Susi 'an Abi 'Amr",
            rawiName = "الإمام أبو شعيب صالح بن زياد بن عبد الله السوسي الرقي (ت 261 هـ)",
            imamName = "الإمام أبو عمرو بن العلاء البصري (ت 154 هـ)",
            regionAr = "حلقات الإقراء والتخصص القرآني في العالم الإسلامي",
            scriptTypeAr = "الرسم العثماني - برواية السوسي",
            historicalBrief = "الراوي الثاني عن أبي عمرو البصري بواسطة الإمام يحيى اليزيدي، واشتهر بالإدغام الكبير وإبدال الهمز الساكن حرف مد.",
            keyRules = listOf(
                "الإدغام الكبير: إدغام الحرف المتحرك في الحرف المتحرك بعده عند التقائهما (مثل: {الرَّحِيمِ مَلِكِ} -> {الرَّحِيمِّلِكِ}، {جَعَلَ لَكُمُ} -> {جَعَلَّكُمُ}).",
                "إبدال كل همزة ساكنة حرف مد من جنس حركة ما قبلها ({مُؤْمِنُونَ} -> {مُومِنُونَ}).",
                "قراءة {مَلِكِ يَوْمِ الدِّينِ} بحذف الألف.",
                "إمالة ذوات الراء والألفات المتطرفة."
            ),
            basmalahRule = "يجوز له بين السورتين البسملة والسكت والوصل.",
            defaultQariId = "soufi_susi"
        ),
        QuranRiwayah(
            id = "shubah",
            nameAr = "رواية شعبة عن عاصم",
            nameEn = "Shu'bah 'an 'Asim",
            rawiName = "الإمام أبو بكر شعبة بن عياش بن سالم الأسدي الكوفي (ت 193 هـ)",
            imamName = "الإمام عاصم بن بهدلة أبي النَّجود الكوفي (ت 127 هـ)",
            regionAr = "حلقات الإقراء والقراءات العشر ومساجد العراق والشام قديماً",
            scriptTypeAr = "الرسم العثماني - برواية شعبة",
            historicalBrief = "رفيق حفص والتلميذ الأكبر للإمام عاصم بن أبي النجود، وكان عالماً زاهداً مكث في مجلس الإقراء عقوداً وروى القرآن بدقة وإتقان.",
            keyRules = listOf(
                "قراءة {مَالِكِ يَوْمِ الدِّينِ} بإثبات الألف كمثل حفص.",
                "كسر حرف السين في {يَحْسَبُ} في جميع مواضعه ({يَحْسِبُ}).",
                "إمالة ألف {رَمَىٰ} في الأنفال و{أَعْمَىٰ} في الإسراء و{سُوَىٰ} في طه.",
                "إسكان الياءات في مواضع مخصوصة مثل {وَمَحْيَايْ}.",
                "فروق مخصوصة في فرش الحروف عن حفص في مواضع متعددة."
            ),
            basmalahRule = "إثبات البسملة آية أولى في سورة الفاتحة وعدها سبع آيات.",
            defaultQariId = "saltany_shoba"
        )
    )

    fun getRiwayah(id: String): QuranRiwayah {
        return riwayahs.find { it.id.equals(id, ignoreCase = true) } ?: riwayahs.first()
    }

    /**
     * Reciters available for each Riwayah
     */
    val riwayahReciters = mapOf(
        "hafs" to listOf(
            Qari("alafasy", "مشاري راشد العفاسي", "Mishary Alafasy", "https://server8.mp3quran.net/afs/", "hafs"),
            Qari("yasser_dossari", "ياسر الدوسري", "Yasser Al-Dossari", "https://server11.mp3quran.net/yasser/", "hafs"),
            Qari("maher", "ماهر المعيقلي", "Maher Al-Muaiqly", "https://server12.mp3quran.net/maher/", "hafs"),
            Qari("sudais", "عبد الرحمن السديس", "Abdurrahman As-Sudais", "https://server11.mp3quran.net/sds/", "hafs"),
            Qari("minshawi", "محمد صديق المنشاوي (مرتل)", "Mohamed Siddiq El-Minshawi", "https://server10.mp3quran.net/minsh/", "hafs"),
            Qari("husary", "محمود خليل الحصري", "Mahmoud Khalil Al-Husary", "https://server13.mp3quran.net/husr/", "hafs"),
            Qari("basit", "عبد الباسط عبد الصمد (مرتل)", "Abdulbasit Abdulsamad", "https://server7.mp3quran.net/basit/", "hafs"),
            Qari("ajamy", "أحمد بن علي العجمي", "Ahmed Al-Ajamy", "https://server10.mp3quran.net/ajm/", "hafs")
        ),
        "warsh" to listOf(
            Qari("koshi_warsh", "العيون الكوشي (ورش عن نافع)", "El-Ayoun El-Kouchi (Warsh)", "https://server10.mp3quran.net/koshi/", "warsh"),
            Qari("jaza_warsh", "ياسين الجزائري (ورش عن نافع)", "Yassen Al-Jazairi (Warsh)", "https://server11.mp3quran.net/jaza/", "warsh"),
            Qari("husary_warsh", "محمود خليل الحصري (ورش عن نافع)", "Al-Husary (Warsh)", "https://server13.mp3quran.net/husr/warsh/", "warsh"),
            Qari("qzb_warsh", "عمر القزابري (ورش عن نافع)", "Omar Al-Qazabri (Warsh)", "https://server8.mp3quran.net/qzb/", "warsh"),
            Qari("basit_warsh", "عبد الباسط عبد الصمد (ورش عن نافع)", "Abdulbasit (Warsh)", "https://server7.mp3quran.net/basit/warsh/", "warsh")
        ),
        "qalun" to listOf(
            Qari("husary_qalun", "محمود خليل الحصري (قالون عن نافع)", "Al-Husary (Qalun)", "https://server12.mp3quran.net/husr/qaloon/", "qalun"),
            Qari("dokali_qalun", "الدوكالي محمد العالم (قالون عن نافع)", "Al-Dokali Muhammad (Qalun)", "https://server7.mp3quran.net/dokali/", "qalun"),
            Qari("hudhaifi_qalun", "علي بن عبد الرحمن الحذيفي (قالون عن نافع)", "Al-Hudhaifi (Qalun)", "https://server9.mp3quran.net/hthfi_qalon/", "qalun")
        ),
        "duri" to listOf(
            Qari("fateh_duri", "الفاتح محمد الزبير (الدوري عن أبي عمرو)", "Al-Fateh Al-Zubair (Duri)", "https://server6.mp3quran.net/fateh/", "duri"),
            Qari("saltany_dori", "مفتاح السلطني (الدوري عن أبي عمرو)", "Muftah Al-Saltany (Duri)", "https://server8.mp3quran.net/saltany_dori/", "duri")
        ),
        "susi" to listOf(
            Qari("soufi_susi", "عبد الرشيد صوفي (السوسي عن أبي عمرو)", "Abdulrashid Soufi (Susi)", "https://server16.mp3quran.net/soufi/", "susi")
        ),
        "shubah" to listOf(
            Qari("saltany_shoba", "مفتاح السلطني (شعبة عن عاصم)", "Muftah Al-Saltany (Shu'bah)", "https://server8.mp3quran.net/saltany_shoba/", "shubah")
        )
    )

    fun getRecitersForRiwayah(riwayahId: String): List<Qari> {
        return riwayahReciters[riwayahId.lowercase()] ?: riwayahReciters["hafs"]!!
    }

    /**
     * Canonical text differences across Riwayahs for educational comparison and recitation display
     */
    val differences = listOf(
        // Al-Fatihah
        RiwayahDifference(1, 4, "مَالِكِ يَوْمِ الدِّينِ", "مَلِكِ يَوْمِ الدِّينِ", "warsh", "حذف الألف بعد الميم وقراءتها بكسر اللام (مَلِكِ).", "فرش حروف"),
        RiwayahDifference(1, 4, "مَالِكِ يَوْمِ الدِّينِ", "مَلِكِ يَوْمِ الدِّينِ", "qalun", "حذف الألف بعد الميم كقراءة نافع.", "فرش حروف"),
        RiwayahDifference(1, 4, "مَالِكِ يَوْمِ الدِّينِ", "مَلِكِ يَوْمِ الدِّينِ", "duri", "حذف الألف بعد الميم كقراءة أبي عمرو.", "فرش حروف"),
        RiwayahDifference(1, 4, "مَالِكِ يَوْمِ الدِّينِ", "الرَّحِيمِّلِكِ يَوْمِ الدِّينِ", "susi", "حذف الألف مع الإدغام الكبير لميم الرحيم في ميم ملك وصلاً.", "إدغام كبير"),
        RiwayahDifference(1, 6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "warsh", "بالصاد وترقيق الراء، وعند رويس وقنبل بالسين {السِّرَاطَ}.", "أصول قراءة"),
        RiwayahDifference(1, 7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ", "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمُو", "qalun", "صلة ميم الجمع بواو لفظية بخلف عنه وصلاً.", "صلة ميم الجمع"),

        // Al-Baqarah
        RiwayahDifference(2, 9, "يُخَادِعُونَ اللَّهَ وَالَّذِينَ آمَنُوا وَمَا يَخْدَعُونَ إِلَّا أَنفُسَهُمْ", "يُخَادِعُونَ اللَّهَ وَالَّذِينَ آمَنُوا وَمَا يُخَادِعُونَ إِلَّا أَنفُسَهُمْ", "warsh", "قراءة الموضع الثاني بضم الياء وفتح الخاء وألف بعدها وكسر الدال (يُخَادِعُونَ).", "فرش حروف"),
        RiwayahDifference(2, 10, "فِي قُلُوبِهِم مَّرَضٌ فَزَادَهُمُ اللَّهُ مَرَضًا ۖ وَلَهُمْ عَذَابٌ أَلِيمٌ بِمَا كَانُوا يَكْذِبُونَ", "فِي قُلُوبِهِم مَّرَضٌ فَزَادَهُمُ اللَّهُ مَرَضًا ۖ وَلَهُمْ عَذَابٌ أَلِيمٌ بِمَا كَانُوا يُكَذِّبُونَ", "warsh", "قراءة {يُكَذِّبُونَ} بضم الياء وفتح الكاف وتشديد الذال مكسورة كقراءة الكوفيين وعاصم.", "فرش حروف"),

        // Al-Ikhlas
        RiwayahDifference(112, 4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "وَلَمْ يَكُن لَّهُ كُفُؤًا أَحَدٌ", "warsh", "قراءة {كُفُؤًا} بضم الفاء مع الهمز، مع نقل حركة الهمزة في {أَحَدٌ} وصلاً.", "نقل وهمز"),
        RiwayahDifference(112, 4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "وَلَمْ يَكُن لَّهُ كُفُؤًا أَحَدٌ", "qalun", "قراءة {كُفُؤًا} بضم الفاء والهمز وصلاً ووقفاً.", "همز مفرد"),

        // Al-Falaq
        RiwayahDifference(113, 1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "قُلَ اعُوذُ بِرَبِّ الْفَلَقِ", "warsh", "نقل حركة همزة القطع المفتوحة إلى اللام الساكنة قبلها فتسقط الهمزة لفظاً (قُلَ اعُوذُ).", "نقل حركة الهمز"),

        // An-Nas
        RiwayahDifference(114, 1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "قُلَ اعُوذُ بِرَبِّ النَّاسِ", "warsh", "نقل حركة الهمزة المفتوحة إلى اللام الساكنة قبلها لفظاً (قُلَ اعُوذُ).", "نقل حركة الهمز"),
        RiwayahDifference(114, 1, "بِرَبِّ النَّاسِ", "بِرَبِّ النَّاسِ (مع الإمالة)", "duri", "إمالة الألف قبل السين المكسورة بخلف عنه عند بعض القراء.", "إمالة")
    )

    fun getDifferencesForSurah(surahNumber: Int, riwayahId: String): List<RiwayahDifference> {
        return differences.filter {
            it.surahNumber == surahNumber && (riwayahId == "all" || it.riwayahId.equals(riwayahId, ignoreCase = true))
        }
    }

    /**
     * Adapts Ayah text according to active Riwayah for accurate visual display
     */
    fun getAyahTextForRiwayah(surahNumber: Int, ayahNumber: Int, defaultHafsText: String, riwayahId: String): String {
        return when (riwayahId.lowercase()) {
            "warsh" -> {
                when {
                    surahNumber == 1 && ayahNumber == 4 -> "مَلِكِ يَوْمِ الدِّينِ"
                    surahNumber == 112 && ayahNumber == 4 -> "وَلَمْ يَكُن لَّهُ كُفُؤاً أَحَدٌ"
                    surahNumber == 113 && ayahNumber == 1 -> "قُلَ اعُوذُ بِرَبِّ الْفَلَقِ"
                    surahNumber == 114 && ayahNumber == 1 -> "قُلَ اعُوذُ بِرَبِّ النَّاسِ"
                    else -> defaultHafsText
                }
            }
            "qalun" -> {
                when {
                    surahNumber == 1 && ayahNumber == 4 -> "مَلِكِ يَوْمِ الدِّينِ"
                    surahNumber == 112 && ayahNumber == 4 -> "وَلَمْ يَكُن لَّهُ كُفُؤاً أَحَدٌ"
                    else -> defaultHafsText
                }
            }
            "duri", "susi" -> {
                when {
                    surahNumber == 1 && ayahNumber == 4 -> "مَلِكِ يَوْمِ الدِّينِ"
                    else -> defaultHafsText
                }
            }
            else -> defaultHafsText
        }
    }
}
