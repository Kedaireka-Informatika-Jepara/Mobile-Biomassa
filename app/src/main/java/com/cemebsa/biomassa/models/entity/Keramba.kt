package com.cemebsa.biomassa.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keramba")
data class Keramba constructor(
    @PrimaryKey(autoGenerate = true)
    val keramba_id: Int = 0,
    val nama_keramba: String,
    val ukuran: Double,
    val tanggal_install: Long = 0,
)
