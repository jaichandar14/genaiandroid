package com.bpm.genmobai.app.base

import android.app.Application
import android.content.Context
import android.os.StrictMode
import com.bpm.genmobai.di.dagger2.AppComponent
import com.bpm.genmobai.di.dagger2.DaggerAppComponent
import com.bpm.genmobai.di.dagger2.retrofit.NetworkModule


class MyApplication : Application() {
    var TAG: String = this.javaClass.simpleName

    companion object {
        lateinit var appContext: MyApplication
        var applicationComponent: AppComponent? = null
        fun getAppContextInitialization(): Boolean = ::appContext.isInitialized
    }

    operator fun get(context: Context): MyApplication {
        return context.applicationContext as MyApplication
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        @Suppress("DEPRECATION")
        applicationComponent = DaggerAppComponent.builder()
            .networkModule(
                NetworkModule()
            ).build()

    }
}