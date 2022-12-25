package com.cemebsa.biomassa.models.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PakanNetwork(
    val pakan_id: String,
    val jenis_pakan: String,
    val deskripsi: String
)
