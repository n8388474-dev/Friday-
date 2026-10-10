package com.friday.assistant.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {

    private const val API_KEY = "YAHAN_APNI_GEMINI_KEY_PASTE_KARO"

    private const val URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun ask(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }

            val body = json.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-goog-api-key", API_KEY)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: return@withContext "No response"

            val parsed = JSONObject(responseBody)
            val candidates = parsed.optJSONArray("candidates")
                ?: return@withContext "Error: $responseBody"

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            val text = parts.getJSONObject(0).getString("text")

            text
        } catch (e: Exception) {
            "Network error: ${e.message}"
        }
    }
}
