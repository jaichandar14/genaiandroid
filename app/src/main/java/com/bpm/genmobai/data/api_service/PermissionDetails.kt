package com.bpm.genmobai.data.api_service

import com.bpm.genmobai.data.response.PromptResponseDTO
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface PermissionDetails {

    @GET("get-privacy-policy-content/{googlePlayStoreId}/permissionType/{permissionType}")
    fun getPermissionDetails(
        @Path("googlePlayStoreId") googlePlayStoreId: String,
        @Path("permissionType") text: String
    ): Observable<PromptResponseDTO>
}