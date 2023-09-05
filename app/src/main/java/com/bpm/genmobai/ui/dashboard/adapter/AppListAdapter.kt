package com.bpm.genmobai.ui.dashboard.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bpm.genmobai.R

class AppListAdapter(var context: Context) :
    RecyclerView.Adapter<AppListAdapter.ParentViewHolder>(),
    PermissionListAdapter.CallBackInterface {
    private val viewPool = RecyclerView.RecycledViewPool()
    private var appNameIcon = HashMap<String, Drawable>()
    private var permStatus = HashMap<String, Int>()
    private var onRefresh = 0
    var prompt = String()
    var packageId = ApplicationInfo()
    var pos: Int? = null
    var appPostionOnClick: Int? = null
    var permissionPostionOnClick: Int? = null
    lateinit var mPermissionRecyclerView: RecyclerView
    lateinit var mAdapterPermissionList: PermissionListAdapter
    var filterNameIcon = HashMap<String, Drawable>()
    var filter = false
    var namePos: Int? = null
    var onMini = false
    var privacyPolicyUrl: String? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        // Here we inflate the corresponding
        // layout of the parent item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.applist_dashboard_grid, parent, false)
        return ParentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        // Initialize child recycler view which show the permission list
        mInitializeChildRecycler(holder)
        Log.d("TAG", "onBindViewHolder: 1")
        holder.childRecyclerView.visibility = View.GONE
        if (onMini) {
            pos = null
            Log.d("TAG", "onBindViewHolder: 2")
            holder.onBind(appNameIcon.entries.toList()[position], position, pos, appNameIcon)
            onMini = false
        } else {
            Log.d("TAG", "onBindViewHolder: 21")
            holder.onBind(appNameIcon.entries.toList()[position], position, pos, appNameIcon)

        }


    }

    private fun mInitializeChildRecycler(holder: ParentViewHolder) {
        mPermissionRecyclerView = holder.childRecyclerView
        mAdapterPermissionList = PermissionListAdapter(context)

        mPermissionRecyclerView.layoutManager = LinearLayoutManager(
            holder.childRecyclerView.context,
            LinearLayoutManager.VERTICAL,
            false
        )
        mPermissionRecyclerView.adapter = mAdapterPermissionList
        mAdapterPermissionList.setCallBackInterface(this)
    }

    override fun getItemCount(): Int {
        return appNameIcon.entries.toList().size
    }

    // This class is to initialize
    // the Views present in
    // the parent RecyclerView
    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parentItemIcon: ImageView = itemView.findViewById(R.id.app_icon)
        val add: ImageView = itemView.findViewById(R.id.add)
        val parentItemTitle: TextView = itemView.findViewById(R.id.app_name)
        val childRecyclerView: RecyclerView = itemView.findViewById(R.id.innerRecyclerView)
        val nameLayout: ConstraintLayout = itemView.findViewById(R.id.app_name_layout)

        fun onBind(
            appNameIcon: MutableMap.MutableEntry<String, Drawable>,
            position: Int,
            pos: Int?,
            appNameIcon1: HashMap<String, Drawable>
        ) {

            if (pos == position) {
                Log.d("TAG", "onBindViewHolder: 15")
                Log.d("TAG", "onBind: ${appNameIcon}")
                Log.d("TAG", "onBindViewHolder: 12")
                childRecyclerView.visibility = View.VISIBLE
                if (childRecyclerView.visibility == View.VISIBLE) {
                    Log.d("TAG", "onBindViewHolder: 13")

                    add.setImageResource(R.drawable.ic_baseline_minimize_24)
                    mAdapterPermissionList.refreshItems(permStatus, packageId, position)
                } else {
                    Log.d("TAG", "onBindViewHolder: 14")
//                    pos = null

                } //
            }
            // childRecyclerView.visibility=View.GONE
            add.setImageResource(R.drawable.ic_baseline_add_24)

            parentItemTitle.text = appNameIcon.key
            parentItemIcon.setImageDrawable(appNameIcon.value)

            nameLayout.setOnClickListener {
                if (childRecyclerView.visibility == View.GONE) {
                    Log.d("TAG", "onBindViewHolder: 16")
                    callBackInterface?.onClickAppName(parentItemTitle.text as String, position)
                } else {
                    Log.d("TAG", "onBindViewHolder: 17")
                    add.setImageResource(R.drawable.ic_baseline_add_24)
                    childRecyclerView.visibility = View.GONE
//                    onMini = true
                }
            }
            if (appPostionOnClick == position) {
                childRecyclerView.visibility = View.VISIBLE
                mAdapterPermissionList.refreshUsePermission(
                    permStatus, packageId,
                    appPostionOnClick!!, permissionPostionOnClick, prompt, privacyPolicyUrl
                )
                Log.d("TAG", "onBindViewHolder: 15")
                appPostionOnClick = null
                permissionPostionOnClick = null

            }
        }

        fun onVind(filterList: String, filterIcon: Drawable) {
            parentItemTitle.text = filterList
            parentItemIcon.setImageDrawable(filterIcon)
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshItems(
        nameIcon: HashMap<String, Drawable>,
        onRefresh: Int
    ) {
        Log.d("TAG", "refreshItems: $nameIcon")
        appNameIcon.clear()
        appNameIcon.putAll(nameIcon)
        this.onRefresh = onRefresh
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshItems1(
        nameIcon: HashMap<String, Drawable>,
        onRefresh: Int,
        b: Boolean
    ) {
        appNameIcon.clear()
        appNameIcon.putAll(nameIcon)
        this.namePos = onRefresh
        onMini = b
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshItemsPermission(
        nameIcon: HashMap<String, Int>,
        onRefresh: Int,
        position: Int,
        packageId: ApplicationInfo
    ) {
        permStatus.clear()
        permStatus.putAll(nameIcon)
        this.packageId = packageId
        this.onRefresh = onRefresh
        pos = 0
        pos = position
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshPermissionDetailsUi(
        responseDTO: String,
        appNamePosition: Int?,
        permissionPos: Int?,
        b: Boolean,
        privacyPolicyUrl: String
    ) {
        onMini = b
        prompt = responseDTO
        this.appPostionOnClick = appNamePosition
        this.permissionPostionOnClick = permissionPos
        this.privacyPolicyUrl = privacyPolicyUrl
        Log.d(
            "TAG",
            "refreshPermissionDetailsUi: promtpt:$responseDTO apppos:$appNamePosition permpos:$permissionPos"
        )
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterAppRefresh(
        filteredlist: java.util.HashMap<String, Drawable>,
        isFilter: Boolean,

        ) {

        this.filter = isFilter
        this.filterNameIcon.clear()
        this.filterNameIcon.putAll(filteredlist)
        notifyDataSetChanged()
    }

    override fun onClickViewMore(
        text: String,
        position: Int,
        packageId: ApplicationInfo,
        appNamePosition: Int?
    ) {
        Log.d("TAG", "onClickViewMore: 1235")
        callBackInterface?.onClickViewMore(text, position, packageId, appNamePosition)
    }

    override fun toggleClick(
        text: String,
        position: Int,
        packageId: ApplicationInfo,
        appNamePosition: Int?
    ) {
        callBackInterface?.onClickToggle(text, position, packageId, appNamePosition)
    }

    private var callBackInterface: CallBackInterface? = null

    // Initializing CallBack Interface Method
    fun setCallBackInterface(callback: CallBackInterface) {
        callBackInterface = callback
    }

    // CallBackInterface
    interface CallBackInterface {
        fun onClickAppName(text: String, position: Int)
        fun onClickViewMore(
            text: String,
            position: Int,
            packageId: ApplicationInfo,
            appNamePosition: Int?
        )

        fun onClickToggle(
            text: String,
            position: Int,
            packageId: ApplicationInfo,
            appNamePosition: Int?
        )

        fun onMinimize(b: Boolean, text: HashMap<String, Drawable>, i: Int)
    }

}
