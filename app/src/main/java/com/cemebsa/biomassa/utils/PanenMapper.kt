package com.cemebsa.biomassa.utils

import com.cemebsa.biomassa.abstractions.mapper.IEntityMapper
import com.cemebsa.biomassa.models.domain.PanenDomain
import com.cemebsa.biomassa.models.entity.Panen
import javax.inject.Inject

class PanenMapper @Inject constructor(): IEntityMapper<Panen, PanenDomain> {
    override fun mapFromEntity(entity: Panen): PanenDomain {
        return PanenDomain(
            activity_id = entity.activity_id,
            tanggal_panen = entity.tanggal_panen,
            panjang = entity.panjang,
            bobot = entity.bobot,
            jumlah_hidup = entity.jumlah_hidup,
            jumlah_mati = entity.jumlah_mati,
            //mortality_rate = entity.mortality_rate,
            biota_id = entity.biota_id,
            keramba_id = entity.keramba_id
        )
    }

    override fun mapToEntity(target: PanenDomain): Panen {
        return Panen(
            activity_id = target.activity_id,
            tanggal_panen = target.tanggal_panen,
            panjang = target.panjang,
            bobot = target.bobot,
            jumlah_hidup = target.jumlah_hidup,
            jumlah_mati = target.jumlah_mati,
            //mortality_rate = target.mortality_rate,
            biota_id = target.biota_id,
            keramba_id = target.keramba_id
        )
    }
}