package com.cemebsa.biomassa.models.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.cemebsa.biomassa.models.entity.FeedingDetail
import com.cemebsa.biomassa.models.entity.Pakan

data class FeedingDetailAndPakan(
    @Embedded val feedingDetail: FeedingDetail,

    @Relation(
        parentColumn = "pakan_id",
        entityColumn = "pakan_id"
    )
    val pakan: Pakan
)