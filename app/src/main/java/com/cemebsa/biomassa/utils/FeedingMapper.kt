package com.cemebsa.biomassa.utils

import com.cemebsa.biomassa.abstractions.mapper.IEntityMapper
import com.cemebsa.biomassa.models.domain.FeedingDomain
import com.cemebsa.biomassa.models.entity.Feeding
import javax.inject.Inject

class FeedingMapper @Inject constructor(): IEntityMapper<Feeding, FeedingDomain> {
    override fun mapFromEntity(entity: Feeding): FeedingDomain {
        return FeedingDomain(
            feeding_id = entity.feeding_id,
            keramba_id = entity.keramba_id,
            tanggal_feeding = entity.tanggal_feeding
        )
    }

    override fun mapToEntity(target: FeedingDomain): Feeding {
        return Feeding(
            feeding_id = target.feeding_id,
            keramba_id = target.keramba_id,
            tanggal_feeding = target.tanggal_feeding
        )
    }
}