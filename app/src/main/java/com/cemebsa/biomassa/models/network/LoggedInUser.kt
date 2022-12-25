package com.cemebsa.biomassa.models.network

import com.squareup.moshi.JsonClass

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@JsonClass(generateAdapter = true)
data class LoggedInUser(
    val token: String,
    val username: String,
    val user_id: String
)