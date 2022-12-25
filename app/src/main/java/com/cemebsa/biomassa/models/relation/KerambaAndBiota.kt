package com.cemebsa.biomassa.models.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.cemebsa.biomassa.models.entity.Biota
import com.cemebsa.biomassa.models.entity.Keramba

data class KerambaAndBiota(
    @Embedded val keramba: Keramba,
    @Relation(
        parentColumn = "keramba_id",
        entityColumn = "keramba_id"
    )
    val biotaList: List<Biota>
)
