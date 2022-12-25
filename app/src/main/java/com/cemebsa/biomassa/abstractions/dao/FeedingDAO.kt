package com.cemebsa.biomassa.abstractions.dao

import androidx.room.*
import com.cemebsa.biomassa.models.entity.Feeding
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedingDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(listFeeding: List<Feeding>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(feeding: Feeding)

    @Update
    suspend fun updateOne(feeding: Feeding)

    @Query("SELECT * FROM feeding WHERE keramba_id=:kerambaId ORDER BY tanggal_feeding DESC")
    fun getAll(kerambaId: Int): Flow<List<Feeding>>

    @Query("SELECT * FROM feeding WHERE feeding_id =:feedingId")
    fun getByFeedingId(feedingId: Int): Flow<Feeding>

    @Query("SELECT COUNT(*) FROM feeding WHERE keramba_id =:kerambaId")
    fun getFeedingCountFromKeramba(kerambaId: Int): Int

    @Query("DELETE FROM feeding WHERE keramba_id =:kerambaId")
    suspend fun deleteFeedingFromKeramba(kerambaId: Int)

    @Query("DELETE FROM feeding WHERE feeding_id =:feedingId")
    suspend fun deleteOne(feedingId: Int)
}