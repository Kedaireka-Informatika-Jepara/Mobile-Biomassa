package com.cemebsa.biomassa.models.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KerambaNetwork(
    val keramba_id: String,
    val nama: String,
    val ukuran: String,
    val tanggal_install: String
)
