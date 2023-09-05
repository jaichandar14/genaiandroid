package com.bpm.genmobai.utility

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.bpm.genmobai.R
import com.google.android.material.snackbar.Snackbar

object SnackBar {

    /************************************ ShowSnackbar with message, KeepItDisplayedOnScreen for few seconds */
    fun showSnakbarTypeOne(rootView: View?, mMessage: String?, activity: Activity, duration: Int) {
        if (rootView != null) {
            val snackbar = Snackbar.make(rootView, "", duration).setAction("Action", null)
            // inflate the custom_snackbar_view created previously
            val customSnackView: View = activity.layoutInflater.inflate(
                R.layout.layout_custome_toast, null
            )
            setSnackBar(customSnackView, mMessage, snackbar)
        }
    }

    private fun setSnackBar(customSnackView: View, mMessage: String?, snackbar: Snackbar) {
        val textView: TextView = customSnackView.findViewById(R.id.custom_toast_message)

        textView.text = mMessage
        textView.setTextColor(Color.BLACK)
        // set the background of the default snackbar as transparent
        snackbar.view.setBackgroundColor(Color.TRANSPARENT)
        // now change the layout of the snackbar
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        snackbarLayout.setPadding(10, 0, 0, 0)
        // add the custom snack bar layout to snackbar layout
        snackbarLayout.addView(customSnackView, 0)
        // Position snackbar at top
        snackbar.show()
    }

    /************************************ ShowSnackbar with message, KeepItDisplayedOnScreen */
    fun showSnakbarTypeTwo(rootView: View?, mMessage: String?) {
        if (rootView != null) {
            Snackbar.make(rootView, mMessage!!, Snackbar.LENGTH_INDEFINITE)
                .setAction("Action", null).show()
        }
    }
}