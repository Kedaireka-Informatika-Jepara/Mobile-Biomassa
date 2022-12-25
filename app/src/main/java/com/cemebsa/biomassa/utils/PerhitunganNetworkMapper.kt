package com.cemebsa.biomassa.utils

import com.cemebsa.biomassa.abstractions.mapper.IEntityMapper
import com.cemebsa.biomassa.models.domain.PengukuranDomain
import com.cemebsa.biomassa.models.domain.PerhitunganDomain
import com.cemebsa.biomassa.models.network.PengukuranNetwork
import com.cemebsa.biomassa.models.network.PerhitunganNetwork
import javax.inject.Inject

class PerhitunganNetworkMapper @Inject constructor():
    IEntityMapper<PerhitunganNetwork, PerhitunganDomain> {
    override fun mapFromEntity(entity: PerhitunganNetwork): PerhitunganDomain {
        return PerhitunganDomain(
            perhitungan_id = entity.perhitungan_id.toInt(),
            hidup = entity.hidup.toInt(),
            mati = entity.mati.toInt(),
            tanggal_hitung = convertStringToDateLong(entity.tanggal_hitung, "yyyy-MM-dd"),
            biota_id = entity.biota_id.toInt()
        )
    }

    override fun mapToEntity(target: PerhitunganDomain): PerhitunganNetwork {
        return PerhitunganNetwork(
            perhitungan_id = target.perhitungan_id.toString(),
            hidup = target.hidup.toString(),
            mati = target.mati.toString(),
            tanggal_hitung = convertLongToDateString(target.tanggal_hitung, "yyyy-MM-dd"),
            biota_id = target.biota_id.toString()
        )
    }
}