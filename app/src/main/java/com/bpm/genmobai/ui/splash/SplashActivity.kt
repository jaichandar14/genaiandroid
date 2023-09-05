package com.bpm.genmobai.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bpm.genmobai.R
import com.bpm.genmobai.app.base.AppActivity
import com.bpm.genmobai.app.base.MyApplication
import com.bpm.genmobai.databinding.ActivityMainBinding
import com.bpm.genmobai.ui.dashboard.DashBoardActivity

class SplashActivity : AppActivity() {
    val TAG = this.javaClass.simpleName
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        // Dagger injection
        MyApplication.applicationComponent?.inject(this)
        // Listener for get Started Button
        binding.progress.visibility = View.INVISIBLE
        getStartedBtnListener()

    }

    private fun getStartedBtnListener() {
        binding.splashBtn.setOnClickListener {
            binding.progress.visibility = View.VISIBLE
            val intent = Intent(this, DashBoardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}