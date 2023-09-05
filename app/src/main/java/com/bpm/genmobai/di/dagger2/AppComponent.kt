package com.bpm.genmobai.di.dagger2

import com.bpm.genmobai.di.dagger2.retrofit.NetworkModule
import com.bpm.genmobai.ui.dashboard.DashBoardActivity
import com.bpm.genmobai.ui.dashboard.DashBoardViewModel
import com.bpm.genmobai.ui.splash.SplashActivity
import com.bpm.genmobai.utility.CustomDialog
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface AppComponent {
    fun inject(splashActivity: SplashActivity)
    fun inject(dashBoardActivity: DashBoardActivity)
    fun inject(dashBoardViewModel: DashBoardViewModel)
    fun inject(customDialog: CustomDialog)
}