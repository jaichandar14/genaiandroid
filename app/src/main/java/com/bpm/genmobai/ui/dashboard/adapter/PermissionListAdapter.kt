package com.bpm.genmobai.ui.dashboard.adapter


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bpm.genmobai.R


class PermissionListAdapter(var context: Context) :
    RecyclerView.Adapter<PermissionListAdapter.ChildViewHolder>() {
    private var permStatus = HashMap<String, Int>()
    private var isToggled = false
    var packageId = ApplicationInfo()
    var appNamePosition: Int? = null
    var permissionPosition: Int? = null
    var prompt = String()
    var privacyPolicyUrl: String? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        // Here we inflate the corresponding
        // layout of the child item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.permissiondetails_dashboard_grid, parent, false)

        return ChildViewHolder(view)
    }

    @SuppressLint("SetJavaScriptEnabled", "SetTextI18n")
    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        //    holder.userTerm.visibility=View.GONE
        // Create an instance of the ChildItem
        // class for the given position
        val childItem = permStatus.entries.toList()[position]
        when (childItem.key) {
            "Location" -> {
                holder.childItemIcon.setImageResource(R.drawable.location)
            }
            "Camera" -> {
                holder.childItemIcon.setImageResource(R.drawable.camera)
            }
            "Calendar" -> {
                holder.childItemIcon.setImageResource(R.drawable.calendar)
            }
            "Microphone" -> {
                holder.childItemIcon.setImageResource(R.drawable.mic)
            }
            "Storage" -> {
                holder.childItemIcon.setImageResource(R.drawable.storage)
            }
            "Call Logs" -> {
                holder.childItemIcon.setImageResource(R.drawable.call_log)
            }
            "Contact" -> {
                holder.childItemIcon.setImageResource(R.drawable.contact)
            }

        }
        holder.childItemTitle.text = childItem.key
        if (childItem.value != 2) {
            // Toggle off animation
            val animation = AnimationUtils.loadAnimation(holder.toggle.context, R.anim.toogle_off)
            holder.toggle.startAnimation(animation)
            holder.toggle.setImageResource(R.drawable.ic_baseline_toggle_on_24)
        } else {
            // Toggle on animation
            val animation = AnimationUtils.loadAnimation(holder.toggle.context, R.anim.toogle_on)
            holder.toggle.startAnimation(animation)
            holder.toggle.setImageResource(R.drawable.ic_baseline_toggle_off_24)
        }
        holder.toggle.setOnClickListener {
            callBackInterface?.toggleClick(
                holder.childItemTitle.text as String,
                position,
                packageId,
                appNamePosition
            )

        }

        holder.viewMore.setOnClickListener {
            if (holder.userTerm.visibility == View.GONE) {

                callBackInterface?.onClickViewMore(
                    holder.childItemTitle.text as String,
                    position,
                    packageId,
                    appNamePosition
                )
            } else {
                holder.userTerm.visibility = View.GONE
            }
        }

        holder.userTerm.text = "<style>" +
                ".red-text { color: blue; }" +
                "</style>" +
                "<p>$prompt To view terms of use <span class=\"$privacyPolicyUrl\">Click here</span> string.</p>"
        if (position == permissionPosition) {
            holder.userTerm.visibility = View.VISIBLE

//            val text = findViewById(R.id.text_login) as TextVi
            holder.userTerm.text =
                holder.fromHtml(
                    "Terms of Use:\n<p>$prompt.To view terms of use.click this link<br>" +
                            "<span style=\"color: blue;\">$privacyPolicyUrl</span></p>"
                )
//            appNamePosition=null
//            permissionPosition=null
        }
        holder.userTerm.setOnClickListener {
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Page re-direct")
            builder.setMessage(
                "Page getting redirect to privacy policy of ${
                    context.packageManager.getApplicationLabel(
                        packageId
                    ).toString()
                } website"
            )
            builder.setCancelable(false)
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                val httpIntent = Intent(Intent.ACTION_VIEW)
                httpIntent.data = Uri.parse(privacyPolicyUrl)
                context.startActivity(httpIntent)
            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                dialog.dismiss()
            }


            builder.show()

        }

    }


    override fun getItemCount(): Int {
        return permStatus.size
    }


    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val childItemTitle: TextView = itemView.findViewById(R.id.user_name_et)
        val childItemIcon: ImageView = itemView.findViewById(R.id.user_name)
        val viewMore: ImageView = itemView.findViewById(R.id.view_more)
        val toggle: ImageView = itemView.findViewById(R.id.toggleIcon)
        val userTerm: TextView = itemView.findViewById(R.id.user_case)
        val webView: WebView = itemView.findViewById(R.id.webview)

        @Suppress("DEPRECATION")
        fun fromHtml(html: String?): Spanned {
            if (html == null) {
                // Return an empty spannable if the html is null
                return SpannableString("")
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // FROM_HTML_MODE_LEGACY is the behavior that was used for versions below Android N
                // We are using this flag to give a consistent behavior
                return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            } else {
                return Html.fromHtml(html)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshItems(permStatus: HashMap<String, Int>, packageId: ApplicationInfo, position: Int) {
        this.permStatus.clear()
        this.permStatus.putAll(permStatus)
        appNamePosition = position
        this.packageId = packageId
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshUsePermission(
        permStatus: HashMap<String, Int>,
        packageId: ApplicationInfo,
        appPostionOnClick: Int,
        permissionPostionOnClick: Int?,
        prompt: String,
        privacyPolicyUrl: String?
    ) {
        this.permStatus.clear()
        this.permStatus.putAll(permStatus)
        appNamePosition = appPostionOnClick
        this.packageId = packageId
        this.prompt = prompt
        this.permissionPosition = permissionPostionOnClick
        this.privacyPolicyUrl = privacyPolicyUrl
        notifyDataSetChanged()
    }

    private var callBackInterface: CallBackInterface? = null

    // Initializing CallBack Interface Method
    fun setCallBackInterface(callback: CallBackInterface) {
        callBackInterface = callback
    }

    // CallBackInterface
    interface CallBackInterface {
        fun onClickViewMore(
            text: String,
            position: Int,
            packageId: ApplicationInfo,
            appNamePosition: Int?
        )

        fun toggleClick(
            text: String,
            position: Int,
            packageId: ApplicationInfo,
            appNamePosition: Int?
        )
    }
}
