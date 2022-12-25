package com.cemebsa.biomassa.abstractions.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cemebsa.biomassa.models.entity.Pengukuran
import kotlinx.coroutines.flow.Flow

@Dao
interface PengukuranDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(pengukuran: Pengukuran)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(listPengukuran: List<Pengukuran>)

    @Query("SELECT * FROM pengukuran WHERE biota_id =:biotaId ORDER BY tanggal_ukur DESC")
    fun getAll(biotaId: Int): Flow<List<Pengukuran>>

    @Query("SELECT COUNT(*) FROM pengukuran WHERE biota_id =:biotaId ORDER BY tanggal_ukur")
    fun getPengukuranCountFromBiota(biotaId: Int): Int

    @Query("DELETE FROM pengukuran WHERE biota_id =:biotaId")
    suspend fun deletePengukuranFromBiota(biotaId: Int)

    @Query("DELETE FROM pengukuran WHERE pengukuran_id =:pengukuranId")
    suspend fun deleteOne(pengukuranId: Int)
}