package com.cemebsa.biomassa.utils

import com.cemebsa.biomassa.abstractions.mapper.IEntityMapper
import com.cemebsa.biomassa.models.domain.KerambaDomain
import com.cemebsa.biomassa.models.network.KerambaNetwork
import javax.inject.Inject

class KerambaNetworkMapper @Inject constructor(): IEntityMapper<KerambaNetwork, KerambaDomain> {
    override fun mapFromEntity(entity: KerambaNetwork): KerambaDomain {
        return KerambaDomain(
            keramba_id = entity.keramba_id.toInt(),
            nama_keramba = entity.nama,
            ukuran = entity.ukuran.toDouble(),
            tanggal_install = convertStringToDateLong(entity.tanggal_install, "yyyy-MM-dd")
        )
    }
    override fun mapToEntity(target: KerambaDomain): KerambaNetwork {
        return KerambaNetwork(
            keramba_id = target.keramba_id.toString(),
            nama = target.nama_keramba,
            ukuran = target.ukuran.toString(),
            tanggal_install = convertLongToDateString(target.tanggal_install, "yyyy-MM-dd")
        )
    }
}