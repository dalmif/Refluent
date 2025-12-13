package io.kayt.refluent.core.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequestModel(val phoneId: String, val type: String, val appVersion : Int?)