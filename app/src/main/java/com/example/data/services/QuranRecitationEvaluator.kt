package com.example.data.services

import java.util.regex.Pattern

data class EvaluatedWord(
    val index: Int,
    val originalWord: String,
    val normalizedExpected: String,
    val isCorrect: Boolean,
    val spokenWord: String? = null,
    val errorMessage: String? = null
)

data class RecitationEvaluationResult(
    val words: List<EvaluatedWord>,
    val accuracyPercentage: Int,
    val totalWords: Int,
    val correctWordsCount: Int,
    val errorWordsCount: Int,
    val feedbackMessage: String,
    val spokenText: String
)

object QuranRecitationEvaluator {

    private val DIACRITICS_PATTERN = Pattern.compile("[\\u064B-\\u065F\\u0670\\u06D6-\\u06ED]")
    private val SYMBOLS_PATTERN = Pattern.compile("[ۖۗۚۛۜ۝۞0-9٠-٩\\p{Punct}]")

    fun normalizeArabic(text: String): String {
        var clean = DIACRITICS_PATTERN.matcher(text).replaceAll("")
        clean = SYMBOLS_PATTERN.matcher(clean).replaceAll("")
        return clean
            .replace('أ', 'ا')
            .replace('إ', 'ا')
            .replace('آ', 'ا')
            .replace('ٱ', 'ا')
            .replace('ى', 'ي')
            .replace('ة', 'ه')
            .replace('ؤ', 'و')
            .replace('ئ', 'ي')
            .trim()
            .replace(Regex("\\s+"), " ")
    }

    fun evaluate(expectedAyahText: String, spokenText: String): RecitationEvaluationResult {
        val originalWords = expectedAyahText.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        val normalizedExpectedWords = originalWords.map { normalizeArabic(it) }
        val spokenWords = spokenText.trim().split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .map { normalizeArabic(it) }

        val evaluatedWords = mutableListOf<EvaluatedWord>()
        var spokenIndex = 0

        for (i in normalizedExpectedWords.indices) {
            val exp = normalizedExpectedWords[i]
            val orig = originalWords.getOrElse(i) { exp }

            if (spokenIndex < spokenWords.size) {
                val spk = spokenWords[spokenIndex]
                if (wordsMatch(exp, spk)) {
                    evaluatedWords.add(
                        EvaluatedWord(
                            index = i,
                            originalWord = orig,
                            normalizedExpected = exp,
                            isCorrect = true,
                            spokenWord = spk
                        )
                    )
                    spokenIndex++
                } else {
                    // Check if the user skipped a word or next spoken word matches
                    val nextSpoken = spokenWords.getOrNull(spokenIndex + 1)
                    if (nextSpoken != null && wordsMatch(exp, nextSpoken)) {
                        evaluatedWords.add(
                            EvaluatedWord(
                                index = i,
                                originalWord = orig,
                                normalizedExpected = exp,
                                isCorrect = true,
                                spokenWord = nextSpoken
                            )
                        )
                        spokenIndex += 2
                    } else {
                        // Check if expected matches later
                        val nextExpected = normalizedExpectedWords.getOrNull(i + 1)
                        if (nextExpected != null && wordsMatch(nextExpected, spk)) {
                            // Current expected word was missed
                            evaluatedWords.add(
                                EvaluatedWord(
                                    index = i,
                                    originalWord = orig,
                                    normalizedExpected = exp,
                                    isCorrect = false,
                                    spokenWord = null,
                                    errorMessage = "كلمة مفقودة أو تم تخطيها"
                                )
                            )
                        } else {
                            evaluatedWords.add(
                                EvaluatedWord(
                                    index = i,
                                    originalWord = orig,
                                    normalizedExpected = exp,
                                    isCorrect = false,
                                    spokenWord = spk,
                                    errorMessage = "نطق غير دقيق: $spk"
                                )
                            )
                            spokenIndex++
                        }
                    }
                }
            } else {
                evaluatedWords.add(
                    EvaluatedWord(
                        index = i,
                        originalWord = orig,
                        normalizedExpected = exp,
                        isCorrect = false,
                        spokenWord = null,
                        errorMessage = "لم تُتلى هذه الكلمة"
                    )
                )
            }
        }

        val total = evaluatedWords.size.coerceAtLeast(1)
        val correct = evaluatedWords.count { it.isCorrect }
        val errors = total - correct
        val accuracy = ((correct.toFloat() / total.toFloat()) * 100).toInt()

        val feedback = when {
            accuracy >= 95 -> "ما شاء الله! تلاوة ممتازة ومتقنة للغاية 🌟"
            accuracy >= 80 -> "أحسنت! تلاوة جيدة جداً، انتبه للكلمات المحددة باللون الأحمر 🌿"
            accuracy >= 50 -> "محاولة طيبة، يرجى الاستماع للقارئ وتصحيح الكلمات باللون الأحمر ⚠️"
            else -> "يوجد أخطاء في التلاوة محددة بالأحمر، استمع للتلاوة الصحيحة وأعد المحاولة 🔄"
        }

        return RecitationEvaluationResult(
            words = evaluatedWords,
            accuracyPercentage = accuracy,
            totalWords = total,
            correctWordsCount = correct,
            errorWordsCount = errors,
            feedbackMessage = feedback,
            spokenText = spokenText
        )
    }

    private fun wordsMatch(w1: String, w2: String): Boolean {
        if (w1 == w2) return true
        if (w1.length > 2 && w2.length > 2) {
            val dist = levenshtein(w1, w2)
            if (dist <= 1) return true
        }
        return false
    }

    private fun levenshtein(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }
}
