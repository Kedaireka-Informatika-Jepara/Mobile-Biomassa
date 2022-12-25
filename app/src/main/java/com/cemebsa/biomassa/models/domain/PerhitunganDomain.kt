package com.cemebsa.biomassa.models.domain

data class PerhitunganDomain(
    val perhitungan_id: Int,
    val hidup: Int,
    val mati: Int,
    val tanggal_hitung: Long,
    val biota_id: Int
)