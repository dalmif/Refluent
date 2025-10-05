package io.kayt.refluent.core.data.utils

object CmuDictIpa {

    data class Options(
        val cotCaughtMerged: Boolean = false, // AO→ɑ if true
        val showSyllableDots: Boolean = false // insert · before each new vowel group
    )

    // Phones considered vowels in CMUdict (base without stress digit)
    private val VOWELS = setOf(
        "AA", "AE", "AH", "AO", "AW", "AY", "EH", "ER", "EY", "IH", "IY", "OW", "OY", "UH", "UW", "AX"
    )

    // Base ARPABET (no stress digits) → IPA (default mapping; AH/ER handled dynamically)
    private val BASE_MAP = mapOf(
        // Vowels (context-free; AH/ER handled below)
        "AA" to "ɑ",
        "AE" to "æ",
        "AO" to "ɔ", // maybe ɑ if cotCaughtMerged=true
        "AW" to "aʊ",
        "AY" to "aɪ",
        "EH" to "ɛ",
        "EY" to "eɪ",
        "IH" to "ɪ",
        "IY" to "i",
        "OW" to "oʊ",
        "OY" to "ɔɪ",
        "UH" to "ʊ",
        "UW" to "u",
        "AX" to "ə",

        // Consonants
        "B" to "b", "CH" to "t͡ʃ", "D" to "d", "DH" to "ð",
        "F" to "f", "G" to "ɡ", "HH" to "h", "JH" to "d͡ʒ",
        "K" to "k", "L" to "l", "M" to "m", "N" to "n",
        "NG" to "ŋ", "P" to "p", "R" to "ɹ", "S" to "s",
        "SH" to "ʃ", "T" to "t", "TH" to "θ", "V" to "v",
        "W" to "w", "Y" to "j", "Z" to "z", "ZH" to "ʒ",
        // Extras sometimes seen
        "DX" to "ɾ", // flap
        "Q" to "ʔ" // glottal stop
    )

    private val TOKEN_RE = Regex("^([A-Z]{1,3})([0-2])?$")

    fun toIpa(arpabet: String): String {
        return toIpa(arpabet, Options())
    }

    /**
     * Convert a CMUdict ARPABET string to IPA.
     * Example input: "K AH0 M P Y UW1 T ER0"
     */
    fun toIpa(arpabet: String, options: Options): String {
        val out = StringBuilder()
        var haveSeenVowel = false
        var prevWasVowel = false

        val tokens = arpabet.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        for (tok in tokens) {
            val m = TOKEN_RE.matchEntire(tok)
                ?: run { // unknown token; pass through as-is
                    if (out.isNotEmpty()) out.append(' ')
                    out.append(tok)
                    continue
                }

            val base = m.groupValues[1]
            val stress = m.groupValues.getOrNull(2)?.takeIf { it.isNotEmpty() }?.single()

            val isVowel = base in VOWELS

            // Syllable dot heuristic: before each new vowel group (except the very first)
            if (options.showSyllableDots && isVowel && haveSeenVowel && !prevWasVowel) {
                out.append('·')
            }

            // Stress marks: put right before the vowel symbol
            if (isVowel) {
                when (stress) {
                    '1' -> out.append('ˈ')
                    '2' -> out.append('ˌ')
                }
            }

            // Map to IPA, handling AH/ER and AO merge
            val ipa = when (base) {
                "AH" -> {
                    // schwa when unstressed, ʌ otherwise
                    if (stress == '0') "ə" else "ʌ"
                }
                "ER" -> {
                    // rhotic vowel: ɚ when unstressed, ɝ when stressed
                    if (stress == '0') "ɚ" else "ɝ"
                }
                "AO" -> if (options.cotCaughtMerged) "ɑ" else "ɔ"
                else -> BASE_MAP[base] ?: base.lowercase()
            }

            out.append(ipa)

            prevWasVowel = isVowel
            if (isVowel) haveSeenVowel = true
        }

        return out.toString()
    }
}
