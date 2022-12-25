package com.cemebsa.biomassa.models.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PengukuranNetwork(
    val pengukuran_id: String,
    val panjang: String,
    val bobot: String,
    val tanggal_ukur: String,
    val biota_id: String,
)
