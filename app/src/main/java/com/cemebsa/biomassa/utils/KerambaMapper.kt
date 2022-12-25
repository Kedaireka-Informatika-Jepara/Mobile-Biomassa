package com.cemebsa.biomassa.utils

import com.cemebsa.biomassa.abstractions.mapper.IEntityMapper
import com.cemebsa.biomassa.models.domain.KerambaDomain
import com.cemebsa.biomassa.models.entity.Keramba
import javax.inject.Inject

class KerambaMapper @Inject constructor(): IEntityMapper<Keramba, KerambaDomain> {
    override fun mapFromEntity(entity: Keramba): KerambaDomain {
        return KerambaDomain(
            keramba_id = entity.keramba_id,
            nama_keramba = entity.nama_keramba,
            ukuran = entity.ukuran,
            tanggal_install = entity.tanggal_install
        )
    }

    override fun mapToEntity(target: KerambaDomain): Keramba {
        return Keramba(
            keramba_id = target.keramba_id,
            nama_keramba = target.nama_keramba,
            ukuran = target.ukuran,
            tanggal_install = target.tanggal_install
        )
    }
}