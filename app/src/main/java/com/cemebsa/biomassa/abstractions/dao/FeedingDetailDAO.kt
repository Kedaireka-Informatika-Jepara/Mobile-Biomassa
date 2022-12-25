package com.cemebsa.biomassa.abstractions.dao

import androidx.room.*
import com.cemebsa.biomassa.models.entity.Feeding
import com.cemebsa.biomassa.models.entity.FeedingDetail
import com.cemebsa.biomassa.models.relation.FeedingDetailAndPakan
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedingDetailDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(feedingDetail: FeedingDetail)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(listFeedingDetail: List<FeedingDetail>)

    @Query("DELETE FROM feeding_detail WHERE detail_id =:detailId")
    suspend fun deleteOne(detailId: Int)

    @Update
    suspend fun updateOne(feeding: Feeding)

    @Query("SELECT * FROM feeding_detail WHERE feeding_id =:feedingId ORDER BY waktu_feeding DESC")
    fun getAll(feedingId: Int): Flow<List<FeedingDetail>>

    @Transaction
    @Query("SELECT * FROM feeding_detail WHERE feeding_id =:feedingId ORDER BY waktu_feeding DESC")
    fun getAllDetailAndPakan(feedingId: Int): Flow<List<FeedingDetailAndPakan>>

    @Query("SELECT COUNT(*) FROM feeding_detail WHERE feeding_id=:feedingId")
    fun getDetailCountFromFeeding(feedingId: Int): Int

    @Query("DELETE FROM feeding_detail WHERE feeding_id =:feedingId")
    suspend fun deleteDetailFromFeeding(feedingId: Int)
}