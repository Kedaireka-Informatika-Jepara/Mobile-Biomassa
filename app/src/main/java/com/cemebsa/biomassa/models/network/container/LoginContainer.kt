package com.cemebsa.biomassa.models.network.container

import com.cemebsa.biomassa.models.network.LoggedInUser
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginContainer(
    override val status: Boolean,
    override val message: String,
    override val data: List<LoggedInUser>
): BaseContainer<LoggedInUser>()