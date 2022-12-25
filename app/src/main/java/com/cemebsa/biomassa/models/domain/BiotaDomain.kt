package com.cemebsa.biomassa.models.domain

data class BiotaDomain(
    val biota_id: Int,
    val jenis_biota: String,
    val bobot: Double,
    val panjang: Double,
    val jumlah_bibit: Int,
    val tanggal_tebar: Long,
    val tanggal_panen: Long,
    val keramba_id: Int,
)