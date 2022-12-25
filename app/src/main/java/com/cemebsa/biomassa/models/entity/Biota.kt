package com.cemebsa.biomassa.models.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "biota",
    foreignKeys= [ForeignKey(
        entity = Keramba::class,
        parentColumns = arrayOf("keramba_id"),
        childColumns = arrayOf("keramba_id"),
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    indices = [Index("keramba_id")]
)
data class Biota constructor(
    @PrimaryKey(autoGenerate = true)
    val biota_id: Int = 0,
    val jenis_biota: String,
    val bobot: Double,
    val panjang: Double,
    val jumlah_bibit: Int,
    val tanggal_tebar: Long,
    val tanggal_panen: Long = 0,
    val keramba_id: Int,
)
