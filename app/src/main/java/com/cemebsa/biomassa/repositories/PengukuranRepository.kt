package com.cemebsa.biomassa.repositories

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.cemebsa.biomassa.abstractions.dao.PengukuranDAO
import com.cemebsa.biomassa.models.domain.PengukuranDomain
import com.cemebsa.biomassa.models.entity.Pengukuran
import com.cemebsa.biomassa.models.network.PengukuranNetwork
import com.cemebsa.biomassa.models.network.container.PengukuranContainer
import com.cemebsa.biomassa.abstractions.service.MonitoringService
import com.cemebsa.biomassa.abstractions.mapper.IEntityMapper
import com.cemebsa.biomassa.utils.convertStringToDateLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PengukuranRepository @Inject constructor(
    private val pengukuranDAO: PengukuranDAO,
    private val monitoringService: MonitoringService,
    private val sharedPreferences: SharedPreferences,
    private val pengukuranMapper: IEntityMapper<Pengukuran, PengukuranDomain>,
    private val pengukuranNetworkMapper: IEntityMapper<PengukuranNetwork, PengukuranDomain>
) {
    fun getAllBiotaData(biota_id: Int): LiveData<List<PengukuranDomain>> =
        Transformations.map(pengukuranDAO.getAll(biota_id).asLiveData()) { list ->
            list.map { pengukuranMapper.mapFromEntity(it) }
        }

    suspend fun deleteLocalPengukuran(pengukuranId: Int) {
        withContext(Dispatchers.IO) { pengukuranDAO.deleteOne(pengukuranId) }
    }

    suspend fun refreshPengukuran(biotaId: Int) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        withContext(Dispatchers.IO) {
            val response: Response<PengukuranContainer> =
                monitoringService.getPengukuranListAsync(token, userId, biotaId)

            when (response.code()) {
                200 -> {
                    val listPengukuranNetwork: List<PengukuranNetwork> = response.body()!!.data

                    val listPengukuran = listPengukuranNetwork.map { target ->
                        Pengukuran(
                            pengukuran_id = target.pengukuran_id.toInt(),
                            panjang = target.panjang.toDouble(),
                            bobot = target.bobot.toDouble(),
                            tanggal_ukur = convertStringToDateLong(
                                target.tanggal_ukur,
                                "yyyy-MM-dd"
                            ),
                            biota_id = target.biota_id.toInt()
                        )
                    }.toList()

                    if (pengukuranDAO.getPengukuranCountFromBiota(biotaId) > listPengukuran.size) {
                        pengukuranDAO.deletePengukuranFromBiota(biotaId)
                    }

                    pengukuranDAO.insertAll(listPengukuran)
                }

                500 -> throw Exception("Internal Server Error")

                401 -> throw Exception("Unauthorized")

                400 -> {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    throw Exception(jsonObj.getString("message"))
                }

                else -> throw Exception("HTTP Request Failed")
            }
        }
    }

    suspend fun addPengukuran(pengukuran: PengukuranDomain): PengukuranContainer {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val pengukuranNetwork: PengukuranNetwork = pengukuranNetworkMapper.mapToEntity(pengukuran)

        val data = mutableMapOf<String, String>()

        data["panjang"] = pengukuranNetwork.panjang

        data["bobot"] = pengukuranNetwork.bobot

        data["tanggal_ukur"] = pengukuranNetwork.tanggal_ukur

        data["biota_id"] = pengukuranNetwork.biota_id

        data["user_id"] = userId.toString()

        val response: Response<PengukuranContainer> =
            monitoringService.addPengukuranAsync(token, data)

        when (response.code()) {
            201 -> return response.body()!!

            500 -> throw Exception("Internal Server Error")

            401 -> throw Exception("Unauthorized")

            400 -> {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                throw Exception(jsonObj.getString("message"))
            }

            else -> throw Exception("HTTP Request Failed")
        }
    }

    suspend fun deletePengukuran(pengukuranId: Int): PengukuranContainer {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val data = mutableMapOf<String, String>()

        data["user_id"] = userId.toString()

        data["pengukuran_id"] = pengukuranId.toString()

        val response: Response<PengukuranContainer> =
            monitoringService.deletePengukuranAsync(token, data)

        when (response.code()) {
            200 -> return response.body()!!

            500 -> throw Exception("Internal Server Error")

            401 ->
                throw Exception("Unauthorized")

            400 -> {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                throw Exception(jsonObj.getString("message"))
            }
            else -> {
                throw Exception("HTTP Request Failed")
            }
        }
    }
}