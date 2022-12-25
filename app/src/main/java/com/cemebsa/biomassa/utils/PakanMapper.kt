package com.cemebsa.biomassa.utils

import com.cemebsa.biomassa.abstractions.mapper.IEntityMapper
import com.cemebsa.biomassa.models.domain.PakanDomain
import com.cemebsa.biomassa.models.entity.Pakan
import javax.inject.Inject

class PakanMapper @Inject constructor(): IEntityMapper<Pakan, PakanDomain> {
    override fun mapFromEntity(entity: Pakan): PakanDomain {
        return PakanDomain(
            pakan_id = entity.pakan_id,
            jenis_pakan = entity.jenis_pakan,
            deskripsi = entity.deskripsi
        )
    }

    override fun mapToEntity(target: PakanDomain): Pakan {
        return Pakan(
            pakan_id = target.pakan_id,
            jenis_pakan = target.jenis_pakan,
            deskripsi = target.deskripsi
        )
    }
}