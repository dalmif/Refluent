package io.kayt.refluent.core.data

import com.google.ai.client.generativeai.GenerativeModel
import io.kayt.refluent.core.data.utils.exemptCancellation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenerativeRepository @Inject constructor() {
    private val modelName = "gemini-2.0-flash"
    private val apiKey = BuildConfig.GEMINI_API_KEY

    private val generativeModel = GenerativeModel(
        modelName = modelName,
        apiKey = apiKey
    )

    @Suppress("UseCheckOrError")
    suspend fun generateExampleSentences(word: String): Result<String> {
        val prompt = """
            Generate 3 example sentences using the word '$word'. Rules:
            - Format as numbered HTML list (<ol> and <li> tags)
            - The word '$word' should be wrapped in <b> tags
            - No text before or after the examples
            - No any "`" or any other markdown at all.
            - Complexity levels:
              1. First sentence: Very simple, like for beginners
              2. Second sentence: Medium difficulty, for intermediate learners and short
              3. Third sentence: Medium difficulty, for intermediate learners and short
            - Return pure HTML format only
            
            Expected format:
            <ol>
            <li>Simple sentence with <b>word</b></li>
            <li>Medium sentence with <b>word</b></li>
            <li>Medium sentence with <b>word</b></li>
            </ol>
        """.trimIndent()
        return runCatching {
            val response = generativeModel.generateContent(prompt)
            response.text
                ?.replace("```html", "")
                ?.replace("```", "")
                ?.trim() ?: throw IllegalStateException("The response is null")
        }.exemptCancellation()
    }

    @Suppress("UseCheckOrError")
    suspend fun generateDefinition(word: String): Result<String> {
        val prompt = """
                Define the word '$word'. Rules:
                - Return pure HTML format only
                - No text before or after the definition
                - No any "`" or any other markdown at all.
                - Do not use any code block markers or markdown formatting
                - Structure the response as follows:
                  1. Main definition (short and simple)
                  2. Word type (noun/verb/adj/etc)
                  3. No example sentences.
                  4. Synonyms (if applicable)
                - Use HTML formatting:
                  - <b> for important terms
                  - <i> for word types and linguistic notes
                  - <mark> for key concepts
                  - <ol> and <li> for lists
                  - <br> for line breaks
                
                Expected format:
                <div>
                <b>word</b> (<i>part of speech</i>) - Main definition here<br><br>
                <mark>Key usage notes:</mark><br>
                <ol>
                <li>Example usage 1</li>
                <li>Example usage 2</li>
                </ol>
                <br>
                <i>Synonyms:</i> word1, word2, word3
                </div>
        """.trimIndent()
        return runCatching {
            val response = generativeModel.generateContent(prompt)
            response.text
                ?.replace("```html", "")
                ?.replace("```", "")
                ?.trim() ?: throw IllegalStateException("The response is null")
        }.exemptCancellation()
    }
}
