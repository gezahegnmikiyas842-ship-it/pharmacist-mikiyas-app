package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(val text: String? = null)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val role: String? = "user",
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    private val clinicalSystemInstruction = GeminiContent(
        role = "system",
        parts = listOf(
            GeminiPart(
                text = """
                You are Pharmacist Mikiyas AI Clinical Assistant — a specialized, board-level clinical pharmacology and pharmacotherapy AI advisor for healthcare professionals, pharmacy students, and clinical practitioners.
                You are built on the expertise of Clinical Pharmacist Mikiyas Gezahegn.
                
                Your clinical expertise includes:
                1. Evidence-based drug information (Mechanism of action, pharmacokinetics, dosing guidelines, renal/hepatic adjustments, pediatric and geriatric considerations, pregnancy and lactation safety).
                2. Complex drug-drug, drug-food, and drug-disease interaction mechanisms and clinical management.
                3. Clinical calculations (Creatinine clearance Cockcroft-Gault, eGFR CKD-EPI, BMI/BSA/IBW, infusion drip rates, pediatric dosing rules, Glasgow Coma Scale).
                4. Patient counseling points, administration pearls, and adverse effect monitoring.
                5. Clinical treatment guidelines (AHA/ACC hypertension/HF, ADA diabetes, IDSA antimicrobial stewardship, GOLD COPD, GINA asthma).
                
                Structure your answers with:
                - Clear concise summary / Clinical takeaway
                - Pharmacological mechanism or clinical rationale
                - Key dosing or administration pearls (bulleted)
                - Monitoring parameters & adverse effects
                - Professional disclaimer that this advice is for educational & clinical decision support and does not replace institutional protocols or individualized patient evaluation.
                """.trimIndent()
            )
        )
    )

    suspend fun askClinicalAssistant(prompt: String, conversationHistory: List<Pair<String, Boolean>> = emptyList()): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Clinical AI Assistant is ready. To enable live Gemini responses, please configure your Gemini API Key in the AI Studio Secrets panel."
        }

        val contents = mutableListOf<GeminiContent>()
        
        // Add conversation history
        for ((msg, isUser) in conversationHistory.takeLast(6)) {
            contents.add(
                GeminiContent(
                    role = if (isUser) "user" else "model",
                    parts = listOf(GeminiPart(text = msg))
                )
            )
        }
        
        // Add current prompt
        contents.add(
            GeminiContent(
                role = "user",
                parts = listOf(GeminiPart(text = prompt))
            )
        )

        val request = GeminiRequest(
            contents = contents,
            systemInstruction = clinicalSystemInstruction
        )

        try {
            val response = service.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            text ?: "No clinical response received. Please rephrase your query."
        } catch (e: Exception) {
            Log.e("GeminiClient", "Clinical AI query error", e)
            "AI Assistant encountered a connection issue: ${e.localizedMessage ?: "Network error"}. Please check your connection or try again."
        }
    }
}
