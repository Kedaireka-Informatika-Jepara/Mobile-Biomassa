package com.cemebsa.biomassa.utils

import com.cemebsa.biomassa.abstractions.mapper.IEntityMapper
import com.cemebsa.biomassa.models.domain.PengukuranDomain
import com.cemebsa.biomassa.models.domain.PerhitunganDomain
import com.cemebsa.biomassa.models.entity.Pengukuran
import com.cemebsa.biomassa.models.entity.Perhitungan
import javax.inject.Inject

class PerhitunganMapper @Inject constructor(): IEntityMapper<Perhitungan, PerhitunganDomain> {
    override fun mapFromEntity(entity: Perhitungan): PerhitunganDomain {
        return PerhitunganDomain(
            perhitungan_id = entity.perhitungan_id,
            hidup = entity.hidup,
            mati = entity.mati,
            tanggal_hitung = entity.tanggal_hitung,
            biota_id = entity.biota_id
        )
    }

    override fun mapToEntity(target: PerhitunganDomain): Perhitungan {
        return Perhitungan(
            perhitungan_id = target.perhitungan_id,
            hidup = target.hidup,
            mati = target.mati,
            tanggal_hitung = target.tanggal_hitung,
            biota_id = target.biota_id
        )
    }
}