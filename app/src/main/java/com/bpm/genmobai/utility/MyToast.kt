package com.bpm.genmobai.utility

import android.content.Context
import android.text.Spanned
import android.widget.Toast
import androidx.core.text.HtmlCompat

object MyToast {
    var toastMessage: Toast? = null
    fun show(context: Context, message: String, duration: Int) {
        if (toastMessage != null) {
            toastMessage?.cancel()
        }

        toastMessage = Toast.makeText(
            context,
            changeBackgroundUsingHTML(getNewLineAfter15Char(message)),
            duration
        )
        toastMessage?.show()

    }

    private fun getNewLineAfter15Char(message: String): String {
        if (message.length > 20) {
            var finalString = StringBuffer()
            val stringArray: List<String> = message.split("\\s+")
            var tmpString = ""
            for (singleWord in stringArray) {
                tmpString = if ("$tmpString$singleWord ".length > 20) {
                    finalString.append(
                        """
                    $tmpString
                    
                    """.trimIndent()
                    )
                    "$singleWord "
                } else {
                    "$tmpString$singleWord "
                }
            }

            if (tmpString.isNotEmpty()) {
                finalString.append(tmpString)
            }
            return finalString.toString()
        } else {
            return message
        }
    }

    private fun changeBackgroundUsingHTML(message: String): Spanned {
        return HtmlCompat.fromHtml(
            "<font background-color:'black'; color:'white'> $message</font>",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }
}