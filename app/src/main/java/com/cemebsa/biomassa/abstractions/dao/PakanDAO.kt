package com.cemebsa.biomassa.abstractions.dao

import androidx.room.*
import com.cemebsa.biomassa.models.entity.Pakan
import kotlinx.coroutines.flow.Flow

@Dao
interface PakanDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(pakan: Pakan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(listPakan: List<Pakan>)

    @Update
    suspend fun updateOne(pakan: Pakan)

    @Query("DELETE FROM pakan WHERE pakan_id =:pakanId")
    suspend fun deleteOne(pakanId: Int)

    @Query("SELECT * FROM pakan")
    fun getAll(): Flow<List<Pakan>>

    @Query("SELECT * FROM pakan WHERE pakan_id =:pakanId")
    fun getById(pakanId: Int): Flow<Pakan>

    @Query("DELETE FROM pakan")
    suspend fun deleteAllPakan()

    @Query("SELECT COUNT(*) FROM pakan")
    fun getPakanCount(): Int
}