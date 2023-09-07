package com.bpm.genmobai.di.dagger2.retrofit

import com.bpm.genmobai.BuildConfig
import com.bpm.genmobai.data.api_service.PermissionDetails
import com.bpm.genmobai.data.repository.UserRepositoryImpl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class RetrofitHelper
@Inject
constructor(private val retrofit: Retrofit.Builder) {

    private fun getUserService(): PermissionDetails {
        val retrofitR = retrofit.baseUrl(BuildConfig.base_url)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return retrofitR.create(PermissionDetails::class.java)
    }

    var client = OkHttpClient.Builder().writeTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .callTimeout(1, TimeUnit.MINUTES)
        .build()

    fun getUserRepository(): UserRepositoryImpl {
        return UserRepositoryImpl(getUserService())
    }


}