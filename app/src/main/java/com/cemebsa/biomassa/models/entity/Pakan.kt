package com.cemebsa.biomassa.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pakan")
data class Pakan(
    @PrimaryKey(autoGenerate = true)
    val pakan_id: Int = 0,

    val jenis_pakan: String,

    val deskripsi: String
)
