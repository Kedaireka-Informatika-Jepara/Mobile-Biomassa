package com.cemebsa.biomassa.models.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FeedingDetailNetwork(
    val detail_id: String,
    val ukuran_tebar: String,
    val banyak_pakan : String,
    val jam_feeding: String,
    val pakan_id: String,
    @Json(name = "activity_id") val feeding_id: String,
)
