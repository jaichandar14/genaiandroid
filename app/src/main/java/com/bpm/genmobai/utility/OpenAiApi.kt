package com.bpm.genmobai.utility

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class OpenAiApi(val apiKey: String) {

    private val client = OkHttpClient()

    // Replace with the engine name you want to use (e.g., "davinci")
    private val engineName = "davinci"

    fun makeApiCall(
        prompt: String,
        maxTokens: Int = 50,
        completionCallback: (String?, Exception?) -> Unit
    ) {
        val json = JSONObject()
        json.put("prompt", prompt)
        json.put("max_tokens", 1000)

        val requestBody =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://api.openai.com/v1/engines/$engineName/completions")
            .post(requestBody)
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                completionCallback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body
                try {
                    if (response.isSuccessful && responseBody != null) {
                        val responseData = responseBody.string()
                        val jsonObject = JSONObject(responseData)
                        val choices = jsonObject.getJSONArray("choices")
                        if (choices.length() > 0) {
                            val text = choices.getJSONObject(0).getString("text")
                            Log.d("TAG", "onResponse: $text")
                            completionCallback(text, null)
                        } else {
                            completionCallback(null, Exception("No response text"))
                        }
                    } else {
                        completionCallback(
                            null,
                            Exception("API call failed with code ${response.code}")
                        )
                    }
                } catch (e: JSONException) {
                    completionCallback(null, e)
                } catch (e: IOException) {
                    completionCallback(null, e)
                } finally {
                    responseBody?.close()
                }
            }
        })
    }
}
