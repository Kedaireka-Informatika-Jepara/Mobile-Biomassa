package com.cemebsa.biomassa.repositories

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.cemebsa.biomassa.abstractions.dao.BiotaDAO
import com.cemebsa.biomassa.models.domain.BiotaDomain
import com.cemebsa.biomassa.models.entity.Biota
import com.cemebsa.biomassa.models.network.BiotaNetwork
import com.cemebsa.biomassa.models.network.container.BiotaContainer
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
class BiotaRepository @Inject constructor(
    private val biotaDAO: BiotaDAO,
    private val monitoringService: MonitoringService,
    private val sharedPreferences: SharedPreferences,
    private val biotaMapper: IEntityMapper<Biota, BiotaDomain>,
    private val biotaNetworkMapper: IEntityMapper<BiotaNetwork, BiotaDomain>
) {
    fun getAllBiota(id: Int): LiveData<List<BiotaDomain>> =
        Transformations.map(biotaDAO.getAll(id).asLiveData()) { list ->
            list.map { biotaMapper.mapFromEntity(it) }
        }

    fun getAllBiotaHistory(id: Int): LiveData<List<BiotaDomain>> =
        Transformations.map(biotaDAO.getAllHistory(id).asLiveData()) { list ->
            list.map { biotaMapper.mapFromEntity(it) }
        }

    suspend fun updateLocalBiota(
        biotaId: Int,
        jenis: String,
        bobot: String,
        panjang: String,
        jumlah: String,
        kerambaId: Int,
        tanggal: Long,
    ) {
        withContext(Dispatchers.IO) {
            biotaDAO.updateOne(
                Biota(
                    biota_id = biotaId,
                    jenis_biota = jenis,
                    bobot = bobot.toDouble(),
                    panjang = panjang.toDouble(),
                    jumlah_bibit = jumlah.toInt(),
                    tanggal_tebar = tanggal,
                    keramba_id = kerambaId,
                    tanggal_panen = 0L
                )
            )
        }
    }

    suspend fun deleteLocalBiota(biotaId: Int) {
        withContext(Dispatchers.IO) { biotaDAO.deleteOne(biotaId) }
    }

    fun loadBiotaData(id: Int): LiveData<BiotaDomain> {
        return Transformations.map(
            biotaDAO.getById(id).asLiveData()
        ) { biotaMapper.mapFromEntity(it) }
    }

    suspend fun refreshBiota(kerambaId: Int) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        withContext(Dispatchers.IO) {
            val response: Response<BiotaContainer> =
                monitoringService.getBiotaListAsync(token, userId, kerambaId)

            when (response.code()) {
                200 -> {
                    val listBiotaNetwork: List<BiotaNetwork> = response.body()!!.data

                    val listBiota: List<Biota> = listBiotaNetwork.map { target ->
                        Biota(
                            biota_id = target.biota_id.toInt(),
                            jenis_biota = target.jenis_biota,
                            bobot = target.bobot.toDouble(),
                            panjang = target.panjang.toDouble(),
                            jumlah_bibit = target.jumlah_bibit.toInt(),
                            tanggal_tebar = convertStringToDateLong(
                                target.tanggal_tebar,
                                "yyyy-MM-dd"
                            ),
                            tanggal_panen = if (target.tanggal_panen != null) {
                                convertStringToDateLong(target.tanggal_panen, "yyyy-MM-dd")
                            } else {
                                0L
                            },
                            keramba_id = target.keramba_id.toInt()
                        )
                    }.toList()

                    if (biotaDAO.getBiotaCountFromKeramba(kerambaId) > listBiota.size) {
                        biotaDAO.deleteBiotaFromKeramba(kerambaId)
                    }
                    biotaDAO.insertAll(listBiota)
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

    suspend fun refreshBiotaHistory(kerambaId: Int) {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        withContext(Dispatchers.IO) {
            val response: Response<BiotaContainer> =
                monitoringService.getHistoryBiotaListAsync(token, userId, kerambaId)

            when (response.code()) {
                200 -> {
                    val listBiotaNetwork: List<BiotaNetwork> = response.body()!!.data

                    val listBiota = listBiotaNetwork.map { target ->
                        Biota(
                            biota_id = target.biota_id.toInt(),
                            jenis_biota = target.jenis_biota,
                            bobot = target.bobot.toDouble(),
                            panjang = target.panjang.toDouble(),
                            jumlah_bibit = target.jumlah_bibit.toInt(),
                            tanggal_tebar = convertStringToDateLong(
                                target.tanggal_tebar,
                                "yyyy-MM-dd"
                            ),
                            tanggal_panen = if (target.tanggal_panen != null) {
                                convertStringToDateLong(target.tanggal_panen, "yyyy-MM-dd")
                            } else {
                                0L
                            },
                            keramba_id = target.keramba_id.toInt()
                        )
                    }.toList()

                    biotaDAO.insertAll(listBiota)
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

    suspend fun addBiota(biota: BiotaDomain): BiotaContainer {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val biotaNetwork: BiotaNetwork = biotaNetworkMapper.mapToEntity(biota)

        val data = mutableMapOf<String, String>()

        data["jenis_biota"] = biotaNetwork.jenis_biota

        data["bobot"] = biotaNetwork.bobot

        data["panjang"] = biotaNetwork.panjang

        data["jumlah_bibit"] = biotaNetwork.jumlah_bibit

        data["tanggal_tebar"] = biotaNetwork.tanggal_tebar

        data["keramba_id"] = biotaNetwork.keramba_id

        data["user_id"] = userId.toString()

        val response: Response<BiotaContainer> =
            monitoringService.addBiotaAsync(token, data)

        when (response.code()) {
            201 -> return response.body()!!

            500 -> throw Exception("Internal Server Error")

            401 -> throw Exception("Unauthorized")

            400 -> {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                throw Exception(jsonObj.getString("message"))
            }
            else -> {
                throw Exception("HTTP Request Failed")
            }
        }
    }

    suspend fun updateBiota(biota: BiotaDomain): BiotaContainer {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val biotaNetwork: BiotaNetwork = biotaNetworkMapper.mapToEntity(biota)

        val data = mutableMapOf<String, String>()

        data["biota_id"] = biotaNetwork.biota_id

        data["jenis_biota"] = biotaNetwork.jenis_biota

        data["bobot"] = biotaNetwork.bobot

        data["panjang"] = biotaNetwork.panjang

        data["jumlah_bibit"] = biotaNetwork.jumlah_bibit

        data["tanggal_tebar"] = biotaNetwork.tanggal_tebar

        data["keramba_id"] = biotaNetwork.keramba_id

        data["user_id"] = userId.toString()

        val response: Response<BiotaContainer> =
            monitoringService.updateBiotaAsync(token, data)

        when (response.code()) {
            200 -> return response.body()!!

            500 -> throw Exception("Internal Server Error")

            401 -> throw Exception("Unauthorized")

            400 -> {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                throw Exception(jsonObj.getString("message"))
            }

            else -> throw Exception("HTTP Request Failed")
        }
    }

    suspend fun deleteBiota(biotaId: Int): BiotaContainer {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val data = mutableMapOf<String, String>()

        data["biota_id"] = biotaId.toString()

        data["user_id"] = userId.toString()

        val response: Response<BiotaContainer> =
            monitoringService.deleteBiotaAsync(token, data)

        when (response.code()) {
            200 -> return response.body()!!

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