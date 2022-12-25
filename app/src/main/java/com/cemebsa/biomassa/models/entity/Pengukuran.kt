package com.cemebsa.biomassa.models.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pengukuran",
    foreignKeys = [ForeignKey(
        entity = Biota::class,
        parentColumns = arrayOf("biota_id"),
        childColumns = arrayOf("biota_id"),
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    indices = [Index("biota_id")]
)
data class Pengukuran(
    @PrimaryKey(autoGenerate = true)
    val pengukuran_id: Int = 0,

    val panjang: Double,

    val bobot: Double,

    val tanggal_ukur: Long,

    val biota_id: Int
)
