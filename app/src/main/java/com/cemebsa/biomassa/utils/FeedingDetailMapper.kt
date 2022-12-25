package com.cemebsa.biomassa.utils

import com.cemebsa.biomassa.abstractions.mapper.IEntityMapper
import com.cemebsa.biomassa.models.domain.FeedingDetailDomain
import com.cemebsa.biomassa.models.entity.FeedingDetail
import javax.inject.Inject

class FeedingDetailMapper @Inject constructor(): IEntityMapper<FeedingDetail, FeedingDetailDomain> {
    override fun mapFromEntity(entity: FeedingDetail): FeedingDetailDomain {
        return FeedingDetailDomain(
            detail_id = entity.detail_id,
            feeding_id = entity.feeding_id,
            pakan_id = entity.pakan_id,
            ukuran_tebar = entity.ukuran_tebar,
            banyak_pakan = entity.banyak_pakan,
            waktu_feeding = entity.waktu_feeding,
        )
    }

    override fun mapToEntity(target: FeedingDetailDomain): FeedingDetail {
        return FeedingDetail(
            detail_id = target.detail_id,
            feeding_id = target.feeding_id,
            pakan_id = target.pakan_id,
            ukuran_tebar = target.ukuran_tebar,
            banyak_pakan = target.banyak_pakan,
            waktu_feeding = target.waktu_feeding,
        )
    }
}