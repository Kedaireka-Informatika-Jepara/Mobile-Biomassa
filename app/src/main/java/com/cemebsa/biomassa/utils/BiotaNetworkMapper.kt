package com.cemebsa.biomassa.utils

import com.cemebsa.biomassa.abstractions.mapper.IEntityMapper
import com.cemebsa.biomassa.models.domain.BiotaDomain
import com.cemebsa.biomassa.models.network.BiotaNetwork
import javax.inject.Inject

class BiotaNetworkMapper @Inject constructor() : IEntityMapper<BiotaNetwork, BiotaDomain> {
    override fun mapFromEntity(entity: BiotaNetwork): BiotaDomain {
        return BiotaDomain(
            biota_id = entity.biota_id.toInt(),
            jenis_biota = entity.jenis_biota,
            bobot = entity.bobot.toDouble(),
            panjang = entity.panjang.toDouble(),
            jumlah_bibit = entity.jumlah_bibit.toInt(),
            tanggal_tebar = convertStringToDateLong(entity.tanggal_tebar, "yyyy-MM-dd"),
            tanggal_panen = convertStringToDateLong(entity.tanggal_panen!!, "yyyy-MM-dd"),
            keramba_id = entity.keramba_id.toInt()
        )
    }

    override fun mapToEntity(target: BiotaDomain): BiotaNetwork {
        return BiotaNetwork(
            biota_id = target.biota_id.toString(),
            jenis_biota = target.jenis_biota,
            bobot = target.bobot.toString(),
            panjang = target.panjang.toString(),
            jumlah_bibit = target.jumlah_bibit.toString(),
            tanggal_tebar = convertLongToDateString(target.tanggal_tebar, "yyyy-MM-dd"),
            tanggal_panen = convertLongToDateString(target.tanggal_panen, "yyyy-MM-dd"),
            keramba_id = target.keramba_id.toString()
        )
    }
}