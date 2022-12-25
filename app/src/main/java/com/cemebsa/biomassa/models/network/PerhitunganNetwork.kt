package com.cemebsa.biomassa.models.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PerhitunganNetwork(
    val perhitungan_id: String,
    val hidup: String,
    val mati: String,
    val tanggal_hitung: String,
    val biota_id: String,
)
