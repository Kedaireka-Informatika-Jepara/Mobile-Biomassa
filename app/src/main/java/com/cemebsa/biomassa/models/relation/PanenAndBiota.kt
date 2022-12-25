package com.cemebsa.biomassa.models.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.cemebsa.biomassa.models.entity.Biota
import com.cemebsa.biomassa.models.entity.Panen

data class PanenAndBiota(
    @Embedded val panen: Panen,
    @Relation(
        parentColumn = "biota_id",
        entityColumn = "biota_id"
    )
    val biota: Biota
)