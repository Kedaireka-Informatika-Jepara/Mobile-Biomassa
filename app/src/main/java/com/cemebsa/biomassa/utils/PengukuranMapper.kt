package com.cemebsa.biomassa.utils

import com.cemebsa.biomassa.abstractions.mapper.IEntityMapper
import com.cemebsa.biomassa.models.domain.PengukuranDomain
import com.cemebsa.biomassa.models.entity.Pengukuran
import javax.inject.Inject

class PengukuranMapper @Inject constructor(): IEntityMapper<Pengukuran, PengukuranDomain> {
    override fun mapFromEntity(entity: Pengukuran): PengukuranDomain {
        return PengukuranDomain(
            pengukuran_id = entity.pengukuran_id,
            panjang = entity.panjang,
            bobot = entity.bobot,
            tanggal_ukur = entity.tanggal_ukur,
            biota_id = entity.biota_id
        )
    }

    override fun mapToEntity(target: PengukuranDomain): Pengukuran {
        return Pengukuran(
            pengukuran_id = target.pengukuran_id,
            panjang = target.panjang,
            bobot = target.bobot,
            tanggal_ukur = target.tanggal_ukur,
            biota_id = target.biota_id
        )
    }
}