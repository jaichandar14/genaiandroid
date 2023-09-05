package com.bpm.genmobai.utility

import com.bpm.genmobai.ui.dashboard.DashBoardActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://api.openai.com"
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val chatGptApi: DashBoardActivity.ChatGptApi by lazy {
        retrofit.create(DashBoardActivity.ChatGptApi::class.java)
    }
}