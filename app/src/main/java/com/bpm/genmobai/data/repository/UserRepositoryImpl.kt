package com.bpm.genmobai.data.repository

import com.bpm.genmobai.data.api_service.PermissionDetails
import com.bpm.genmobai.data.response.PromptResponseDTO
import io.reactivex.Observable
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(userService: PermissionDetails) : PermissionDetails {
    private var mPermissionDetails: PermissionDetails = userService
    override fun getPermissionDetails(
        googlePlayStoreId: String,
        text: String
    ): Observable<PromptResponseDTO> {
        return mPermissionDetails.getPermissionDetails(googlePlayStoreId, text).doOnNext { }
    }


}