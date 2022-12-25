package com.cemebsa.biomassa.utils

import com.cemebsa.biomassa.abstractions.mapper.IEntityMapper
import com.cemebsa.biomassa.models.domain.FeedingDetailDomain
import com.cemebsa.biomassa.models.network.FeedingDetailNetwork
import javax.inject.Inject

class FeedingDetailNetworkMapper @Inject constructor():
    IEntityMapper<FeedingDetailNetwork, FeedingDetailDomain> {
    override fun mapFromEntity(entity: FeedingDetailNetwork): FeedingDetailDomain {
        return FeedingDetailDomain(
            detail_id = entity.detail_id.toInt(),
            feeding_id = entity.feeding_id.toInt(),
            ukuran_tebar = entity.ukuran_tebar.toDouble(),
            banyak_pakan = entity.banyak_pakan.toDouble(),
            waktu_feeding = convertStringToDateLong(entity.jam_feeding,"H:m"),
            pakan_id = entity.pakan_id.toInt()
        )
    }

    override fun mapToEntity(target: FeedingDetailDomain): FeedingDetailNetwork {
        return FeedingDetailNetwork(
            detail_id = target.detail_id.toString(),
            feeding_id = target.feeding_id.toString(),
            ukuran_tebar = target.ukuran_tebar.toString(),
            banyak_pakan = target.banyak_pakan.toString(),
            jam_feeding = convertLongToDateString(target.waktu_feeding, "H:m"),
            pakan_id = target.pakan_id.toString()
        )
    }
}