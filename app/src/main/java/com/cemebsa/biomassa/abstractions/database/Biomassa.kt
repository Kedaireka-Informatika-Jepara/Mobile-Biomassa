package com.cemebsa.biomassa.abstractions

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cemebsa.biomassa.abstractions.dao.*
import com.cemebsa.biomassa.models.entity.*

@Database(entities = [Keramba::class, Biota::class, Pakan::class, Pengukuran::class, Perhitungan::class, Feeding::class, FeedingDetail::class, Panen::class], version = 1, exportSchema = false)
abstract class DatabaseBiomassa: RoomDatabase() {
    abstract fun kerambaDAO(): KerambaDAO

    abstract fun biotaDAO(): BiotaDAO

    abstract fun pakanDAO(): PakanDAO

    abstract fun pengukuranDAO(): PengukuranDAO

    abstract fun perhitunganDAO(): PerhitunganDAO

    abstract fun feedingDAO(): FeedingDAO

    abstract fun feedingDetailDAO(): FeedingDetailDAO

    abstract fun panenDAO(): PanenDAO

    companion object{
        const val DATABASE_NAME = "biomassa_db"
    }
}