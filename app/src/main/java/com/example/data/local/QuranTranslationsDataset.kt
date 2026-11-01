package com.example.data.local

import com.example.data.models.QuranTranslationEdition

object QuranTranslationsDataset {

    val editions = listOf(
        QuranTranslationEdition("en", "الإنجليزية", "English", "Sahih International", "🇬🇧"),
        QuranTranslationEdition("fr", "الفرنسية", "Français", "Muhammad Hamidullah", "🇫🇷"),
        QuranTranslationEdition("ur", "الأردية", "اردو", "مولانا فتح محمد جالندھری", "🇵🇰"),
        QuranTranslationEdition("id", "الإندونيسية", "Bahasa Indonesia", "Kementerian Agama RI", "🇮🇩"),
        QuranTranslationEdition("tr", "التركية", "Türkçe", "Diyanet İşleri Başkanlığı", "🇹🇷"),
        QuranTranslationEdition("es", "الإسبانية", "Español", "Abdel Ghani Navio", "🇪🇸")
    )

    fun getEdition(code: String): QuranTranslationEdition {
        return editions.find { it.code.equals(code, ignoreCase = true) } ?: editions.first()
    }

    /**
     * Returns multi-language translations for given surah and ayah (keyed by language code).
     */
    fun getTranslations(surahNumber: Int, ayahNumber: Int): Map<String, String> {
        val key = "$surahNumber:$ayahNumber"
        val specific = translationsMap[key]
        if (specific != null) return specific

        // Fallback generic English/French/Urdu translation if not in offline cache
        return mapOf(
            "en" to "Ayah $ayahNumber of Surah $surahNumber - In the Name of Allah, the Entirely Merciful, the Especially Merciful.",
            "fr" to "Verset $ayahNumber de la sourate $surahNumber - Au nom d'Allah, le Tout Miséricordieux, le Très Miséricordieux.",
            "ur" to "سورۃ $surahNumber کی آیت $ayahNumber - اللہ کے نام سے جو رحمان و رحیم ہے۔",
            "id" to "Ayat $ayahNumber dari Surah $surahNumber - Dengan nama Allah Yang Maha Pengasih, Maha Penyayang.",
            "tr" to "Sure $surahNumber, Ayet $ayahNumber - Rahmân ve Rahîm olan Allah'ın adıyla.",
            "es" to "Aleya $ayahNumber de la Sura $surahNumber - En el nombre de Al-lâh, el Clementísimo, el Misericordiosísimo."
        )
    }

    private val translationsMap = mapOf(
        // Al-Fatihah (1)
        "1:1" to mapOf(
            "en" to "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
            "fr" to "Au nom d'Allah, le Tout Miséricordieux, le Très Miséricordieux.",
            "ur" to "شروع اللہ کا نام لے کر جو بڑا مہربان نہایت رحم والا ہے۔",
            "id" to "Dengan nama Allah Yang Maha Pengasih, Maha Penyayang.",
            "tr" to "Rahmân ve Rahîm olan Allah'ın adıyla.",
            "es" to "En el nombre de Al-lâh, el Clementísimo, el Misericordiosísimo."
        ),
        "1:2" to mapOf(
            "en" to "[All] praise is [due] to Allah, Lord of the worlds.",
            "fr" to "Louange à Allah, Seigneur de l'univers.",
            "ur" to "سب طرح کی تعریف اللہ ہی کو سزاوار ہے جو تمام جہانوں کا پروردگار ہے۔",
            "id" to "Segala puji bagi Allah, Tuhan semesta alam.",
            "tr" to "Hamd, âlemlerin Rabbi olan Allah'a mahsustur.",
            "es" to "Alabado sea Al-lâh, Señor de los mundos."
        ),
        "1:3" to mapOf(
            "en" to "The Entirely Merciful, the Especially Merciful,",
            "fr" to "Le Tout Miséricordieux, le Très Miséricordieux,",
            "ur" to "بڑا مہربان، نہایت رحم والا ہے۔",
            "id" to "Maha Pengasih, Maha Penyayang,",
            "tr" to "O, Rahmân ve Rahîm'dir.",
            "es" to "El Clementísimo, el Misericordiosísimo,"
        ),
        "1:4" to mapOf(
            "en" to "Sovereign of the Day of Recompense.",
            "fr" to "Maître du Jour de la rétribution.",
            "ur" to "انصاف کے دن کا حاکم ہے۔",
            "id" to "Pemilik Hari Pembalasan.",
            "tr" to "Ceza (ve mükâfat) gününün sahibidir.",
            "es" to "Dueño del Día del Juicio."
        ),
        "1:5" to mapOf(
            "en" to "It is You we worship and You we ask for help.",
            "fr" to "C'est Toi [Seul] que nous adorons, et c'est Toi [Seul] dont nous implorons secours.",
            "ur" to "ہم تیری ہی عبادت کرتے ہیں اور تجھ ہی سے مدد مانگتے ہیں۔",
            "id" to "Hanya kepada Engkaulah kami menyembah dan hanya kepada Engkaulah kami mohon pertolongan.",
            "tr" to "(Rabbimiz!) Ancak sana kulluk eder ve yalnız senden yardım dileriz.",
            "es" to "Solo a Ti adoramos y solo a Ti pedimos ayuda."
        ),
        "1:6" to mapOf(
            "en" to "Guide us to the straight path -",
            "fr" to "Guide-nous dans le droit chemin,",
            "ur" to "ہم کو سیدھے راستے پر چلا۔",
            "id" to "Tunjukilah kami jalan yang lurus,",
            "tr" to "Bizi doğru yola ilet,",
            "es" to "Guíanos por el camino recto,"
        ),
        "1:7" to mapOf(
            "en" to "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.",
            "fr" to "Le chemin de ceux que Tu as comblés de faveurs, non pas de ceux qui ont encouru Ta colère, ni des égarés.",
            "ur" to "ان لوگوں کے راستے پر جن پر تو نے انعام فرمایا، نہ ان کے جن پر غضب نازل ہوا اور نہ گمراہوں کے۔",
            "id" to "(yaitu) jalan orang-orang yang telah Engkau beri nikmat kepadanya; bukan (jalan) mereka yang dimurkai, dan bukan (pula jalan) mereka yang sesat.",
            "tr" to "Kendilerine lütuf ve ikramda bulunduğun kimselerin yoluna; gazaba uğramışların ve sapmışların yoluna değil.",
            "es" to "El camino de los que has colmado con Tus gracias, no el de los que han incurrido en Tu ira, ni el de los extraviados."
        ),

        // Al-Ikhlas (112)
        "112:1" to mapOf(
            "en" to "Say, \"He is Allah, [who is] One,",
            "fr" to "Dis: «Il est Allah, Unique.",
            "ur" to "کہو کہ وہ اللہ ایک ہے۔",
            "id" to "Katakanlah (Muhammad), \"Dialah Allah, Yang Maha Esa.\"",
            "tr" to "De ki: O, Allah birdir.",
            "es" to "Di: Él es Al-lâh, Uno."
        ),
        "112:2" to mapOf(
            "en" to "Allah, the Eternal Refuge.",
            "fr" to "Allah, Le Seul à être imploré pour ce que nous désirons.",
            "ur" to "اللہ بے نیاز ہے۔",
            "id" to "Allah tempat meminta segala sesuatu.",
            "tr" to "Allah sameddir (her şey O'na muhtaçtır, O hiçbir şeye muhtaç değildir).",
            "es" to "Al-lâh, el Señor Absoluto."
        ),
        "112:3" to mapOf(
            "en" to "He neither begets nor is born,",
            "fr" to "Il n'a jamais engendré, n'a pas été engendré non plus.",
            "ur" to "نہ اس کی کوئی اولاد ہے اور نہ وہ کسی کی اولاد ہے۔",
            "id" to "(Allah) tidak beranak dan tidak pula diperanakkan,",
            "tr" to "O, doğurmamış ve doğmamıştır.",
            "es" to "No ha engendrado ni ha sido engendrado."
        ),
        "112:4" to mapOf(
            "en" to "Nor is there to Him any equivalent.\"",
            "fr" to "Et nul n'est égal à Lui».",
            "ur" to "اور نہ کوئی اس کا ہمسر ہے۔",
            "id" to "dan tidak ada sesuatu yang setara dengan Dia.\"",
            "tr" to "Ve hiçbir şey O'nun dengi olmamıştır.",
            "es" to "Y no hay nadie semejante a Él."
        ),

        // Al-Falaq (113)
        "113:1" to mapOf(
            "en" to "Say, \"I seek refuge in the Lord of daybreak",
            "fr" to "Dis: «Je cherche protection auprès du Seigneur de l'aube naissante,",
            "ur" to "کہو کہ میں صبح کے پروردگار کی پناہ مانگتا ہوں۔",
            "id" to "Katakanlah, \"Aku berlindung kepada Tuhan yang menguasai subuh (fajar),",
            "tr" to "De ki: Sabahın Rabbine sığınırım,",
            "es" to "Di: Me refugio en el Señor del amanecer,"
        ),
        "113:2" to mapOf(
            "en" to "From the evil of that which He created",
            "fr" to "contre le mal des êtres qu'Il a créés,",
            "ur" to "ہر اس چیز کی برائی سے جو اس نے پیدا کی۔",
            "id" to "dari kejahatan (makhluk yang) Dia ciptakan,",
            "tr" to "Yarattığı şeylerin şerrinden,",
            "es" to "Del mal de lo que Él ha creado,"
        ),
        "113:3" to mapOf(
            "en" to "And from the evil of darkness when it settles",
            "fr" to "contre le mal de l'obscurité quand elle s'approfondit,",
            "ur" to "اور اندھیری رات کی برائی سے جب وہ چھا جائے۔",
            "id" to "dan dari kejahatan malam apabila telah gelap gulita,",
            "tr" to "Karanlığı çöktüğü zaman gecenin şerrinden,",
            "es" to "Del mal de la noche cuando se extiende,"
        ),
        "113:4" to mapOf(
            "en" to "And from the evil of the blowers in knots",
            "fr" to "contre le mal de celles qui soufflent sur les nœuds,",
            "ur" to "اور گنڈوں پر پھونکنے والیوں کی برائی سے۔",
            "id" to "dan dari kejahatan (perempuan-perempuan) penyihir yang meniup pada buhul-buhul (talinya),",
            "tr" to "Düğümlere üfleyenlerin şerrinden,",
            "es" to "Del mal de las que soplan en los nudos,"
        ),
        "113:5" to mapOf(
            "en" to "And from the evil of an envier when he envies.\"",
            "fr" to "et contre le mal de l'envieux quand il envie».",
            "ur" to "اور حسد کرنے والے کی برائی سے جب وہ حسد کرے۔",
            "id" to "dan dari kejahatan orang yang dengki apabila dia dengki.\"",
            "tr" to "Ve haset ettiği zaman hasetçinin şerrinden.",
            "es" to "Y del mal del envidioso cuando envidia."
        ),

        // An-Nas (114)
        "114:1" to mapOf(
            "en" to "Say, \"I seek refuge in the Lord of mankind,",
            "fr" to "Dis: «Je cherche protection auprès du Seigneur des hommes.",
            "ur" to "کہو کہ میں لوگوں کے پروردگار کی پناہ مانگتا ہوں۔",
            "id" to "Katakanlah, \"Aku berlindung kepada Tuhannya manusia,",
            "tr" to "De ki: İnsanların Rabbine sığınırım,",
            "es" to "Di: Me refugio en el Señor de los hombres,"
        ),
        "114:2" to mapOf(
            "en" to "The Sovereign of mankind,",
            "fr" to "Le Roi des hommes,",
            "ur" to "لوگوں کے بادشاہ کی۔",
            "id" to "Raja manusia,",
            "tr" to "İnsanların mâlikine,",
            "es" to "El Rey de los hombres,"
        ),
        "114:3" to mapOf(
            "en" to "The God of mankind,",
            "fr" to "Le Dieu des hommes,",
            "ur" to "لوگوں کے معبود برحق کی۔",
            "id" to "Sembahan manusia,",
            "tr" to "İnsanların ilâhına,",
            "es" to "El Dios de los hombres,"
        ),
        "114:4" to mapOf(
            "en" to "From the evil of the retreating whisperer -",
            "fr" to "contre le mal du mauvais conseiller, furtif,",
            "ur" to "وسوسہ ڈالنے والے (اور) دبک جانے والے کی برائی سے۔",
            "id" to "dari kejahatan (bisikan) setan yang bersembunyi,",
            "tr" to "O sinsi vesvesecinin şerrinden,",
            "es" to "Del mal del susurrador que huye,"
        ),
        "114:5" to mapOf(
            "en" to "Who whispers [evil] into the breasts of mankind -",
            "fr" to "qui souffle le mal dans les poitrines des hommes,",
            "ur" to "جو لوگوں کے دلوں میں وسوسے ڈالتا ہے۔",
            "id" to "yang membisikkan (kejahatan) ke dalam dada manusia,",
            "tr" to "Ki o, insanların kalplerine vesvese verir,",
            "es" to "Que susurra en los pechos de los hombres,"
        ),
        "114:6" to mapOf(
            "en" to "From among the jinn and mankind.\"",
            "fr" to "qu'il soit, [ce conseiller], parmi les djinns ou les hommes».",
            "ur" to "(خواہ وہ) جنات میں سے ہو یا انسانوں میں سے۔",
            "id" to "dari (golongan) jin dan manusia.\"",
            "tr" to "Gerek cinlerden, gerekse insanlardan.",
            "es" to "Sea de los genios o de los hombres."
        ),

        // Al-Kawthar (108)
        "108:1" to mapOf(
            "en" to "Indeed, We have granted you, [O Muhammad], al-Kawthar.",
            "fr" to "Nous t'avons certes accordé l'Abondance.",
            "ur" to "بے شک ہم نے آپ کو کوثر عطا فرمایا۔",
            "id" to "Sungguh, Kami telah memberimu (Muhammad) nikmat yang banyak.",
            "tr" to "Şüphesiz biz sana Kevser'i verdik.",
            "es" to "Por cierto que te hemos concedido la Gracia Inmensa (el Kauzar)."
        ),
        "108:2" to mapOf(
            "en" to "So pray to your Lord and sacrifice [to Him alone].",
            "fr" to "Accomplis donc la prière pour ton Seigneur et sacrifie.",
            "ur" to "پس اپنے رب کے لیے نماز پڑھیے اور قربانی کیجیے۔",
            "id" to "Maka laksanakanlah salat karena Tuhanmu, dan berkurbanlah.",
            "tr" to "Şimdi sen Rabbin için namaz kıl ve kurban kes.",
            "es" to "Reza a tu Señor y haz el sacrificio."
        ),
        "108:3" to mapOf(
            "en" to "Indeed, your enemy is the one cut off.",
            "fr" to "Celui qui te hait sera certes, sans postérité.",
            "ur" to "یقیناً آپ کا دشمن ہی بے نام و نشان رہے گا۔",
            "id" to "Sungguh, orang yang membencimu dialah yang terputus (dari rahmat Allah).",
            "tr" to "Doğrusu sana kin besleyendir soyu kesik olan.",
            "es" to "Quien te detesta es quien no tendrá descendencia."
        ),

        // Al-Asr (103)
        "103:1" to mapOf(
            "en" to "By time,",
            "fr" to "Par le Temps!",
            "ur" to "زمانے کی قسم!",
            "id" to "Demi masa,",
            "tr" to "Asra yemin olsun ki,",
            "es" to "¡Por el tiempo!"
        ),
        "103:2" to mapOf(
            "en" to "Indeed, mankind is in loss,",
            "fr" to "L'homme est certes, en perdition,",
            "ur" to "بے شک انسان خسارے میں ہے،",
            "id" to "sungguh, manusia berada dalam kerugian,",
            "tr" to "İnsan gerçekten ziyan içindedir.",
            "es" to "Ciertamente el ser humano está en la perdición,"
        ),
        "103:3" to mapOf(
            "en" to "Except for those who have believed and done righteous deeds and advised each other to truth and advised each other to patience.",
            "fr" to "sauf ceux qui croient et accomplissent les bonnes œuvres, s'enjoignent mutuellement la vérité et s'enjoignent mutuellement l'endurance.",
            "ur" to "سوائے ان لوگوں کے جو ایمان لائے اور نیک عمل کیے اور ایک دوسرے کو حق کی نصیحت اور صبر کی تلقین کی۔",
            "id" to "kecuali orang-orang yang beriman dan mengerjakan kebajikan serta saling menasihati untuk kebenaran dan saling menasihati untuk kesabaran.",
            "tr" to "Ancak iman edip salih ameller işleyenler, birbirlerine hakkı tavsiye edenler ve sabrı tavsiye edenler müstesnadır.",
            "es" to "Salvo los que creen, obran rectamente, se aconsejan mutuamente la verdad y se aconsejan la paciencia."
        ),

        // Al-Mulk (67:1-4)
        "67:1" to mapOf(
            "en" to "Blessed is He in whose hand is dominion, and He is over all things competent -",
            "fr" to "Béni soit celui dans la main de qui est la royauté, et Il est Omnipotent.",
            "ur" to "بڑی برکت والی ہے وہ ذات جس کے ہاتھ میں بادشاہی ہے اور وہ ہر چیز پر قادر ہے۔",
            "id" to "Mahasuci Allah yang menguasai (segala) kerajaan, dan Dia Mahakuasa atas segala sesuatu.",
            "tr" to "Hükümranlık elinde olan Allah yücedir ve O, her şeye hakkıyla kadirdir.",
            "es" to "Bendito sea Aquel en Cuyas Manos está el reino y es sobre todas las cosas Poderoso."
        ),
        "67:2" to mapOf(
            "en" to "[He] who created death and life to test you [as to] which of you is best in deed - and He is the Exalted in Might, the Forgiving -",
            "fr" to "Celui qui a créé la mort et la vie afin de vous éprouver (et de savoir) qui de vous est le meilleur en œuvre, et c'est Lui le Puissant, le Pardonneur.",
            "ur" to "جس نے موت اور زندگی کو پیدا کیا تاکہ تمہیں آزمائے کہ تم میں سے عمل کے لحاظ سے کون بہتر ہے، اور وہ زبردست بخشنے والا ہے۔",
            "id" to "Yang menciptakan mati dan hidup, untuk menguji kamu, siapa di antara kamu yang lebih baik amalnya. Dan Dia Mahaperkasa, Maha Pengampun.",
            "tr" to "O, hanginizin daha güzel amel yapacağını sınamak için ölümü ve hayatı yaratandır. O, mutlak güç sahibidir, çok bağışlayandır.",
            "es" to "Aquel que creó la muerte y la vida para probaros cuál de vosotros tiene mejores obras. Y Él es el Poderoso, el Perdonador."
        )
    )
}
