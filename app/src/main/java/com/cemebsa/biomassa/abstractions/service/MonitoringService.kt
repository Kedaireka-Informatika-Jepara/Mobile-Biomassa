package com.cemebsa.biomassa.abstractions.service

import com.cemebsa.biomassa.models.network.container.*
import retrofit2.Response
import retrofit2.http.*

interface MonitoringService {

    //login
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun loginUserAsync(@Field("username") username: String, @Field("password") password: String)
            : Response<LoginContainer>

    // keramba section
    @GET("keramba")
    suspend fun getKerambaListAsync(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int
    ): Response<KerambaContainer>

    @FormUrlEncoded
    @POST("keramba")
    suspend fun addKerambaAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<KerambaContainer>

    @FormUrlEncoded
    @PUT("keramba")
    suspend fun updateKerambaAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<KerambaContainer>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "keramba", hasBody = true)
    suspend fun deleteKerambaAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<KerambaContainer>

    // biota section
    @GET("biota")
    suspend fun getBiotaListAsync(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int,
        @Query("keramba_id") kerambaId: Int
    ): Response<BiotaContainer>

    @GET("history")
    suspend fun getHistoryBiotaListAsync(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int,
        @Query("keramba_id") kerambaId: Int
    ): Response<BiotaContainer>

    @FormUrlEncoded
    @POST("biota")
    suspend fun addBiotaAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<BiotaContainer>

    @FormUrlEncoded
    @PUT("biota")
    suspend fun updateBiotaAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<BiotaContainer>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "biota", hasBody = true)
    suspend fun deleteBiotaAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<BiotaContainer>

    // pakan section
    @GET("pakan")
    suspend fun getPakanListAsync(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int
    ): Response<PakanContainer>

    @FormUrlEncoded
    @POST("pakan")
    suspend fun addPakanAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<PakanContainer>

    @FormUrlEncoded
    @PUT("pakan")
    suspend fun updatePakanAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<PakanContainer>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "pakan", hasBody = true)
    suspend fun deletePakanAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<PakanContainer>

    //pengukuran section
    @GET("pengukuran")
    suspend fun getPengukuranListAsync(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int,
        @Query("biota_id") biotaId: Int
    ): Response<PengukuranContainer>

    @FormUrlEncoded
    @POST("pengukuran")
    suspend fun addPengukuranAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<PengukuranContainer>

    @FormUrlEncoded
    @PUT("pengukuran")
    suspend fun updatePengukuranAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<PengukuranContainer>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "pengukuran", hasBody = true)
    suspend fun deletePengukuranAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<PengukuranContainer>

    //perhitungan
    @GET("perhitungan")
    suspend fun getPerhitunganListAsync(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int,
        @Query("biota_id") biotaId: Int
    ): Response<PerhitunganContainer>

    @FormUrlEncoded
    @POST("perhitungan")
    suspend fun addPerhitunganAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<PerhitunganContainer>

    @FormUrlEncoded
    @PUT("perhitungan")
    suspend fun updatePerhitunganAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<PerhitunganContainer>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "perhitungan", hasBody = true)
    suspend fun deletePerhitunganAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<PerhitunganContainer>

    //feeding
    @GET("feeding")
    suspend fun getFeedingListAsync(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int,
        @Query("keramba_id") kerambaId: Int
    ): Response<FeedingContainer>

    @FormUrlEncoded
    @POST("feeding")
    suspend fun addFeedingAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<FeedingContainer>

    @FormUrlEncoded
    @PUT("feeding")
    suspend fun updateFeedingAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<FeedingContainer>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "feeding", hasBody = true)
    suspend fun deleteFeedingAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<FeedingContainer>

    @GET("feeding/detail")
    suspend fun getFeedingDetailListAsync(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int,
        @Query("activity_id") activityId: Int
    ): Response<FeedingDetailContainer>

    @FormUrlEncoded
    @POST("feeding/detail")
    suspend fun addFeedingDetailAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<FeedingDetailContainer>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "feeding/detail", hasBody = true)
    suspend fun deleteFeedingDetailAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<FeedingDetailContainer>

    //panen
    @GET("panen")
    suspend fun getPanenListAsync(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int,
        @Query("keramba_id") kerambaId: Int
    ): Response<PanenContainer>

    @FormUrlEncoded
    @POST("panen")
    suspend fun addPanenAsync(
        @Header("Authorization") token: String,
        @FieldMap data: Map<String, String>
    ): Response<PanenContainer>
}