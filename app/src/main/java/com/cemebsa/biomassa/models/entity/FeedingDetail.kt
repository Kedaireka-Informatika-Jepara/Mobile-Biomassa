package com.cemebsa.biomassa.models.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "feeding_detail",
    foreignKeys = [ForeignKey(
        entity = Feeding::class,
        parentColumns = arrayOf("feeding_id"),
        childColumns = arrayOf("feeding_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Pakan::class,
        parentColumns = arrayOf("pakan_id"),
        childColumns = arrayOf("pakan_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index("feeding_id"), Index("pakan_id")]
)
data class FeedingDetail constructor(
    @PrimaryKey(autoGenerate = true)
    val detail_id: Int = 0,
    val pakan_id: Int,
    val ukuran_tebar: Double,
    val waktu_feeding: Long,
    val banyak_pakan: Double,
    val feeding_id: Int,
)
