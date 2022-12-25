package com.cemebsa.biomassa.models.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FeedingNetwork(
    @Json(name = "activity_id") val feeding_id: String,
    val keramba_id: String,
    val tanggal_feeding: String
)