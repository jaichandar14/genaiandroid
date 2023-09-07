package com.bpm.genmobai.app.base

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.ViewDataBinding
import com.bpm.genmobai.utility.MyToast
import com.bpm.genmobai.utility.SnackBar

abstract class BaseActivity<T : BaseViewModel> : AppActivity() {

    var TAG: String = this.javaClass.simpleName
    lateinit var viewModel: T


    var bindingRoot: ViewDataBinding? = null
    open fun observer() {
        viewModel.toastMessage.observe(this) { message ->
            message?.let { MyToast.show(this, message, Toast.LENGTH_LONG) }
        }
        // 3372 getting the root view from each activity
        viewModel.bindingRoot.observe(this) {
            bindingRoot = it
        }
        viewModel.getToastMessageG.observe(this) { toastMessageG ->
            Log.d("TAG", "on toast create Base Activity $toastMessageG")
            if (!toastMessageG.msg.isNullOrEmpty()) {
                SnackBar.showSnakbarTypeOne(
                    bindingRoot?.root,
                    toastMessageG.msg,
                    this,
                    toastMessageG.duration
                )
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Set Status bar
        observer()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    }


    override fun onDestroy() {
        super.onDestroy()
    }

}