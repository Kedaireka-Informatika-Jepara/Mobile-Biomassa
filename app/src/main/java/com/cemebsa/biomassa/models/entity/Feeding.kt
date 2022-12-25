package com.cemebsa.biomassa.models.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "feeding",
    foreignKeys = [ForeignKey(
        entity = Keramba::class,
        parentColumns = arrayOf("keramba_id"),
        childColumns = arrayOf("keramba_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index("keramba_id")]
)
data class Feeding constructor(
    @PrimaryKey(autoGenerate = true)
    val feeding_id: Int = 0,
    val keramba_id: Int,
    val tanggal_feeding: Long,
)