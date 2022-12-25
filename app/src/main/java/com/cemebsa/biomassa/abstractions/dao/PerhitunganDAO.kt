package com.cemebsa.biomassa.abstractions.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cemebsa.biomassa.models.entity.Pengukuran
import com.cemebsa.biomassa.models.entity.Perhitungan
import kotlinx.coroutines.flow.Flow

@Dao
interface PerhitunganDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(perhitungan: Perhitungan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(listPerhitungan: List<Perhitungan>)

    @Query("SELECT * FROM perhitungan WHERE biota_id =:biotaId ORDER BY tanggal_hitung DESC")
    fun getAll(biotaId: Int): Flow<List<Perhitungan>>

    @Query("SELECT COUNT(*) FROM perhitungan WHERE biota_id =:biotaId ORDER BY tanggal_hitung")
    fun getPerhitunganCountFromBiota(biotaId: Int): Int

    @Query("DELETE FROM perhitungan WHERE biota_id =:biotaId")
    suspend fun deletePerhitunganFromBiota(biotaId: Int)

    @Query("DELETE FROM perhitungan WHERE perhitungan_id =:perhitunganId")
    suspend fun deleteOne(perhitunganId: Int)
}