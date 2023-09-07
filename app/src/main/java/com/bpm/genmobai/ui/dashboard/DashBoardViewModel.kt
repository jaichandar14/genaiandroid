package com.bpm.genmobai.ui.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bpm.genmobai.app.base.BaseViewModel
import com.bpm.genmobai.app.base.MyApplication
import com.bpm.genmobai.data.response.PromptResponseDTO
import com.bpm.genmobai.data.response.ResponseDTO
import com.bpm.genmobai.databinding.ActivityDashBoardBinding
import com.bpm.genmobai.di.dagger2.retrofit.RetrofitHelper
import com.bpm.genmobai.utility.ApiClient
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

class DashBoardViewModel : BaseViewModel() {
    val appsList = ArrayList<ApplicationInfo>()
    var permissionPos: Int? = null
    var appNamePosition: Int? = null
    var dialogAppName = String()
    var dialogEnable = false
    var dialogAppIcon: Drawable? = null
    private var listOfAppIcon = java.util.HashMap<String, Drawable>()
    var mAppName = MutableLiveData<ArrayList<String>>()
    var mInstalledAppId = MutableLiveData<ArrayList<ApplicationInfo>>()

    init {
        // Dagger injection
        MyApplication.applicationComponent?.inject(this)
        mAppName.value = ArrayList<String>()
        mInstalledAppId.value = ArrayList<ApplicationInfo>()
    }

    fun permissionDetailsApi(
        text: String,
        packageId: ApplicationInfo,
        position: Int,
        appNamePosition: Int?
    ) {
        Log.d(TAG, "permissionDetailsApi: ${packageId.packageName}")
        permissionPos = position
        this.appNamePosition = appNamePosition
        retrofitHelper = RetrofitHelper(Retrofit.Builder())
        val observable: Observable<PromptResponseDTO> =
            retrofitHelper.getUserRepository().getPermissionDetails(
                packageId.packageName, text
            )
        this.observable.value = observable as Observable<ResponseDTO>
        doNetworkOperation()
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun mFetchInstalledApps(
        status: String,
        dashBoardActivity: DashBoardActivity
    ): MutableList<ApplicationInfo> {
        val mAllApp = mutableListOf<ApplicationInfo>()
        // Use the PackageManager to get a list of all installed apps
        val packageManager = dashBoardActivity.packageManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val mainIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val appList4: List<ResolveInfo> = packageManager.queryIntentActivities(
                mainIntent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
            )
            appList4.forEach {
                appsList.add(it.activityInfo.applicationInfo)
            }

            mInstalledAppId.value?.addAll(appsList)
            mAllApp.addAll(appsList)
            //Log.d(TAG, "mFetchInstalledApps: ${ filterPerBased(appsList, dashBoardActivity, status, packageManager)}")
            settingDataINUI(appsList, status, dashBoardActivity)
        } else {
            val installedApps =
                packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            var userInstalledApp = ArrayList<ApplicationInfo>()
            for (notSystemApps in installedApps) {
                if (notSystemApps.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                    appsList.add(notSystemApps)
                }
            }
            mInstalledAppId.value?.addAll(appsList)
            mAllApp.addAll(appsList)
            settingDataINUI(appsList, status, dashBoardActivity)

        }
        return mAllApp
    }

    fun filterPerBased(
        appsList: ArrayList<ApplicationInfo>,
        dashBoardActivity: DashBoardActivity,
        status: String,
        packageManager: PackageManager,
        binding: ActivityDashBoardBinding
    ): ArrayList<ApplicationInfo> {
        Log.d(TAG, "filterPerBased: $status")
        var appNameList = ArrayList<ApplicationInfo>()
        for (appInfo in appsList) {
            try {
                val packageName = appInfo.packageName
                val appPermissions = getPackageInfo(
                    appInfo.packageName, PackageManager.GET_PERMISSIONS, packageManager
                )
                // val appInfo1 = appInfo.find { it.packageName == packageName }
                when (status) {
                    "L" ->
                        if (appPermissions?.contains(Manifest.permission.ACCESS_FINE_LOCATION) == true) {
                            if (packageManager.checkPermission(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    packageName
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                appNameList.add(appInfo)
                                binding.userDetails.text =
                                    "Location Permission ${appNameList.size}"


                            }
                        }

                    "C" -> {
                        if (appPermissions?.contains(Manifest.permission.CAMERA) == true) {
                            if (packageManager.checkPermission(
                                    Manifest.permission.CAMERA,
                                    packageName
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {

                                appNameList.add(appInfo)
                                binding.userDetails.text =
                                    "Camera Permission ${appNameList.size}"

                            }
                        }

                    }

                    "CO" -> {
//                        binding.listOfPermission.visibility=View.GONE
//                        binding.userDataList.visibility = View.VISIBLE
                        if (appPermissions?.contains(Manifest.permission.WRITE_CONTACTS) == true || appPermissions?.contains(
                                Manifest.permission.READ_CONTACTS
                            ) == true
                        ) {
                            if (packageManager.checkPermission(
                                    Manifest.permission.WRITE_CONTACTS,
                                    packageName
                                ) == PackageManager.PERMISSION_GRANTED || packageManager.checkPermission(
                                    Manifest.permission.READ_CONTACTS,
                                    packageName
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {

                                appNameList.add(appInfo)
                                binding.userDetails.text =
                                    "Contact Permission ${appNameList.size}"

                                Log.d(TAG, "filterPerBased:error ")
                            }
                        } else {
                            Log.d(TAG, "filterPerBased:$appNameList ")
                        }
                    }
                    "STORAGE" -> {
//                        binding.userDataList.visibility = View.VISIBLE
                        if (appPermissions?.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE) == true || appPermissions?.contains(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ) == true
                        ) {
                            if (packageManager.checkPermission(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    packageName
                                ) == PackageManager.PERMISSION_GRANTED || packageManager.checkPermission(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    packageName
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {


                                appNameList.add(appInfo)
                                binding.userDetails.text =
                                    "Storage Permission ${appNameList.size}"

                            }
                        }

                    }
                    "MP" -> {
//                        binding.userDataList.visibility = View.VISIBLE
                        if (appPermissions?.contains(Manifest.permission.RECORD_AUDIO) == true) {
                            if (packageManager.checkPermission(
                                    Manifest.permission.RECORD_AUDIO,
                                    packageName
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {


                                appNameList.add(appInfo)
                                binding.userDetails.text =
                                    "Microphone Permission ${appNameList.size}"

                            }
                        }

                    }
                    "MAIN" -> {
//                        binding.userDetails.text = "Intalled Applications"
                        appNameList.add(appInfo)
                    }
                }

            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        return appNameList
    }

    fun settingDataINUI(
        appsList: ArrayList<ApplicationInfo>,
        status: String,
        dashBoardActivity: DashBoardActivity
    ) {
        val appPackageName: MutableList<String> = mutableListOf()
        val appsWithPermission = mutableListOf<String>()
        for (appInfo in appsList) {
            try {
                Log.d(TAG, "settingDataINUI: $appInfo")
                appPackageName.add(appInfo.packageName)
                val packageName = appInfo.packageName
                appsWithPermission.add(packageName)

            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        var appIcon: Drawable? = null
        val appNameIcon = HashMap<String, Drawable>()
        appsWithPermission.forEach { packageName ->
            val appLabel = dashBoardActivity.packageManager.getPackageInfoCompat(
                packageName, PackageManager.GET_META_DATA
            ).applicationInfo
            val appName = dashBoardActivity.packageManager.getApplicationLabel(appLabel)
            appIcon = dashBoardActivity.packageManager.getApplicationIcon(packageName)
            mAppName.value?.add(appName.toString())
            appNameIcon.put(appName.toString(), appIcon!!)
            // Get the application's icon (logo) as a Drawable

            Log.d(TAG, "permissionAccessRequest: $appNameIcon")
        }

        callBackInterface?.mInstalledApps(appNameIcon)

        // mAdapterServiceDetails.refreshItems(appNames1, appIcon, appNameIcon)

    }

    override fun onSuccess(responseDTO: ResponseDTO) {
        super.onSuccess(responseDTO)
        Log.d(TAG, "onSuccess: $responseDTO ")
        var rep = responseDTO as PromptResponseDTO

        if (!rep.isPrivacyPolicyAvailable) {
            callBackInterface?.settingUseOfPermission(
                "Unable to display the privacy policy",
                appNamePosition,
                permissionPos,
                rep.privacyPolicyUrl
            )

        } else if (rep.isPrivacyPolicyAvailable && dialogEnable) {
            callBackInterface?.detailsDialog(dialogAppName, rep.message, dialogAppIcon, appsList[0])
            dialogEnable = false
        } else {
            callBackInterface?.settingUseOfPermission(
                rep.message,
                appNamePosition,
                permissionPos,
                rep.privacyPolicyUrl
            )

        }


    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)
        Log.d(TAG, "onSuccessError: ${throwable.message}")
    }

    private fun PackageManager.getPackageInfoCompat(
        packageName: String, flags: Int = 0
    ): PackageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") getPackageInfo(packageName, flags)

    }

    fun fetchPackedIDFromName(text: String, dashBoardActivity: DashBoardActivity): ApplicationInfo {
        // Use the PackageManager to get a list of all installed apps
        val packageManager = dashBoardActivity.packageManager
        var packageName = ApplicationInfo()
        for (appInfo in appsList) {
            val label = packageManager.getApplicationLabel(appInfo).toString()
            if (label == text) {
                packageName = appInfo
            }
        }

        return packageName
    }


    fun searchPermission(generatedResponse: String, appCon: DashBoardActivity) {
        var appName = String()
        var repApp: String? = null
        var packageid = ApplicationInfo()
        for (it in appsList) {
            val appLabel = appCon.packageManager.getPackageInfoCompat(
                it.packageName, PackageManager.GET_META_DATA
            ).applicationInfo

            appName = appCon.packageManager.getApplicationLabel(appLabel).toString()
            if (generatedResponse.contains(appName)) {
                repApp = appName
                packageid = it
            }
        }

        if (repApp != null) {
            if (generatedResponse.lowercase().contains(repApp.lowercase())) {
                Log.d(TAG, "searchPermission,: $generatedResponse")
                Log.d(TAG, "searchPermission2: $repApp")

                fetchBasedApp(repApp.lowercase(), packageid, generatedResponse.lowercase(), appCon)
            }
        } else {
            filterPermission(generatedResponse.lowercase(), appsList)
        }
    }

    fun fetchBasedApp(
        appName: String,
        packageid: ApplicationInfo,
        prompt: String,
        appCon: DashBoardActivity
    ) {
        var promptPermission = String()
        var permissionList = ArrayList<String>()
        permissionList.add("location")
        permissionList.add("camera")
        permissionList.add("microphone")
        permissionList.add("storage")
        permissionList.add("audio")
        permissionList.add("contact")
        permissionList.add("call logs")
        for (it in permissionList) {
            if (prompt.contains(it)) {
                promptPermission = it
            }
        }
        dialogAppIcon = appCon.packageManager.getApplicationIcon(packageid.packageName)
        dialogEnable = true
        dialogAppName = appName
        retrofitHelper = RetrofitHelper(Retrofit.Builder())
        val observable: Observable<PromptResponseDTO> =
            retrofitHelper.getUserRepository().getPermissionDetails(
                packageid.packageName, promptPermission
            )
        this.observable.value = observable as Observable<ResponseDTO>
        doNetworkOperation()
    }

    fun filterPermission(
        generatedResponse: String,
        appsList: ArrayList<ApplicationInfo>
    ) {
        if (generatedResponse.contains("locations") ||
            generatedResponse.contains("location") ||
            generatedResponse.contains(
                "gps"
            ) || generatedResponse.contains("map") || generatedResponse.contains("navigation")
        ) {
            callBackInterface?.permissionType("L", appsList)
        } else if (generatedResponse.contains("camera") || generatedResponse.contains("cameras")
        ) {
            callBackInterface?.permissionType("C", appsList)
        } else if (generatedResponse.contains("contact") || generatedResponse.contains("contacts")
        ) {
            Log.d(TAG, "filterPermission: CO")
            callBackInterface?.permissionType("CO", appsList)
        } else if (generatedResponse.contains("storage") || generatedResponse.contains(
                "file"
            )
        ) {
            callBackInterface?.permissionType("STORAGE", appsList)
        } else if (generatedResponse.contains(
                "microphone"
            ) || generatedResponse.contains("record")
            || generatedResponse.contains("audio")
        ) {
            callBackInterface?.permissionType("MP", appsList)
        } else {
            showSnackMessage("Please do the valid search")
            // callBackInterface?.clickedAppResponse("Please do the valid search")
        }
    }


    // Make the API request
    fun makeApiRequest(
        text: String,
        type: Int = 1,
        position: Int = 0,
        appCon: DashBoardActivity
    ) {
        // Construct the request
        val model = "gpt-3.5-turbo" // Replace with the model you want to use
        val message = DashBoardActivity.Message("user", text)
        val request = DashBoardActivity.ChatGptRequest(model, listOf(message))
        ApiClient.chatGptApi.getChatGptResponse(request).enqueue(object :
            Callback<DashBoardActivity.ChatGptResponse> {
            override fun onResponse(
                call: Call<DashBoardActivity.ChatGptResponse>,
                response: Response<DashBoardActivity.ChatGptResponse>
            ) {
                if (response.isSuccessful) {
                    val generatedResponse = response.body()?.choices?.get(0)?.message?.content
                    println("jai app success $generatedResponse")
                    showLoading.value = false
                    if (generatedResponse != null) {
                        //  fetchResposne(generatedResponse, type, position)
                        searchPermission(generatedResponse, appCon)
                    }

                } else {
                    // Handle API error
                    showLoading.value = false
                    showSnackMessage("Time out try again")
                    // callBackInterface?.clickedAppResponse("Time out try again")

                }
            }

            override fun onFailure(call: Call<DashBoardActivity.ChatGptResponse>, t: Throwable) {
                // Handle network failures
                showLoading.value = false
                showSnackMessage("Time out try again")
                // callBackInterface?.clickedAppResponse("Time out try again")
            }
        })
    }

    fun filter(
        text: String,
        mInstalledAppId: ArrayList<ApplicationInfo>,
        appCon: DashBoardActivity
    ) {
        val packageManager: PackageManager = appCon.packageManager
        val filteredlist: java.util.ArrayList<String> = java.util.ArrayList()
        val appDetails: kotlin.collections.Map<String, Drawable>? = null
        var appName = kotlin.collections.ArrayList<String>()
        for (appIno in appsList) {
            appName.add(packageManager.getApplicationLabel(appIno).toString())
        }

        Log.d(TAG, "filter1app Name: $appName ")
        // running a for loop to compare elements.
        for (item in appName) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)

            }
        }
        Log.d(TAG, "filter2: $filteredlist")
        var appIcon = ArrayList<Drawable>()
        var appNamess = ArrayList<String>()
        var listOfAppIcons = java.util.HashMap<String, Drawable>()
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            //  Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            for (appInfo in appsList) {
                val label = packageManager.getApplicationLabel(appInfo).toString()
                filteredlist.forEach {

                    if (label == it) {
                        appInfo.packageName
                        listOfAppIcons.put(
                            packageManager.getApplicationLabel(appInfo).toString(),
                            packageManager.getApplicationIcon(appInfo.packageName)
                        )
                    }


                }
            }

            callBackInterface?.filterSearch(listOfAppIcons)
        }
    }


    private var callBackInterface: CallBackInterface? = null

    // Initializing CallBack Interface Method
    fun setCallBackInterface(callback: CallBackInterface) {
        callBackInterface = callback
    }

    // CallBackInterface
    interface CallBackInterface {
        fun mInstalledApps(appNameIcon: HashMap<String, Drawable>)
        fun permissionType(s: String, appsList: ArrayList<ApplicationInfo>)
        fun settingUseOfPermission(
            responseDTO: String,
            appNamePosition: Int?,
            permissionPos: Int?,
            privacyPolicyUrl: String
        )

        fun detailsDialog(
            dialogAppName: String,
            responseDTO: String,
            dialogAppIcon: Drawable?,
            applicationInfo: ApplicationInfo
        )

        fun filterSearch(
            listOfAppIcon: HashMap<String, Drawable>
        )
    }

    private fun getPackageInfo(
        packageName: String,
        getPermissions: Int,
        packageManager: PackageManager
    ): Array<String>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                packageName, PackageManager.PackageInfoFlags.of(getPermissions.toLong())
            ).requestedPermissions
        } else {
            @Suppress("DEPRECATION") packageManager.getPackageInfo(
                packageName,
                getPermissions
            ).requestedPermissions
        }
    }

}



