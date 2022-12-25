package com.cemebsa.biomassa.models.domain

data class PengukuranDomain(
    val pengukuran_id: Int,
    val panjang: Double,
    val bobot: Double,
    val tanggal_ukur: Long,
    val biota_id: Int
)