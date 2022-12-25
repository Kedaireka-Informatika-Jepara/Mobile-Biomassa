package com.cemebsa.biomassa.repositories

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.cemebsa.biomassa.abstractions.dao.PengukuranDAO
import com.cemebsa.biomassa.abstractions.dao.PerhitunganDAO
import com.cemebsa.biomassa.models.domain.PengukuranDomain
import com.cemebsa.biomassa.models.entity.Pengukuran
import com.cemebsa.biomassa.models.network.PengukuranNetwork
import com.cemebsa.biomassa.models.network.container.PengukuranContainer
import com.cemebsa.biomassa.abstractions.service.MonitoringService
import com.cemebsa.biomassa.abstractions.mapper.IEntityMapper
import com.cemebsa.biomassa.models.domain.PerhitunganDomain
import com.cemebsa.biomassa.models.entity.Perhitungan
import com.cemebsa.biomassa.models.network.PerhitunganNetwork
import com.cemebsa.biomassa.models.network.container.PerhitunganContainer
import com.cemebsa.biomassa.utils.convertStringToDateLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerhitunganRepository @Inject constructor(
    private val perhitunganDAO: PerhitunganDAO,
    private val monitoringService: MonitoringService,
    private val sharedPreferences: SharedPreferences,
    private val perhitunganMapper: IEntityMapper<Perhitungan, PerhitunganDomain>,
    private val perhitunganNetworkMapper: IEntityMapper<PerhitunganNetwork, PerhitunganDomain>
) {
    fun getAllBiotaData(biota_id: Int): LiveData<List<PerhitunganDomain>> =
        Transformations.map(perhitunganDAO.getAll(biota_id).asLiveData()) { list ->
            list.map { perhitunganMapper.mapFromEntity(it) }
        }

    suspend fun deleteLocalPerhitungan(perhitunganId: Int) {
        withContext(Dispatchers.IO) { perhitunganDAO.deleteOne(perhitunganId) }
    }

    suspend fun refreshPerhitungan(biotaId: Int) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        withContext(Dispatchers.IO) {
            val response: Response<PerhitunganContainer> =
                monitoringService.getPerhitunganListAsync(token, userId, biotaId)

            when (response.code()) {
                200 -> {
                    val listPerhitunganNetwork: List<PerhitunganNetwork> = response.body()!!.data

                    val listPerhitungan = listPerhitunganNetwork.map { target ->
                        Perhitungan(
                            perhitungan_id = target.perhitungan_id.toInt(),
                            hidup = target.hidup.toInt(),
                            mati = target.mati.toInt(),
                            tanggal_hitung = convertStringToDateLong(
                                target.tanggal_hitung,
                                "yyyy-MM-dd"
                            ),
                            biota_id = target.biota_id.toInt()
                        )
                    }.toList()

                    if (perhitunganDAO.getPerhitunganCountFromBiota(biotaId) > listPerhitungan.size) {
                        perhitunganDAO.deletePerhitunganFromBiota(biotaId)
                    }

                    perhitunganDAO.insertAll(listPerhitungan)
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

    suspend fun addPerhitungan(perhitungan: PerhitunganDomain): PerhitunganContainer {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val perhitunganNetwork: PerhitunganNetwork = perhitunganNetworkMapper.mapToEntity(perhitungan)

        val data = mutableMapOf<String, String>()

        data["hidup"] = perhitunganNetwork.hidup

        data["mati"] = perhitunganNetwork.mati

        data["tanggal_hitung"] = perhitunganNetwork.tanggal_hitung

        data["biota_id"] = perhitunganNetwork.biota_id

        data["user_id"] = userId.toString()

        val response: Response<PerhitunganContainer> =
            monitoringService.addPerhitunganAsync(token, data)

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

    suspend fun deletePerhitungan(perhitunganId: Int): PerhitunganContainer {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val data = mutableMapOf<String, String>()

        data["user_id"] = userId.toString()

        data["perhitungan_id"] = perhitunganId.toString()

        val response: Response<PerhitunganContainer> =
            monitoringService.deletePerhitunganAsync(token, data)

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