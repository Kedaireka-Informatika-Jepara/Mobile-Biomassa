package com.cemebsa.biomassa.models.domain

data class FeedingDetailDomain(
    val detail_id: Int,
    val pakan_id: Int,
    val ukuran_tebar: Double,
    val waktu_feeding: Long,
    val banyak_pakan: Double,
    val feeding_id: Int,
)
