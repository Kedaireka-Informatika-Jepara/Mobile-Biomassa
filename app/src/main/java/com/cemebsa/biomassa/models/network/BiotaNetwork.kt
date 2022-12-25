package com.cemebsa.biomassa.models.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BiotaNetwork(
    val biota_id: String,
    val jenis_biota: String,
    val bobot: String,
    val panjang: String,
    val jumlah_bibit: String,
    val tanggal_tebar: String,
    val tanggal_panen: String?,
    val keramba_id: String,
)