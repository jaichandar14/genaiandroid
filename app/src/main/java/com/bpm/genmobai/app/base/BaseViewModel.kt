package com.bpm.genmobai.app.base

import android.util.Log
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bpm.genmobai.R
import com.bpm.genmobai.app.constant.AppConstants
import com.bpm.genmobai.app.constant.AppConstants.NOT_FOUND
import com.bpm.genmobai.app.constant.AppConstants.SERVICE_UNAVAILABLE
import com.bpm.genmobai.app.constant.AppConstants.SERVICE_UNAVAILABLE_2
import com.bpm.genmobai.app.constant.AppConstants.TIMEOUT_EXCEPTION
import com.bpm.genmobai.app.constant.AppConstants.TOO_MANY_REQUEST_CODE
import com.bpm.genmobai.app.constant.AppConstants.UNAUTHORISED
import com.bpm.genmobai.data.response.ResponseDTO
import com.bpm.genmobai.di.dagger2.retrofit.RetrofitHelper
import com.bpm.genmobai.utility.CustomApiError
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

abstract class BaseViewModel : ViewModel() {
    var showLoading = MutableLiveData<Boolean>()
    var retryErrorMessage = MutableLiveData<Int?>()
    var logout = MutableLiveData<Boolean>()
    val TAG: String = this.javaClass.simpleName
    val disposables = CompositeDisposable()
    var observable = MutableLiveData<Observable<ResponseDTO>>()

    //    var observable1 = MutableLiveData<Observable<String>>()
    //var observable1 = MutableLiveData<Observable<List<ResponseDTO>>>()
    var responsesAI = String()
    var bindingRoot = MutableLiveData<ViewDataBinding>()

    @Inject
    lateinit var retrofitHelper: RetrofitHelper

    @Inject
    lateinit var customErrorAPI: CustomApiError

    var toastMessage = MutableLiveData<String?>()


    init {
        hideProgress()
    }

    private fun showProgress() {
        showLoading.value = true
    }

    private fun hideProgress() {
        showLoading.value = false
    }


    open fun onError(throwable: Throwable) {
        try {
            when {
                throwable is IOException -> {
                    when (throwable) {
                        is UnknownHostException -> {
                            //   retryErrorMessage.value = R.string.internet_error
                        }
                        else -> {
                            retryErrorMessage.value = (R.string.time_out_error)
                        }
                    }
                }
                throwable is SocketTimeoutException -> {
                    retryErrorMessage.value = (R.string.time_out_error)
                }
                (throwable as HttpException).code() == NOT_FOUND -> {
                    retryErrorMessage.value = (R.string.server_not_found)
                }
                (throwable).code() == UNAUTHORISED -> {
                    retryErrorMessage.value = R.string.invalid_credential
                }
                (throwable).code() == AppConstants.FORBIDDEN -> {
                    retryErrorMessage.value = R.string.unAuthorised_request
                }
                (throwable).code() == TOO_MANY_REQUEST_CODE -> {
                    retryErrorMessage.value = R.string.too_many_request
                }
                throwable.code() == SERVICE_UNAVAILABLE || (throwable).code() == SERVICE_UNAVAILABLE_2 -> {
                    retryErrorMessage.value = (R.string.service_not_available)
                }
                throwable.code() == TIMEOUT_EXCEPTION -> {
                    retryErrorMessage.value = (R.string.invalid_credential)
                    //getUserToken(preferenceHelper[SharedPrefConstant.USER_ID, ""])
                    logout.value = true
                }

                else -> {
                    try {

                    } catch (e: Exception) {
                        retryErrorMessage.value = (R.string.something_went_wrong)
                    }
                }
            }
        } catch (e: Exception) {
            when (e) {
                is SocketTimeoutException -> {
                    retryErrorMessage.value = (R.string.time_out_error)
                }
                else -> {
                    retryErrorMessage.value = (R.string.something_went_wrong)
                }
            }
        } finally {
            showLoading.postValue(false)
        }
    }

    open fun onSuccess(responseDTO: ResponseDTO) {
        hideProgress()
    }
//    open fun onSuccess(responseDTO: String) {
//        hideProgress()
//    }
//    open fun onSuccess(responseDTO: List<ResponseDTO>) {
//        hideProgress()
//    }

    open fun onSuccess(responseBody: ResponseBody) {
        hideProgress()
    }


    override fun onCleared() {
        disposables.clear()
        hideProgress()
    }

    open fun doNetworkOperation() {
        showProgress()
        Log.d(TAG, "doNetworkOperation: ${TAG}")
        observable.value?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(this::onSuccess, this::onError)
            ?.let {
                disposables.add(it)
            }
    }


    // 3372
    data class ToastLayoutParam(var msg: String, var duration: Int, var properties: String)

    var toastMessageG = MutableLiveData<ToastLayoutParam>()
    val getToastMessageG: LiveData<ToastLayoutParam> = toastMessageG
    fun showSnackMessage(
        msg: String,
        duration: Int = Snackbar.LENGTH_LONG,
        properties: String = AppConstants.PLAIN_SNACK_BAR
    ) {
        toastMessageG.value = ToastLayoutParam(msg, duration, properties)
        Log.d("TAG", "setCurrentDate: ${toastMessageG.value}")
    }

    data class ErrorResponse(
        var id: Int,
        var errorMessage: String,
        var errorCode: String,
        var timeStamp: String,
        var exceptionMessage: String
    )


}