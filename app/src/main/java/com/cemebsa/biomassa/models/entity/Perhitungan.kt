package com.cemebsa.biomassa.models.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "perhitungan",
    foreignKeys = [ForeignKey(
        entity = Biota::class,
        parentColumns = arrayOf("biota_id"),
        childColumns = arrayOf("biota_id"),
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    indices = [Index("biota_id")]
)
data class Perhitungan(
    @PrimaryKey(autoGenerate = true)
    val perhitungan_id: Int = 0,

    val hidup: Int,

    val mati: Int,

    val tanggal_hitung: Long,

    val biota_id: Int
)
