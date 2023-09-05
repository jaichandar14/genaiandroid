package com.bpm.genmobai.data.response

data class PromptResponseDTO(
    var isPrivacyPolicyAvailable: Boolean,
    var message: String,
    var privacyPolicyUrl: String
) : ResponseDTO()