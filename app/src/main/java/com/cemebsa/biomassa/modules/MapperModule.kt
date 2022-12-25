package com.cemebsa.biomassa.modules

import com.cemebsa.biomassa.abstractions.mapper.IEntityMapper
import com.cemebsa.biomassa.models.domain.*
import com.cemebsa.biomassa.models.entity.*
import com.cemebsa.biomassa.models.network.*
import com.cemebsa.biomassa.utils.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MapperModule {

    @Binds
    abstract fun bindBiotaMapper(
        biotaMapper: BiotaMapper
    ): IEntityMapper<Biota, BiotaDomain>

    @Binds
    abstract fun bindBiotaNetworkMapper(
        biotaNetworkMapper: BiotaNetworkMapper
    ): IEntityMapper<BiotaNetwork, BiotaDomain>

    @Binds
    abstract fun bindKerambaMapper(
        kerambaMapper: KerambaMapper
    ): IEntityMapper<Keramba, KerambaDomain>

    @Binds
    abstract fun bindPakanMapper(
        pakanMapper: PakanMapper
    ): IEntityMapper<Pakan, PakanDomain>

    @Binds
    abstract fun bindPakanNetworkMapper(
        pakanNetworkMapper: PakanNetworkMapper
    ): IEntityMapper<PakanNetwork, PakanDomain>

    @Binds
    abstract fun bindPengukuranMapper(
        pengukuranMapper: PengukuranMapper
    ): IEntityMapper<Pengukuran, PengukuranDomain>

    @Binds
    abstract fun bindPengukuranNetworkMapper(
        pengukuranNetworkMapper: PengukuranNetworkMapper
    ): IEntityMapper<PengukuranNetwork, PengukuranDomain>

    @Binds
    abstract fun bindPerhitunganMapper(
        perhitunganMapper: PerhitunganMapper
    ): IEntityMapper<Perhitungan, PerhitunganDomain>

    @Binds
    abstract fun bindPerhitunganNetworkMapper(
        perhitunganNetworkMapper: PerhitunganNetworkMapper
    ): IEntityMapper<PerhitunganNetwork, PerhitunganDomain>

    @Binds
    abstract fun bindFeedingMapper(
        feedingMapper: FeedingMapper
    ): IEntityMapper<Feeding, FeedingDomain>

    @Binds
    abstract fun bindFeedingDetailMapper(
        feedingDetailMapper: FeedingDetailMapper
    ): IEntityMapper<FeedingDetail, FeedingDetailDomain>
  
    @Binds
    abstract fun bindKerambaNetworkMapper(
        kerambaNetworkMapper: KerambaNetworkMapper
    ): IEntityMapper<KerambaNetwork, KerambaDomain>

    @Binds
    abstract fun bindPanenMapper(
        panenMapper: PanenMapper
    ): IEntityMapper<Panen, PanenDomain>

    @Binds
    abstract fun bindPanenNetworkMapper(
        panenNetworkMapper: PanenNetworkMapper
    ): IEntityMapper<PanenNetwork, PanenDomain>

    @Binds
    abstract fun bindFeedingNetworkMapper(
        feedingNetworkMapper: FeedingNetworkMapper
    ): IEntityMapper<FeedingNetwork, FeedingDomain>

    @Binds
    abstract fun bindFeedingDetailNetworkMapper(
        feedingDetailNetworkMapper: FeedingDetailNetworkMapper
    ): IEntityMapper<FeedingDetailNetwork, FeedingDetailDomain>
}
