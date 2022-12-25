package com.cemebsa.biomassa.repositories

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.cemebsa.biomassa.abstractions.dao.PakanDAO
import com.cemebsa.biomassa.models.domain.PakanDomain
import com.cemebsa.biomassa.models.entity.Pakan
import com.cemebsa.biomassa.models.network.PakanNetwork
import com.cemebsa.biomassa.models.network.container.PakanContainer
import com.cemebsa.biomassa.abstractions.service.MonitoringService
import com.cemebsa.biomassa.abstractions.mapper.IEntityMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PakanRepository @Inject constructor(
    private val pakanDAO: PakanDAO,
    private val sharedPreferences: SharedPreferences,
    private val monitoringService: MonitoringService,
    private val pakanMapper: IEntityMapper<Pakan, PakanDomain>,
    private val pakanNetworkMapper: IEntityMapper<PakanNetwork, PakanDomain>
) {
    val pakanList: LiveData<List<PakanDomain>> = Transformations.map(pakanDAO.getAll().asLiveData()){ list->
        list.map { pakanMapper.mapFromEntity(it) }}

    fun loadPakanData(pakanId: Int): LiveData<PakanDomain> = Transformations.map(
        pakanDAO.getById(pakanId).asLiveData()
    ) { pakanMapper.mapFromEntity(it) }

    suspend fun updateLocalPakan(pakanId: Int, jenis_pakan: String, deskripsi: String){
        withContext(Dispatchers.IO) {
            pakanDAO.updateOne(
                Pakan(
                    pakan_id = pakanId,
                    jenis_pakan = jenis_pakan,
                    deskripsi = deskripsi
                )
            )
        }
    }

    suspend fun deleteLocalPakan(pakanId: Int){
        withContext(Dispatchers.IO) {
            pakanDAO.deleteOne(pakanId)
        }
    }

    suspend fun deleteAllLocalPakan(){
        withContext(Dispatchers.IO) {
            pakanDAO.deleteAllPakan()
        }
    }

    suspend fun refreshPakan() {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        withContext(Dispatchers.IO) {
            val response: Response<PakanContainer> =
                monitoringService.getPakanListAsync(token, userId)

            when (response.code()){
                200 -> {
                    val listPakanNetwork: List<PakanNetwork> = response.body()!!.data

                    val listPakan = listPakanNetwork.map { target ->
                        Pakan(
                            pakan_id = target.pakan_id.toInt(),
                            jenis_pakan = target.jenis_pakan,
                            deskripsi = target.deskripsi
                        )
                    }.toList()

                    if (pakanDAO.getPakanCount() > listPakan.size) {
                        pakanDAO.deleteAllPakan()
                    }

                    pakanDAO.insertAll(listPakan)
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

    suspend fun addPakan(pakan: PakanDomain): PakanContainer {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val pakanNetwork: PakanNetwork = pakanNetworkMapper.mapToEntity(pakan)

        val data = mutableMapOf<String, String>()

        data["jenis_pakan"] = pakanNetwork.jenis_pakan

        data["deskripsi"] = pakanNetwork.deskripsi

        data["user_id"] = userId.toString()

        val response: Response<PakanContainer> =
            monitoringService.addPakanAsync(token, data)

        when (response.code()){
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

    suspend fun updatePakan(pakan: PakanDomain): PakanContainer {
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val pakanNetwork: PakanNetwork = pakanNetworkMapper.mapToEntity(pakan)

        val data = mutableMapOf<String, String>()

        data["pakan_id"] = pakanNetwork.pakan_id

        data["jenis_pakan"] = pakanNetwork.jenis_pakan

        data["deskripsi"] = pakanNetwork.deskripsi

        data["user_id"] = userId.toString()

        val response: Response<PakanContainer> =
            monitoringService.updatePakanAsync(token, data)

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

    suspend fun deletePakan(pakanId: Int): PakanContainer{
        val userId = sharedPreferences.getString("user_id", null)?.toInt() ?: 0

        val token: String = sharedPreferences.getString("token", null) ?: ""

        val data = mutableMapOf<String, String>()

        data["user_id"] = userId.toString()

        data["pakan_id"] = pakanId.toString()

        val response: Response<PakanContainer> =
            monitoringService.deletePakanAsync(token, data)

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