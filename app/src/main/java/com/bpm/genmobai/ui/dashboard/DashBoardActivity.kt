package com.bpm.genmobai.ui.dashboard

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bpm.genmobai.R
import com.bpm.genmobai.app.base.BaseActivity
import com.bpm.genmobai.app.base.MyApplication
import com.bpm.genmobai.databinding.ActivityDashBoardBinding
import com.bpm.genmobai.ui.dashboard.adapter.AppListAdapter
import com.bpm.genmobai.utility.CustomDialog
import com.bpm.genmobai.utility.CustomSpinnerAdapter
import com.bpm.genmobai.utility.PermissionValidation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.http.*


class DashBoardActivity : BaseActivity<DashBoardViewModel>(), DashBoardViewModel.CallBackInterface,
    AppListAdapter.CallBackInterface, CustomDialog.CallBackInterface {
    lateinit var binding: ActivityDashBoardBinding
    private lateinit var mAppListRecyclerView: RecyclerView
    private lateinit var mAdapterAppList: AppListAdapter
    private var listOfAppIcon = java.util.HashMap<String, Drawable>()
    private var appNameList = kotlin.collections.ArrayList<String>()
    var mInstalledAppId = kotlin.collections.ArrayList<ApplicationInfo>()


    lateinit var customDialog: CustomDialog
//    var openAiApi: OpenAiApi = OpenAiApi("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")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initializing data binding ,viewmodel and dagger
        mInitializer()
        // Initializing RecyclerView
        mAppListRecyclerView()
        customDialog = CustomDialog(this, null, null, null, 0, null)
        MyApplication.applicationComponent?.inject(this)
        viewModel.setCallBackInterface(this)
        mAdapterAppList.setCallBackInterface(this)
        customDialog.setCallBackInterface(this)


        onClickSend()
        searchApp(mInstalledAppId)
        viewModel.mAppName.observe(this, androidx.lifecycle.Observer {
            appNameList.addAll(it)

        })
        viewModel.mInstalledAppId.observe(this, androidx.lifecycle.Observer {
            mInstalledAppId.addAll(it)

        })
        binding.reload.setOnClickListener {
            viewModel.showLoading.value = true
            startActivity(Intent(this, DashBoardActivity::class.java))
            GlobalScope.launch(Dispatchers.IO) {
                delay(3500.toLong())
                withContext(Dispatchers.Main) {
                    viewModel.showLoading.value = false
                }
                // Place your task here
            }
        }
        // Create a list of CustomSpinnerItem objects
        val items = listOf(
            CustomSpinnerItem(R.drawable.location, "Location"),
            CustomSpinnerItem(R.drawable.camera, "Camera"),
            CustomSpinnerItem(R.drawable.contact, "Contact")
        )

        val adapter = CustomSpinnerAdapter(this, items)
        binding.spinner.adapter = adapter
    }

    data class CustomSpinnerItem(var icon: Int, var name: String)

    fun searchApp(mInstalledAppId: ArrayList<ApplicationInfo>) {
        // Set up the search view
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Not used in this example, you can perform search on pressing enter/submit here.
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //appNames1.add(newText.toString())
                viewModel.filter(newText.toString(), mInstalledAppId, this@DashBoardActivity)
                return true
            }
        })
    }

    private fun onClickSend() {
        binding.send.setOnClickListener {
            var prompt = binding.etPrompt.text.toString()
            viewModel.showLoading.value = true
            viewModel.makeApiRequest(
                "tell me the app name and permission name  if app name is not mention tell me the permission name from this sentence .The sentence is \"\"\" $prompt\"\"\"",
                1,
                0, this@DashBoardActivity
            )
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        viewModel.mFetchInstalledApps("Main", this)
    }

    private fun mInitializer() {
        binding =
            DataBindingUtil.setContentView(this@DashBoardActivity, R.layout.activity_dash_board)
        viewModel = ViewModelProvider(this)[DashBoardViewModel::class.java]
        binding.dashBoardViewModel = viewModel
        binding.lifecycleOwner = this@DashBoardActivity
        // Dagger injection
        MyApplication.applicationComponent?.inject(this)
        viewModel.bindingRoot.value = binding
    }

    // Initializing app list recycler view
    private fun mAppListRecyclerView() {
        mAppListRecyclerView = binding.appList
        mAdapterAppList = AppListAdapter(this)
        mAppListRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAppListRecyclerView.adapter = mAdapterAppList
    }


    override fun mInstalledApps(appNameIcon: HashMap<String, Drawable>) {
        mAdapterAppList.refreshItems(appNameIcon, 1)
    }

    override fun permissionType(s: String, appsList: ArrayList<ApplicationInfo>) {
        //Log.d(TAG, "permissionType: ${viewModel.filterPerBased(appsList, this,s,packageManager)}")

        viewModel.settingDataINUI(
            viewModel.filterPerBased(
                appsList,
                this,
                s,
                packageManager,
                binding
            ), "Main", this
        )
    }

    override fun settingUseOfPermission(
        responseDTO: String,
        appNamePosition: Int?,
        permissionPos: Int?,
        privacyPolicyUrl: String
    ) {
        mAdapterAppList.refreshPermissionDetailsUi(
            responseDTO,
            appNamePosition,
            permissionPos,
            true,
            privacyPolicyUrl
        )
    }

    override fun detailsDialog(
        dialogAppName: String,
        responseDTO: String,
        dialogAppIcon: Drawable?,
        applicationInfo: ApplicationInfo
    ) {
        customDialog = CustomDialog(
            this,
            dialogAppName,
            responseDTO,
            dialogAppIcon,
            1,
            applicationInfo
        )
        customDialog.show()
    }

    override fun filterSearch(
        listOfAppIcon: HashMap<String, Drawable>
    ) {
        mAdapterAppList.refreshItems(listOfAppIcon, 1)
    }

    // ON click app name call back
    override fun onClickAppName(text: String, position: Int) {
        mAppListRecyclerView.layoutManager?.scrollToPosition(position)
        var packageId = viewModel.fetchPackedIDFromName(text, this)
        val appPermissions = mutableListOf<String>()
        var permList = PermissionValidation.permissionValidation(this, packageId)
        mAdapterAppList.refreshItemsPermission(permList, 2, position, packageId)
    }

    override fun onClickViewMore(
        text: String,
        position: Int,
        packageId: ApplicationInfo,
        appNamePosition: Int?
    ) {
//        Log.d(TAG, "onClickViewMore: 123")
//
//        var retrofitHelper = RetrofitHelper(Retrofit.Builder())
        viewModel.permissionDetailsApi(text, packageId, position, appNamePosition)
    }

    override fun onClickToggle(
        text: String,
        position: Int,
        packageId: ApplicationInfo,
        appNamePosition: Int?
    ) {
        var dialogAppName = packageManager.getApplicationLabel(packageId).toString()
        var dialogAppIcon = packageManager.getApplicationIcon(packageId.packageName)
        var details =
            "Redirect to App Settings\nTo continue, press the \"settings\" to change the app permission ."
        customDialog = CustomDialog(this, dialogAppName, details, dialogAppIcon, 2, packageId)
        customDialog.show()
    }

    override fun onMinimize(b: Boolean, text: HashMap<String, Drawable>, i: Int) {
        Log.d(TAG, "onMinimize: $text")

    }


    interface ChatGptApi {
        @Headers("Authorization: Bearer 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")
        @POST("/v1/chat/completions")
        fun getChatGptResponse(@Body request: ChatGptRequest): Call<ChatGptResponse>

        @Headers("Content-Type: application/json")
        @GET("api/mobile/get-privacy-policy-content/{dynamicPath}")
        fun getPrivacyPolicyContent(@Path("dynamicPath") dynamicPath: String?): Call<String>

    }


    data class Choices(
        val text: String,
        // You may have other fields here based on the API response
    )

    data class ChatGptRequest(val model: String, val messages: List<Message>)
    data class Message(val role: String, val content: String)
    data class ChatGptResponse(val id: String, val choices: List<Choice>)
    data class Choice(val message: Message)

    override fun settingsBtn(appNameIcon: Boolean, packageId: ApplicationInfo) {
        val intent = Intent()
        intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.parse("package:${packageId.packageName}")
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

}

