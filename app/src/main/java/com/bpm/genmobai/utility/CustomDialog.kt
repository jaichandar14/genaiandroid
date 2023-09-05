package com.bpm.genmobai.utility

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bpm.genmobai.R

class CustomDialog(
    context: Context,
    private var dialogAppName: String?,
    private var responseDTO: String?,
    var dialogAppIcon: Drawable?,
    var i: Int,
    var packageId: ApplicationInfo?
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(R.layout.custom_dialog)

        val titleTextView = findViewById<TextView>(R.id.title)
        val content = findViewById<TextView>(R.id.content)
        val appIcon = findViewById<ImageView>(R.id.imageView2)
        val closeBtn = findViewById<ImageView>(R.id.close)
        val okButton = findViewById<Button>(R.id.ok_btn)
        titleTextView.text = dialogAppName
        content.text = responseDTO
        appIcon.setImageDrawable(dialogAppIcon)

        if (i == 2) {
            closeBtn.visibility = View.VISIBLE
            okButton.text = "Settings"
            okButton.setOnClickListener {
                val intent = Intent()
                intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.data = Uri.parse("package:${packageId?.packageName}")
                context.applicationContext.startActivity(intent)
                callBackInterface?.settingsBtn(true, packageId!!)
                dismiss()
            }
            closeBtn.setOnClickListener {
                dismiss() // Close the dialog
            }
        } else {
            okButton.setOnClickListener {
                dismiss() // Close the dialog
            }
        }
    }

    private var callBackInterface: CallBackInterface? = null

    // Initializing CallBack Interface Method
    fun setCallBackInterface(callback: CallBackInterface) {
        callBackInterface = callback
    }

    // CallBackInterface
    interface CallBackInterface {
        fun settingsBtn(appNameIcon: Boolean, packageId: ApplicationInfo)

    }
}