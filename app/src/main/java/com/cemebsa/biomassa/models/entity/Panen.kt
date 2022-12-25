package com.cemebsa.biomassa.models.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "panen",
    foreignKeys = [
        ForeignKey(
            entity = Keramba::class,
            parentColumns = arrayOf("keramba_id"),
            childColumns = arrayOf("keramba_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Biota::class,
            parentColumns = arrayOf("biota_id"),
            childColumns = arrayOf("biota_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("keramba_id"), Index("biota_id")]
)
data class Panen(
    @PrimaryKey(autoGenerate = true)
    val activity_id: Int = 0,
    val tanggal_panen: Long,
    val panjang: Double,
    val bobot: Double,
    val jumlah_hidup: Int,
    val jumlah_mati: Int,
    //val mortality_rate: Double,
    val biota_id: Int,
    val keramba_id: Int
)
