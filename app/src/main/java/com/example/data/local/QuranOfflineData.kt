package com.example.data.local

import com.example.data.models.Ayah

object QuranOfflineData {

    fun getOfflineSurahAyahs(surahNumber: Int): List<Ayah>? {
        return when (surahNumber) {
            1 -> fatihah
            67 -> mulk
            93 -> duha
            94 -> sharh
            95 -> tin
            96 -> alaq
            97 -> qadr
            98 -> bayyinah
            99 -> zalzalah
            100 -> adiyat
            101 -> qariah
            102 -> takathur
            103 -> asr
            104 -> humazah
            105 -> fil
            106 -> quraysh
            107 -> maun
            108 -> kawthar
            109 -> kafirun
            110 -> nasr
            111 -> masad
            112 -> ikhlas
            113 -> falaq
            114 -> nas
            36 -> yaseenSample
            55 -> rahmanSample
            56 -> waqiahSample
            18 -> kahfSample
            else -> null
        }
    }

    val fatihah = listOf(
        Ayah(1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah, the Entirely Merciful, the Especially Merciful.", "تفتتح بها القراءة وتطلب بها البركة ومعونة الله"),
        Ayah(2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "[All] praise is [due] to Allah, Lord of the worlds -", "الثناء والحمد الخالص لله رب كل المخلوقات"),
        Ayah(3, "الرَّحْمَٰنِ الرَّحِيمِ", "The Entirely Merciful, the Especially Merciful,", "ذو الرحمة الواسعة بعباده المؤمنين والكافرين في الدنيا، والخاصة بالمؤمنين في الآخرة"),
        Ayah(4, "مَالِكِ يَوْمِ الدِّينِ", "Sovereign of the Day of Recompense.", "الملك المتصرف وحده يوم الحساب والجزاء"),
        Ayah(5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "It is You we worship and You we ask for help.", "نخصك وحدك بالعبادة ونستعين بك وحدك في كل شأن"),
        Ayah(6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Guide us to the straight path -", "أرشدنا وثبتنا على دين الإسلام الواضح المستقيم"),
        Ayah(7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.", "طريق النبيين والصديقين والشهداء والصالحين")
    )

    val mulk = listOf(
        Ayah(1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Blessed is He in whose hand is dominion, and He is over all things competent -", "تعاظم وثبت خير الله وتصرفه المطلق في خلقه"),
        Ayah(2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ", "He who created death and life to test you [as to] which of you is best in deed -", "خلق الموت والحياة ليختبر طاعتكم وإخلاصكم"),
        Ayah(3, "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا ۖ مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ ۖ فَارْجِعِ الْبَصَرَ هَلْ تَرَىٰ مِن فُطُورٍ", "Who created seven heavens in layers. You do not see in the creation of the Most Merciful any inconsistency.", "خلق سبع سماوات متطابقة في إتقان وإحكام بديع"),
        Ayah(4, "ثُمَّ ارْجِعِ الْبَصَرَ كَرَّتَيْنِ يَنقَلِبْ إِلَيْكَ الْبَصَرُ خَاسِئًا وَهُوَ حَسِيرٌ", "Then return [your] vision twice again. [Your] vision will return to you humbled while it is fatigued.", "مهما تأملت فلن تجد عيباً أو نقصاً"),
        Ayah(5, "وَلَقَدْ زَيَّنَّا السَّمَاءَ الدُّنْيَا بِمَصَابِيحَ وَجَعَلْنَاهَا رُجُومًا لِّلشَّيَاطِينِ ۖ وَأَعْتَدْنَا لَهُمْ عَذَابَ السَّعِيرِ", "And We have certainly beautified the nearest heaven with stars and made them missiles for devils.", "زينة للناظرين وحفظاً من استراق الشياطين للسمع"),
        Ayah(6, "وَلِلَّذِينَ كَفَرُوا بِرَبِّهِمْ عَذَابُ جَهَنَّمَ ۖ وَبِئْسَ الْمَصِيرُ", "And for those who disbelieved in their Lord is the punishment of Hell, and wretched is the destination.", "جزاء الكافرين نار جهنم وبئس المستقر"),
        Ayah(7, "إِذَا أُلْقُوا فِيهَا سَمِعُوا لَهَا شَهِيقًا وَهِيَ تَفُورُ", "When they are thrown into it, they hear from it a dreadful inhaling while it boils up.", "لها صوت منكر مفزع وهي تغلي بهم"),
        Ayah(8, "تَكَادُ تَمَيَّزُ مِنَ الْغَيْظِ ۖ كُلَّمَا أُلْقِيَ فِيهَا فَوْجٌ سَأَلَهُمْ خَزَنَتُهَا أَلَمْ يَأْتِكُمْ نَذِيرٌ", "It almost bursts with fury. Every time a company is thrown into it, its keepers ask them: Did there not come to you a warner?", "تتقطع غيظاً على أهل الكفر"),
        Ayah(9, "قَالُوا بَلَىٰ قَدْ جَاءَنَا نَذِيرٌ فَكَذَّبْنَا وَقُلْنَا مَا نَزَّلَ اللَّهُ مِن شَيْءٍ إِنْ أَنتُمْ إِلَّا فِي ضَلَالٍ كَبِيرٍ", "They will say: Yes, a warner had come to us, but we denied and said: Allah has not sent down a thing.", "اعتراف متأخر بالذنب والتكذيب"),
        Ayah(10, "وَقَالُوا لَوْ كُنَّا نَسْمَعُ أَوْ نَعْقِلُ مَا كُنَّا فِي أَصْحَابِ السَّعِيرِ", "And they will say: If only we had been listening or reasoning, we would not be among the companions of the Blaze.", "لو استمعنا للحق وعقلناه لنجونا"),
        Ayah(11, "فَاعْتَرَفُوا بِذَنبِهِمْ فَسُحْقًا لِّأَصْحَابِ السَّعِيرِ", "And they will admit their sin, so [it is] alienation for the companions of the Blaze.", "فبعداً وهلاكاً لأهل النار"),
        Ayah(12, "إِنَّ الَّذِينَ يَخْشَوْنَ رَبَّهُم بِالْغَيْبِ لَهُم مَّغْفِرَةٌ وَأَجْرٌ كَبِيرٌ", "Indeed, those who fear their Lord unseen will have forgiveness and great reward.", "الذين يخافون الله في سرهم وعلانيتهم لهم الجنة"),
        Ayah(13, "وَأَسِرُّوا قَوْلَكُمْ أَوِ اجْهَرُوا بِهِ ۖ إِنَّهُ عَلِيمٌ بِذَاتِ الصُّدُورِ", "And conceal your speech or publicize it; indeed, He is Knowing of that within the breasts.", "يعلم ما تسرون وما تعلنون"),
        Ayah(14, "أَلَا يَعْلَمُ مَنْ خَلَقَ وَهُوَ اللَّطِيفُ الْخَبِيرُ", "Does He who created not know, while He is the Subtle, the Acquainted?", "كيف لا يعلم الخالق خلقه وهو اللطيف بأسرارهم"),
        Ayah(15, "هُوَ الَّذِي جَعَلَ لَكُمُ الْأَرْضَ ذَلُولًا فَامْشُوا فِي مَنَاكِبِهَا وَكُلُوا مِن رِّزْقِهِ ۖ وَإِلَيْهِ النُّشُورُ", "He it is who made the earth tame for you - so walk among its slopes and eat of His provision - and to Him is the resurrection.", "يسر الأرض للعيش والسعي والرزق"),
        Ayah(16, "أَأَمِنتُم مَّن فِي السَّمَاءِ أَن يَخْسِفَ بِكُمُ الْأَرْضَ فَإِذَا هِيَ تَمُورُ", "Do you feel secure that He who is in the heaven would not cause the earth to swallow you up and suddenly it would sway?", "تذكير بقدرة الله على الخسف والعذاب"),
        Ayah(17, "أَمْ أَمِنتُم مَّن فِي السَّمَاءِ أَن يُرْسِلَ عَلَيْكُمْ حَاصِبًا ۖ فَسَتَعْلَمُونَ كَيْفَ نَذِيرِ", "Or do you feel secure that He who is in the heaven would not send against you a storm of stones?", "أو يرسل عليكم ريحاً حاصبة تقذف بالحجارة"),
        Ayah(18, "وَلَقَدْ كَذَّبَ الَّذِينَ مِن قَبْلِهِمْ فَكَيْفَ كَانَ نَكِيرِ", "And already had those before them denied, and how [terrible] was My reproach.", "فكيف كان إنكاري وعقابي للأمم السابقة"),
        Ayah(19, "أَوَلَمْ يَرَوْا إِلَى الطَّيْرِ فَوْقَهُمْ صَافَّاتٍ وَيَقْبِضْنَ ۚ مَا يُمْسِكُهُنَّ إِلَّا الرَّحْمَٰنُ ۚ إِنَّهُ بِكُلِّ شَيْءٍ بَصِيرٌ", "Do they not see the birds above them with wings outspread and [then] folding [them] in? None holds them up except the Most Merciful.", "آية الطير وبديع حفظ الله لها في جو السماء"),
        Ayah(20, "أَمَّنْ هَٰذَا الَّذِي هُوَ جُندٌ لَّكُمْ يَنصُرُكُم مِّن دُونِ الرَّحْمَٰنِ ۚ إِنِ الْكَافِرُونَ إِلَّا فِي غُرُورٍ", "Or who is it that could be an army for you to aid you other than the Most Merciful? The disbelievers are not but in delusion.", "لا ناصر ينصركم إن أراد الله بكم سوءاً"),
        Ayah(21, "أَمَّنْ هَٰذَا الَّذِي يَرْزُقُكُمْ إِنْ أَمْسَكَ رِزْقَهُ ۚ بَل لَّجُّوا فِي عُتُوٍّ وَنُفُورٍ", "Or who is it that could provide for you if He withheld His provision? But they have persisted in insolence and aversion.", "من يرزقكم إن أمسك الله خزائن رحمته"),
        Ayah(22, "أَفَمَن يَمْشِي مُكِبًّا عَلَىٰ وَجْهِهِ أَهْدَىٰ أَمَّن يَمْشِي سَوِيًّا عَلَىٰ صِرَاطٍ مُّسْتَقِيمٍ", "Then is one who walks fallen on his face better guided or one who walks erect on a straight path?", "مثل الكافر التائه والمؤمن المستبصر المهتدي"),
        Ayah(23, "قُلْ هُوَ الَّذِي أَنشَأَكُمْ وَجَعَلَ لَكُمُ السَّمْعَ وَالْأَبْصَارَ وَالْأَفْئِدَةَ ۖ قَلِيلًا مَّا تَشْكُرُونَ", "Say, It is He who has produced you and made for you hearing and vision and hearts; little are you grateful.", "نعم السمع والبصر والعقل تستوجب الشكر"),
        Ayah(24, "قُلْ هُوَ الَّذِي ذَرَأَكُمْ فِي الْأَرْضِ وَإِلَيْهِ تُحْشَرُونَ", "Say, It is He who has multiplied you throughout the earth, and to Him you will be gathered.", "خلقكم وبثكم في الأرض وإليه مرجعكم"),
        Ayah(25, "وَيَقُولُونَ مَتَىٰ هَٰذَا الْوَعْدُ إِن كُنتُمْ صَادِقِينَ", "And they say, When is this promise, if you should be truthful?", "استعجال الكفار للبعث تكذيباً"),
        Ayah(26, "قُلْ إِنَّمَا الْعِلْمُ عِندَ اللَّهِ وَإِنَّمَا أَنَا نَذِيرٌ مُّبِينٌ", "Say, The knowledge is only with Allah, and I am only a clear warner.", "علم الساعة عند الله وحده"),
        Ayah(27, "فَلَمَّا رَأَوْهُ زُلْفَةً سِيئَتْ وُجُوهُ الَّذِينَ كَفَرُوا وَقِيلَ هَٰذَا الَّذِي كُنتُم بِهِ تَدَّعُونَ", "But when they see it approaching, the faces of those who disbelieve will be distressed.", "إذا شاهدوا العذاب قريباً اسودت وجوههم خيبة وحزناً"),
        Ayah(28, "قُلْ أَرَأَيْتُمْ إِنْ أَهْلَكَنِيَ اللَّهُ وَمَن مَّعِيَ أَوْ رَحِمَنَا فَمَن يُجِيرُ الْكَافِرِينَ مِنْ عَذَابٍ أَلِيمٍ", "Say, Have you considered: whether Allah should cause my death and those with me or have mercy upon us, who can protect the disbelievers from a painful punishment?", "من يحميكم من العذاب الأليم"),
        Ayah(29, "قُلْ هُوَ الرَّحْمَٰنُ آمَنَّا بِهِ وَعَلَيْهِ تَوَكَّلْنَا ۖ فَسَتَعْلَمُونَ مَنْ هُوَ فِي ضَلَالٍ مُّبِينٍ", "Say, He is the Most Merciful; we have believed in Him, and upon Him we have relied.", "هو الله الرحمن آمنا به وعليه توكلنا"),
        Ayah(30, "قُلْ أَرَأَيْتُمْ إِنْ أَصْبَحَ مَاؤُكُمْ غَوْرًا فَمَن يَأْتِيكُم بِمَاءٍ مَّعِينٍ", "Say, Have you considered: if your water was to become sunken into the earth, then who could bring you flowing water?", "لو غار ماؤكم في باطن الأرض فمن يأتيكم بماء عذب جارٍ غير الله؟")
    )

    val duha = listOf(
        Ayah(1, "وَالضُّحَىٰ", "By the morning brightness", "قسم بوقت الضحى ونوره وضيائه"),
        Ayah(2, "وَاللَّيْلِ إِذَا سَجَىٰ", "And [by] the night when it covers with darkness,", "والليل إذا اشتد ظلامه وسكن الخلق فيه"),
        Ayah(3, "مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ", "Your Lord has not taken leave of you, [O Muhammad], nor has He detested [you].", "ما تركك ربك وما أبغضك منذ اصطفاك"),
        Ayah(4, "وَلَلْآخِرَةُ خَيْرٌ لَّكَ مِنَ الْأُولَىٰ", "And the Hereafter is better for you than the first [life].", "ما ادخره الله لك في الدار الآخرة أعظم وأبقى"),
        Ayah(5, "وَلَسَوْفَ يُعْطِيكَ رَبُّكَ فَتَرْضَىٰ", "And your Lord is going to give you, and you will be satisfied.", "سيعطيك من الشفاعة والدرجات العلى حتى ترضى"),
        Ayah(6, "أَلَمْ يَجِدْكَ يَتِيمًا فَآوَىٰ", "Did He not find you an orphan and give [you] refuge?", "ألم تكن يتيماً فرعاك الله وحماك"),
        Ayah(7, "وَوَجَدَكَ ضَالًّا فَهَدَىٰ", "And He found you lost and guided [you],", "علمك ما لم تكن تعلم وهداك للحق والشريعة"),
        Ayah(8, "وَوَجَدَكَ عَائِلًا فَأَغْنَىٰ", "And He found you poor and made [you] self-sufficient.", "كنت فقيراً فرزقك وأغنى قلبك ونفسك"),
        Ayah(9, "فَأَمَّا الْيَتِيمَ فَلَا تَقْهَرْ", "So as for the orphan, do not oppress [him].", "فلا تذل اليتيم ولا تظلمه بل أحسن إليه"),
        Ayah(10, "وَأَمَّا السَّائِلَ فَلَا تَنْهَرْ", "And as for the petitioner, do not repel [him].", "لا تزجر المحتاج بل رده بمعروف ولطف"),
        Ayah(11, "وَأَمَّا بِنِعْمَةِ رَبِّكَ فَحَدِّثْ", "And as for the favor of your Lord, report [it].", "أظهر نعم الله واشكره عليها واذكر فضله")
    )

    val sharh = listOf(
        Ayah(1, "أَلَمْ نَشْرَحْ لَكَ صَدْرَكَ", "Did We not expand for you, [O Muhammad], your breast?", "شرح الله صدر نبيه للإسلام والنبوة والخير"),
        Ayah(2, "وَوَضَعْنَا عَنكَ وِزْرَكَ", "And We removed from you your burden", "وغفرنا لك وحططنا عنك أثقال الهموم"),
        Ayah(3, "الَّذِي أَنقَضَ ظَهْرَكَ", "Which had weighed upon your back", "الذي أثقل حملك"),
        Ayah(4, "وَرَفَعْنَا لَكَ ذِكْرَكَ", "And raised high for you your repute.", "قرن اسمك مع اسمه تعالى في الأذان والشهادة"),
        Ayah(5, "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا", "For indeed, with hardship [will be] ease.", "إن مع الضيق والشدة فرجاً وتيسيراً قريباً"),
        Ayah(6, "إِنَّ مَعَ الْعُسْرِ يُسْرًا", "Indeed, with hardship [will be] ease.", "تأكيد وبشارة بأن اليسر يغلب العسر لا محالة"),
        Ayah(7, "فَإِذَا فَرَغْتَ فَانصَبْ", "So when you have finished [your duties], then stand up [for worship].", "إذا فرغت من أعمالك فاجتهد في العبادة والدعاء"),
        Ayah(8, "وَإِلَىٰ رَبِّكَ فَارْغَب", "And to your Lord direct [your] longing.", "واقبل على ربك راغباً فيما عنده مخلصاً")
    )

    val tin = listOf(
        Ayah(1, "وَالتِّينِ وَالزَّيْتُونِ", "By the fig and the olive", "قسم بشجر التين والزيتون في أرض الشام المباركة"),
        Ayah(2, "وَطُورِ سِينِينَ", "And [by] Mount Sinai", "وجبل الطور الذي كلم الله عليه موسى تكليماً"),
        Ayah(3, "وَهَٰذَا الْبَلَدِ الْأَمِينِ", "And [by] this secure city [Makkah],", "وبمكة المكرمة بلد الأمن والأمان ومهبط الوحي"),
        Ayah(4, "لَقَدْ خَلَقْنَا الْإِنسَانَ فِي أَحْسَنِ تَقْوِيمٍ", "We have certainly created man in the best of stature;", "في أكمل صورة وأجمل هيئة وتعديل"),
        Ayah(5, "ثُمَّ رَدَدْنَاهُ أَسْفَلَ سَافِلِينَ", "Then We return him to the lowest of the low,", "إلى النار إن كفر وتولى"),
        Ayah(6, "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ فَلَهُمْ أَجْرٌ غَيْرُ مَمْنُونٍ", "Except for those who believe and do righteous deeds, for they will have a reward uninterrupted.", "لهم أجر مستمر دائم غير منقطع"),
        Ayah(7, "فَمَا يُكَذِّبُكَ بَعْدُ بِالدِّينِ", "So what yet causes you to deny the Recompense?", "ما يدفعك للكفر بالجزاء والحساب بعد هذه الدلائل"),
        Ayah(8, "أَلَيْسَ اللَّهُ بِأَحْكَمِ الْحَاكِمِينَ", "Is not Allah the most just of judges?", "بلى وهو سبحانه أحكم الحاكمين وأعدلهم")
    )

    val alaq = listOf(
        Ayah(1, "اقْرَأْ بِاسْمِ رَبِّكَ الَّذِي خَلَقَ", "Recite in the name of your Lord who created -", "أول ما نزل من الوحي العظيم"),
        Ayah(2, "خَلَقَ الْإِنسَانَ مِنْ عَلَقٍ", "Created man from a clinging substance.", "من قطعة دم متصلة بالرحم إعجازاً"),
        Ayah(3, "اقْرَأْ وَرَبُّكَ الْأَكْرَمُ", "Recite, and your Lord is the most Generous -", "ربك العظيم واسع الفضل والجود"),
        Ayah(4, "الَّذِي عَلَّمَ بِالْقَلَمِ", "Who taught by the pen -", "علم البشر الكتابة والعلوم بالقلم"),
        Ayah(5, "عَلَّمَ الْإِنسَانَ مَا لَمْ يَعْلَمْ", "Taught man that which he knew not.", "أخرجه من ظلمات الجهل إلى نور العلم")
    )

    val qadr = listOf(
        Ayah(1, "إِنَّا أَنزَلْنَاهُ فِي لَيْلَةِ الْقَدْرِ", "Indeed, We sent the Qur'an down during the Night of Decree.", "أنزلنا القرآن الكريم في ليلة القدر المباركة"),
        Ayah(2, "وَمَا أَدْرَاكَ مَا لَيْلَةُ الْقَدْرِ", "And what can make you know what is the Night of Decree?", "تعظيم لشأن هذه الليلة المباركة وفضلها"),
        Ayah(3, "لَيْلَةُ الْقَدْرِ خَيْرٌ مِّنْ أَلْفِ شَهْرٍ", "The Night of Decree is better than a thousand months.", "العمل الصالح فيها خير من عمل ألف شهر ليس فيها ليلة قدر"),
        Ayah(4, "تَنَزَّلُ الْمَلَائِكَةُ وَالرُّوحُ فِيهَا بِإِذْنِ رَبِّهِم مِّن كُلِّ أَمْرٍ", "The angels and the Spirit descend therein by permission of their Lord for every matter.", "تنزل الملائكة وجبريل بالبركات والسلام"),
        Ayah(5, "سَلَامٌ هِيَ حَتَّىٰ مَطْلَعِ الْفَجْرِ", "Peace it is until the emergence of dawn.", "أمن وسلام وخير كله حتى مطلع فجرها")
    )

    val bayyinah = listOf(
        Ayah(1, "لَمْ يَكُنِ الَّذِينَ كَفَرُوا مِنْ أَهْلِ الْكِتَابِ وَالْمُشْرِكِينَ مُنفَكِّينَ حَتَّىٰ تَأْتِيَهُمُ الْبَيِّنَةُ", "Those who disbelieved were not to be parted until there came to them clear evidence -", "حجة واضحة وبرهان بيّن"),
        Ayah(2, "رَسُولٌ مِّنَ اللَّهِ يَتْلُو صُحُفًا مُّطَهَّرَةً", "A Messenger from Allah, reciting purified scriptures", "محمد ﷺ يتلو كتاباً منزهاً عن الباطل"),
        Ayah(3, "فِيهَا كُتُبٌ قَيِّمَةٌ", "Within which are correct writings.", "أحكام عادلة مستقيمة"),
        Ayah(5, "وَمَا أُمِرُوا إِلَّا لِيَعْبُدُوا اللَّهَ مُخْلِصِينَ لَهُ الدِّينَ حُنَفَاءَ وَيُقِيمُوا الصَّلَاةَ وَيُؤْتُوا الزَّكَاةَ ۚ وَذَٰلِكَ دِينُ الْقَيِّمَةِ", "And they were not commanded except to worship Allah, [being] sincere to Him in religion, inclining to truth, and to establish prayer and to give zakah. And that is the correct religion.", "أساس الدين إخلاص العبادة لله وحده وإقام الصلاة وإيتاء الزكاة"),
        Ayah(7, "إِنَّ الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ أُولَٰئِكَ هُمْ خَيْرُ الْبَرِيَّةِ", "Indeed, they who have believed and done righteous deeds - those are the best of creatures.", "خير من برأ الله وخلق")
    )

    val zalzalah = listOf(
        Ayah(1, "إِذَا زُلْزِلَتِ الْأَرْضُ زِلْزَالَهَا", "When the earth is shaken with its [final] earthquake", "اضطربت الأرض وتحركت لقيام الساعة"),
        Ayah(2, "وَأَخْرَجَتِ الْأَرْضُ أَثْقَالَهَا", "And the earth discharges its burdens", "أخرجت موتاها ودفائنها التي في جوفها"),
        Ayah(3, "وَقَالَ الْإِنسَانُ مَا لَهَا", "And man says, What is [wrong] with it? -", "دهشة الناس وذهولهم من هول المشهد"),
        Ayah(4, "يَوْمَئِذٍ تُحَدِّثُ أَخْبَارَهَا", "That Day, it will report its news", "تشهد الأرض بما عُمل عليها من خير أو شر"),
        Ayah(5, "بِأَنَّ رَبَّكَ أَوْحَىٰ لَهَا", "Because your Lord has commanded it.", "بأمر الله وإذنه"),
        Ayah(6, "يَوْمَئِذٍ يَصْدُرُ النَّاسُ أَشْتَاتًا لِّيُرَوْا أَعْمَالَهُمْ", "That Day, the people will depart separated [into categories] to be shown [the result of] their deeds.", "يتفرقون لمشاهدة جزاء أعمالهم"),
        Ayah(7, "فَمَن يَعْمَلْ مِثْقَالَ ذَرَّةٍ خَيْرًا يَرَهُ", "So whoever does an atom's weight of good will see it,", "أصغر ذرة من الخير لا تضيع عند الله"),
        Ayah(8, "وَمَن يَعْمَلْ مِثْقَالَ ذَرَّةٍ شَرًّا يَرَهُ", "And whoever does an atom's weight of evil will see it.", "وأصغر ذرة من الشر مدونة في كتاب العبد")
    )

    val adiyat = listOf(
        Ayah(1, "وَالْعَادِيَاتِ ضَبْحًا", "By the racers, panting,", "قسم بالخيل الجارية في سبيل الله يسمع صوت نفسها"),
        Ayah(2, "فَالْمُورِيَاتِ قَدْحًا", "And the producers of sparks [when] striking", "تقدح الشرر بحوافرها في الصخور"),
        Ayah(3, "فَالْمُغِيرَاتِ صُبْحًا", "And the chargers at dawn,", "المباغتة للأعداء وقت الصباح"),
        Ayah(4, "فَأَثَرْنَ بِهِ نَقْعًا", "Stirring up thereby [clouds of] dust,", "تثير الغبار في المعركة"),
        Ayah(5, "فَوَسَطْنَ بِهِ جَمْعًا", "Arriving thereby in the center collectively,", "تتوسط صفوف الأعداء بشجاعة"),
        Ayah(6, "إِنَّ الْإِنسَانَ لِرَبِّهِ لَكَنُودٌ", "Indeed mankind, to his Lord, is ungrateful.", "كفور جحود بنعم الله"),
        Ayah(7, "وَإِنَّهُ عَلَىٰ ذَٰلِكَ لَشَهِيدٌ", "And indeed, to that he is to witness.", "مقر بجحوده بلسان حاله"),
        Ayah(8, "وَإِنَّهُ لِحُبِّ الْخَيْرِ لَشَدِيدٌ", "And indeed he is, in love of wealth, intense.", "شديد الحرص على حب المال والدنيا"),
        Ayah(9, "أَفَلَا يَعْلَمُ إِذَا بُعْثِرَ مَا فِي الْقُبُورِ", "But does he not know that when the contents of the graves are scattered", "إذا خرج الأموات للجزاء"),
        Ayah(10, "وَحُصِّلَ مَا فِي الصُّدُورِ", "And that within the breasts is brought out,", "وأظهر ما كانت تكنه القلوب من النوايا"),
        Ayah(11, "إِنَّ رَبَّهُم بِهِمْ يَوْمَئِذٍ لَّخَبِيرٌ", "Indeed, their Lord with them, that Day, is [fully] Acquainted.", "خبير بهم وبكل صغيرة وكبيرة")
    )

    val qariah = listOf(
        Ayah(1, "الْقَارِعَةُ", "The Striking Calamity -", "القيامة التي تقرع القلوب بهولها"),
        Ayah(2, "مَا الْقَارِعَةُ", "What is the Striking Calamity?", "تهويل وتعظيم لشأنها"),
        Ayah(3, "وَمَا أَدْرَاكَ مَا الْقَارِعَةُ", "And what can make you know what is the Striking Calamity?", "لا يدري حقيقتها إلا الله"),
        Ayah(4, "يَوْمَ يَكُونُ النَّاسُ كَالْفَرَاشِ الْمَبْثُوثِ", "It is the Day when people will be like moths, dispersed,", "كالجراد والفراش المنتشر في حيرته واضطرابه"),
        Ayah(5, "وَتَكُونُ الْجِبَالُ كَالْعِهْنِ الْمَنفُوشِ", "And the mountains will be like wool, fluffed up.", "كالصوف المنفوش خفة وزوالاً"),
        Ayah(6, "فَأَمَّا مَن ثَقُلَتْ مَوَازِينُهُ", "Then as for one whose scales are heavy [with good deeds],", "رجحت حسناته على سيئاته"),
        Ayah(7, "فَهُوَ فِي عِيشَةٍ رَّاضِيَةٍ", "He will be in a pleasant life.", "في جنات النعيم والرضوان"),
        Ayah(8, "وَأَمَّا مَنْ خَفَّتْ مَوَازِينُهُ", "But as for one whose scales are light,", "غلب شره خيره"),
        Ayah(9, "فَأُمُّهُ هَاوِيَةٌ", "His refuge will be an abyss.", "مأواه قعر جهنم يهوي فيها"),
        Ayah(10, "وَمَا أَدْرَاكَ مَا هِيَهْ", "And what can make you know what that is?", "تعظيم لنارها وحرها"),
        Ayah(11, "نَارٌ حَامِيَةٌ", "It is a Fire, intensely hot.", "قد بلغت أقصى درجات الإحراق")
    )

    val takathur = listOf(
        Ayah(1, "أَلْهَاكُمُ التَّكَاثُرُ", "Competition in [worldly] increase diverts you", "شغلكم التفاخر بالأموال والأولاد عن طاعة الله"),
        Ayah(2, "حَتَّىٰ زُرْتُمُ الْمَقَابِرَ", "Until you visit the graveyards.", "حتى وافاكم الموت وصرتم في القبور"),
        Ayah(3, "كَلَّا سَوْفَ تَعْلَمُونَ", "No! You are going to know.", "ردع وإنذار: ستعلمون عاقبة هذا اللهو"),
        Ayah(4, "ثُمَّ كَلَّا سَوْفَ تَعْلَمُونَ", "Then no! You are going to know.", "تأكيد بعد تأكيد على التحذير"),
        Ayah(5, "كَلَّا لَوْ تَعْلَمُونَ عِلْمَ الْيَقِينِ", "No! If you only knew with knowledge of certainty...", "لو علمتم حقيقة الأمر يقيناً لما تلهيتم"),
        Ayah(6, "لَتَرَوُنَّ الْجَحِيمَ", "You will surely see the Hellfire.", "لتشاهدن النار بأعينكم"),
        Ayah(7, "ثُمَّ لَتَرَوُنَّهَا عَيْنَ الْيَقِينِ", "Then you will surely see it with the eye of certainty.", "رؤية حقيقية لا شك فيها"),
        Ayah(8, "ثُمَّ لَتُسْأَلُنَّ يَوْمَئِذٍ عَنِ النَّعِيمِ", "Then you will surely be asked that Day about pleasure.", "لتسألن عن شكر نعم الصحة والمال والأمن والفراغ")
    )

    val asr = listOf(
        Ayah(1, "وَالْعَصْرِ", "By time,", "قسم بالدهر والزمان لما فيه من العبر والآيات"),
        Ayah(2, "إِنَّ الْإِنسَانَ لَفِي خُسْرٍ", "Indeed, mankind is in loss,", "كل إنسان في هلاك ونقصان وخسران"),
        Ayah(3, "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ وَتَوَاصَوْا بِالْحَقِّ وَتَوَاصَوْا بِالصَّبْرِ", "Except for those who have believed and done righteous deeds and advised each other to truth and advised each other to patience.", "طريق النجاة: الإيمان والعمل الصالح والتناصح بالحق والصبر عليه")
    )

    val humazah = listOf(
        Ayah(1, "وَيْلٌ لِّكُلِّ هُمَزَةٍ لُّمَزَةٍ", "Woe to every scorner and mocker", "هلاك وعذاب شديد لمن يغتاب الناس ويعيبهم"),
        Ayah(2, "الَّذِي جَمَعَ مَالًا وَعَدَّدَهُ", "Who collects wealth and [continuously] counts it.", "شغلته دنياه بجمع المال وتعديده وحرمانه من الخير"),
        Ayah(3, "يَحْسَبُ أَنَّ مَالَهُ أَخْلَدَهُ", "He thinks that his wealth will make him immortal.", "ظن أن ماله يخلده في الدنيا وينجيه من الموت"),
        Ayah(4, "كَلَّا ۖ لَيُنبَذَنَّ فِي الْحُطَمَةِ", "No! He will surely be thrown into the Crusher.", "ليقذفن في نار جهنم التي تحطم كل ما يلقى فيها"),
        Ayah(5, "وَمَا أَدْرَاكَ مَا الْحُطَمَةُ", "And what can make you know what is the Crusher?", "تعظيم لأمرها وشدتها"),
        Ayah(6, "نَارُ اللَّهِ الْمُوقَدَةُ", "It is the fire of Allah, [eternally] fueled,", "نار أوقدها الله بقدرته"),
        Ayah(7, "الَّتِي تَطَّلِعُ عَلَى الْأَفْئِدَةِ", "Which mounts directed at the hearts.", "تصل حرارتها إلى أعماق القلوب والأفئدة"),
        Ayah(8, "إِنَّهَا عَلَيْهِم مُّؤْصَدَةٌ", "Indeed, Hellfire will be closed down upon them", "مغلقة عليهم لا يخرجون منها أبداً"),
        Ayah(9, "فِي عَمَدٍ مُّمَدَّدَةٍ", "In extended columns.", "موثقون فيها بأعمدة وسلاسل")
    )

    val fil = listOf(
        Ayah(1, "أَلَمْ تَرَ كَيْفَ فَعَلَ رَبُّكَ بِأَصْحَابِ الْفِيلِ", "Have you not considered, [O Muhammad], how your Lord dealt with the companions of the elephant?", "تذكير بقصة أبرهة وجيشه الذي قصد هدم الكعبة"),
        Ayah(2, "أَلَمْ يَجْعَلْ كَيْدَهُمْ فِي تَضْلِيلٍ", "Did He not make their plan into misguidance?", "أبطل مكرهم وجعله وبالاً عليهم"),
        Ayah(3, "وَأَرْسَلَ عَلَيْهِمْ طَيْرًا أَبَابِيلَ", "And He sent against them birds in flocks,", "طير متتابعة جماعات جماعات"),
        Ayah(4, "تَرْمِيهِم بِحِجَارَةٍ مِّن سِجِّيلٍ", "Striking them with stones of hard clay,", "ترمي كل واحد بحجر محرق من طين يابس"),
        Ayah(5, "فَجَعَلَهُمْ كَعَصْفٍ مَّأْكُولٍ", "And He made them like eaten straw.", "كأوراق الزرع التي أكلتها الدواب وداستها")
    )

    val quraysh = listOf(
        Ayah(1, "لِإِيلَافِ قُرَيْشٍ", "For the accustomed security of the Quraysh -", "تألف قريش واجتماع شملها"),
        Ayah(2, "إِيلَافِهِمْ رِحْلَةَ الشِّتَاءِ وَالصَّيْفِ", "Their accustomed security [in] the caravan of winter and summer -", "رحلتا التجارة إلى اليمن شتاءً وإلى الشام صيفاً بأمن وسلام"),
        Ayah(3, "فَلْيَعْبُدُوا رَبَّ هَٰذَا الْبَيْتِ", "Let them worship the Lord of this House,", "فليعبدوا الله رب الكعبة المشرفة شكراً على نعمه"),
        Ayah(4, "الَّذِي أَطْعَمَهُم مِّن جُوعٍ وَآمَنَهُم مِّنْ خَوْفٍ", "Who has fed them, [saving them] from hunger and made them safe, [saving them] from fear.", "أعطاهم الرزق والأمن وهما أعظم أسباب الحياة الكريمة")
    )

    val maun = listOf(
        Ayah(1, "أَرَأَيْتَ الَّذِي يُكَذِّبُ بِالدِّينِ", "Have you seen the one who denies the Recompense?", "هل رأيت حال من يكذب بالبعث والجزاء"),
        Ayah(2, "فَذَٰلِكَ الَّذِي يَدُعُّ الْيَتِيمَ", "For that is the one who drives away the orphan", "يقهر اليتيم ويدفعه بعنف عن حقه"),
        Ayah(3, "وَلَا يَحُضُّ عَلَىٰ طَعَامِ الْمِسْكِينِ", "And does not encourage the feeding of the poor.", "لا يحث نفسه ولا غيره على إطعام المحتاج"),
        Ayah(4, "فَوَيْلٌ لِّلْمُصَلِّينَ", "So woe to those who pray", "هلاك ووعيد للغافلين عن صلاتهم"),
        Ayah(5, "الَّذِينَ هُمْ عَن صَلَاتِهِمْ سَاهُونَ", "[But] who are heedless of their prayer -", "يؤخرونها عن أوقاتها ولا يبالون بها"),
        Ayah(6, "الَّذِينَ هُمْ يُرَاءُونَ", "Those who make a show [of their deeds]", "يراؤون الناس بأعمالهم طلباً للمدح"),
        Ayah(7, "وَيَمْنَعُونَ الْمَاعُونَ", "And withhold [simple] assistance.", "يمنعون إعارة الأشياء البسيطة التي يحتاجها الجار")
    )

    val kawthar = listOf(
        Ayah(1, "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "Indeed, We have granted you, [O Muhammad], al-Kawthar.", "أعطيناك يا محمد الخير الكثير ومنه نهر الكوثر في الجنة"),
        Ayah(2, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "So pray to your Lord and sacrifice [to Him alone].", "أخلص صلاتك ونحرك لله وحده لا للأوثان"),
        Ayah(3, "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", "Indeed, your enemy is the one cut off.", "إن مبغضك هو المنقطع ذكره وأثره من كل خير")
    )

    val kafirun = listOf(
        Ayah(1, "قُلْ يَا أَيُّهَا الْكَافِرُونَ", "Say, O disbelievers,", "إعلان براءة صريح من الشرك وأهله"),
        Ayah(2, "لَا أَعْبُدُ مَا تَعْبُدُونَ", "I do not worship what you worship.", "لا أعبد أصنامكم ومعبوداتكم الباطلة"),
        Ayah(3, "وَلَا أَنتُمْ عَابِدُونَ مَا أَعْبُدُ", "Nor are you worshippers of what I worship.", "ولا أنتم مخلصون العبادة لإلهي الحق"),
        Ayah(4, "وَلَا أَنَا عَابِدٌ مَّا عَبَدتُّمْ", "Nor will I be a worshipper of what you worship.", "توكيد على الثبات التام على التوحيد"),
        Ayah(5, "وَلَا أَنتُمْ عَابِدُونَ مَا أَعْبُدُ", "Nor will you be worshippers of what I worship.", "توكيد على براءة التوحيد من الشرك"),
        Ayah(6, "لَكُمْ دِينُكُمْ وَلِيَ دِينِ", "For you is your religion, and for me is my religion.", "لكم شرككم وباطلوكم ولي توحيدي وديني الحق")
    )

    val nasr = listOf(
        Ayah(1, "إِذَا جَاءَ نَصْرُ اللَّهِ وَالْفَتْحُ", "When the victory of Allah has come and the conquest,", "نصر الله لدينه وفتح مكة المكرمة"),
        Ayah(2, "وَرَأَيْتَ النَّاسَ يَدْخُلُونَ فِي دِينِ اللَّهِ أَفْوَاجًا", "And you see the people entering into the religion of Allah in multitudes,", "دخول القبائل والأمم في الإسلام جماعات"),
        Ayah(3, "فَسَبِّحْ بِحَمْدِ رَبِّكَ وَاسْتَغْفِرْهُ ۚ إِنَّهُ كَانَ تَوَّابًا", "Then exalt [Him] with praise of your Lord and ask forgiveness of Him. Indeed, He is ever Accepting of repentance.", "شكر الله وتسبيحه والاستغفار عند إتمام النعم والفتوح")
    )

    val masad = listOf(
        Ayah(1, "تَبَّتْ يَدَا أَبِي لَهَبٍ وَتَبَّ", "May the hands of Abu Lahab be ruined, and ruined is he.", "خسرت يدا أبي لهب وهلك وخسر عمله"),
        Ayah(2, "مَا أَغْنَىٰ عَنْهُ مَالُهُ وَمَا كَسَبَ", "His wealth will not avail him or that which he gained.", "ما نفعه ماله ولا جاهه من عذاب الله"),
        Ayah(3, "سَيَصْلَىٰ نَارًا ذَاتَ لَهَبٍ", "He will [enter to] burn in a Fire of [blazing] flame", "سيدخل ناراً متأججة عظيمة اللهب"),
        Ayah(4, "وَامْرَأَتُهُ حَمَّالَةَ الْحَطَبِ", "And his wife [as well] - the carrier of firewood.", "امرأته التي كانت تؤذي النبي ﷺ وتعين على الباطل"),
        Ayah(5, "فِي جِيدِهَا حَبْلٌ مِّن مَّسَدٍ", "Around her neck is a rope of [twisted] fiber.", "في عنقها حبل محكم من ليف النار تسحب به")
    )

    val ikhlas = listOf(
        Ayah(1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Say, He is Allah, [who is] One,", "الله الأحد المتفرد بالجلال والكمال الذي لا شريك له"),
        Ayah(2, "اللَّهُ الصَّمَدُ", "Allah, the Eternal Refuge.", "السيد الذي تصمد وتقصد الخلائق في كل حوائجها"),
        Ayah(3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "He neither begets nor is born,", "منزه عن الولد والوالد والشريك والنظير"),
        Ayah(4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "Nor is there to Him any equivalent.", "ليس كمثله شيء وهو السميع البصير")
    )

    val falaq = listOf(
        Ayah(1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Say, I seek refuge in the Lord of daybreak", "ألتجئ وأعتصم برب الصبح وفالقه"),
        Ayah(2, "مِن شَرِّ مَا خَلَقَ", "From the evil of that which He created", "من شر كل ذي شر خلقه الله"),
        Ayah(3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "And from the evil of darkness when it settles", "من شر الليل إذا أقبل واشتد ظلامه"),
        Ayah(4, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "And from the evil of the blowers in knots", "من شر الساحرات والحاسدين الساعين بالأذى"),
        Ayah(5, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "And from the evil of an envier when he envies.", "من شر العين والحسد وكيد الحاقدين")
    )

    val nas = listOf(
        Ayah(1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "Say, I seek refuge in the Lord of mankind,", "أعتصم برب البشر وخالقهم ومدبرهم"),
        Ayah(2, "مَلِكِ النَّاسِ", "The Sovereign of mankind,", "الملك الحق المتصرف في شؤون خلقه"),
        Ayah(3, "إِلَٰهِ النَّاسِ", "The God of mankind,", "المعبود الحق الذي لا إله غيره"),
        Ayah(4, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "From the evil of the retreating whisperer -", "من شر الشيطان الذي يوسوس عند الغفلة ويخنس عند ذكر الله"),
        Ayah(5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Who whispers into the breasts of mankind -", "يبث الشكوك والشهوات في صدور العباد"),
        Ayah(6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "From among the jinn and mankind.", "من شياطين الإنس وشياطين الجن")
    )

    val yaseenSample = listOf(
        Ayah(1, "يس", "Ya, Seen.", "حروف مقطعة تدل على إعجاز القرآن الكريم"),
        Ayah(2, "وَالْقُرْآنِ الْحَكِيمِ", "By the wise Qur'an.", "قسم بالقرآن المحكم في أحكامه وأخباره"),
        Ayah(3, "إِنَّكَ لَمِنَ الْمُرْسَلِينَ", "Indeed you, [O Muhammad], are from among the messengers,", "شهادة من الله لرسوله بالرسالة والصدق"),
        Ayah(4, "عَلَىٰ صِرَاطٍ مُّسْتَقِيمٍ", "On a straight path.", "على دين الحق والتوحيد الواضح"),
        Ayah(5, "تَنزِيلَ الْعَزِيزِ الرَّحِيمِ", "[This is] a revelation of the Exalted in Might, the Merciful,", "منزل من رب العزة والرحمة"),
        Ayah(58, "سَلَامٌ قَوْلًا مِّن رَّبٍّ رَّحِيمٍ", "[And] Peace, a word from a Merciful Lord.", "تحية أهل الجنة من ربهم الرحيم تفيض سكينة ورضواناً")
    )

    val rahmanSample = listOf(
        Ayah(1, "الرَّحْمَٰنُ", "The Most Merciful", "افتتاح باسم الرحمن المفيض بالرحمة والفضل"),
        Ayah(2, "عَلَّمَ الْقُرْآنَ", "Taught the Qur'an,", "أعظم النعم تعليم القرآن وهداية الخلق به"),
        Ayah(3, "خَلَقَ الْإِنسَانَ", "Created man,", "أبدع خلق الإنسان وصوره"),
        Ayah(4, "عَلَّمَهُ الْبَيَانَ", "Taught him speech.", "علمه النطق والتعبير عما في ضميره"),
        Ayah(13, "فَبِأَيِّ آلَاءِ رَبِّكُمَا تُكَذِّبَانِ", "So which of the favors of your Lord would you deny?", "فبأي نعم الله تكذبان يا معشر الإنس والجن")
    )

    val waqiahSample = listOf(
        Ayah(1, "إِذَا وَقَعَتِ الْوَاقِعَةُ", "When the Occurrence occurs,", "إذا قامت القيامة ووقعت الواقعة"),
        Ayah(2, "لَيْسَ لِوَقْعَتِهَا كَاذِبَةٌ", "There is, at its occurrence, no denial.", "حق لا ريب فيها ولا تكذيب"),
        Ayah(3, "خَافِضَةٌ رَّافِعَةٌ", "It will bring down [some] and raise up [others].", "تخفض الكفار في الدركات وترفع المؤمنين في الدرجات العلى")
    )

    val kahfSample = listOf(
        Ayah(1, "الْحَمْدُ لِلَّهِ الَّذِي أَنزَلَ عَلَىٰ عَبْدِهِ الْكِتَابَ وَلَمْ يَجْعَل لَّهُ عِوَجًا ۜ", "[All] praise is [due] to Allah, who has sent down upon His Servant the Book and has not made therein any deviance.", "الثناء لله الذي أنزل القرآن مستقيماً لا خلل فيه"),
        Ayah(2, "قَيِّمًا لِّيُنذِرَ بَأْسًا شَدِيدًا مِّن لَّدُنْهُ وَيُبَشِّرَ الْمُؤْمِنِينَ الَّذِينَ يَعْمَلُونَ الصَّالِحَاتِ أَنَّ لَهُمْ أَجْرًا حَسَنًا", "[He has made it] straight, to warn of severe punishment from Him and to give good tidings to the believers who do righteous deeds that they will have a good reward", "بشارة للمؤمنين بالجنة ونذارة للكافرين"),
        Ayah(10, "إِذْ أَوَى الْفِتْيَةُ إِلَى الْكَهْفِ فَقَالُوا رَبَّنَا آتِنَا مِن لَّدُنكَ رَحْمَةً وَهَيِّئْ لَنَا مِنْ أَمْرِنَا رَشَدًا", "[Mention] when the youths retreated to the cave and said, Our Lord, grant us from Yourself mercy and prepare for us from our affair right guidance.", "دعاء أهل الكهف بالرحمة والرشد عند الشدائد"),
        Ayah(107, "إِنَّ الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ كَانَتْ لَهُمْ جَنَّاتُ الْفِرْدَوْسِ نُزُلًا", "Indeed, those who have believed and done righteous deeds - they will have the Gardens of Paradise as a lodging,", "مستقر المؤمنين جنات الفردوس العلى"),
        Ayah(110, "قُلْ إِنَّمَا أَنَا بَشَرٌ مِّثْلُكُمْ يُوحَىٰ إِلَيَّ أَنَّمَا إِلَٰهُكُمْ إِلَٰهٌ وَاحِدٌ ۖ فَمَن كَانَ يَرْجُو لِقَاءَ رَبِّهِ فَلْيَعْمَلْ عَمَلًا صَالِحًا وَلَا يُشْرِكْ بِعِبَادَةِ رَبِّهِ أَحَدًا", "Say, I am only a man like you, to whom has been revealed that your god is one God. So whoever would hope for the meeting with his Lord - let him do righteous work and not associate in the worship of his Lord anyone.", "ختام سورة الكهف: الإخلاص والعمل الصالح شرط لقاء الله والفوز برضوانه")
    )
}
