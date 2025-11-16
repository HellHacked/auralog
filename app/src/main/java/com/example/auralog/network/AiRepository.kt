package com.example.auralog.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.math.roundToInt

class AiRepository {

    @Serializable
    data class ChatMessage(val role: String, val content: String)

    @Serializable
    data class ChatRequest(val model: String, val messages: List<ChatMessage>)

    @Serializable
    data class Choice(val message: ChatMessage)

    @Serializable
    data class ChatResponse(val choices: List<Choice>)

    fun predictMigraine(prompt: String): Int {
        val client = OkHttpClient()
        val apiKey = "rc_65515aac951f0b34c720b87d286b8a4c5634f37b4d0e80f57e0e071141205886"
        val requestBody = Json.encodeToString(ChatRequest.serializer(), ChatRequest(
            model = "HPAI-BSC/Qwen2.5-Aloe-Beta-72B",
            messages = listOf(ChatMessage("user", prompt))
        )).toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://api.featherless.ai/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        var sum = 0
        var successCount = 0
        var totalCount = 0

        while (successCount < 10) {
            if (totalCount > 25) {
                sum = 0
                Log.d("AiRepository", "API limit reached, resetting sum to 0")
                break
            }
            totalCount += 1
            try {
                val response = client.newCall(request).execute()
                val bodyStr = response.body?.string() ?: continue

                Log.d("AiRepository", "Raw API response: $bodyStr")

                val decoded = Json { ignoreUnknownKeys = true }
                    .decodeFromString(ChatResponse.serializer(), bodyStr)

                val content = decoded.choices.firstOrNull()?.message?.content ?: continue

                val number = content.toFloatOrNull()?.roundToInt() ?: continue

                sum += number
                successCount++

            } catch (e: Exception) {
                Log.e("AiRepository", "Prediction attempt failed", e)
            }
        }

        return sum / 10
    }

}
