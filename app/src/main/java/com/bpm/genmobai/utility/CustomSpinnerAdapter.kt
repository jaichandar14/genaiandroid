package com.bpm.genmobai.utility

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bpm.genmobai.R
import com.bpm.genmobai.ui.dashboard.DashBoardActivity

class CustomSpinnerAdapter(
    private val context: Context,
    private val items: List<DashBoardActivity.CustomSpinnerItem>
) : BaseAdapter() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.custom_spinner_item, parent, false)

        val iconImageView = view.findViewById<ImageView>(R.id.icon)
        val textTextView = view.findViewById<TextView>(R.id.text)

        val item = items[position]

        // Set the data for the custom layout
        iconImageView.setImageResource(item.icon)
        textTextView.text = item.name

        return view
    }
}