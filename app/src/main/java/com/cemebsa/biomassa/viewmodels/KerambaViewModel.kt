package com.cemebsa.biomassa.viewmodels

import androidx.lifecycle.*
import com.cemebsa.biomassa.models.domain.BiotaDomain
import com.cemebsa.biomassa.models.domain.KerambaDomain
import com.cemebsa.biomassa.models.entity.Biota
import com.cemebsa.biomassa.models.entity.Keramba
import com.cemebsa.biomassa.models.network.container.KerambaContainer
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.repositories.KerambaRepository
import com.cemebsa.biomassa.abstractions.mapper.IEntityMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class KerambaViewModel @Inject constructor(
    private val kerambaMapper: IEntityMapper<Keramba, KerambaDomain>,
    private val biotaMapper: IEntityMapper<Biota, BiotaDomain>,
    private val repository: KerambaRepository
) : ViewModel() {
    private val _init = MutableLiveData(true)
    val init: LiveData<Boolean> = _init

    private val _querySearch = MutableLiveData<String>()
    val querySearch: LiveData<String> = _querySearch

    private val _requestGetResult = MutableLiveData<NetworkResult>()
    val requestGetResult: LiveData<NetworkResult> = _requestGetResult

    private val _requestPostAddResult = MutableLiveData<NetworkResult>()
    val requestPostAddResult: LiveData<NetworkResult> = _requestPostAddResult

    private val _requestPutUpdateResult = MutableLiveData<NetworkResult>()
    val requestPutUpdateResult: LiveData<NetworkResult> = _requestPutUpdateResult

    private val _requestDeleteResult = MutableLiveData<NetworkResult>()
    val requestDeleteResult: LiveData<NetworkResult> = _requestDeleteResult

    fun getAllKeramba(): LiveData<List<KerambaDomain>> = repository.kerambaList

    fun setQuerySearch(query: String) {
        _querySearch.value = query
    }

    fun doneToastException() {
        _requestGetResult.value = NetworkResult.Error("")
    }

    fun donePostAddRequest() {
        _requestPostAddResult.value = NetworkResult.Initial()
    }

    fun donePutUpdateRequest() {
        _requestPutUpdateResult.value = NetworkResult.Initial()
    }

    fun doneDeleteRequest() {
        _requestDeleteResult.value = NetworkResult.Initial()
    }

    fun loadKerambaData(id: Int): LiveData<KerambaDomain> {
        return repository.getKerambaById(id)
    }

    fun isEntryValid(jenis: String, ukuran: String, tanggal: Long): Boolean {
        return !(jenis.isBlank() || ukuran.isBlank() || tanggal == 0L)
    }

    fun insertKeramba(nama: String, ukuran: String, tanggal: Long) {
        _requestPostAddResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                val container: KerambaContainer = repository.addKeramba(
                    KerambaDomain(
                        keramba_id = 0,
                        nama_keramba = nama,
                        ukuran = ukuran.toDouble(),
                        tanggal_install = tanggal
                    )
                )

                _requestPostAddResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {
                _requestPostAddResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun deleteKeramba(kerambaId: Int) {
        _requestDeleteResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                val container: KerambaContainer = repository.deleteKeramba(kerambaId)

                _requestDeleteResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {
                _requestDeleteResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun updateKeramba(id: Int, nama: String, ukuran: String, tanggal: Long) {
        _requestPutUpdateResult.value = NetworkResult.Loading()

        viewModelScope.launch {

            try {
                val container: KerambaContainer = repository.updateKeramba(
                    KerambaDomain(
                        keramba_id = id,
                        nama_keramba = nama,
                        ukuran = ukuran.toDouble(),
                        tanggal_install = tanggal
                    )
                )

                _requestPutUpdateResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {
                _requestPutUpdateResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun updateLocalKeramba(id: Int, nama: String, ukuran: String, tanggal: Long) {
        viewModelScope.launch { repository.updateLocalKeramba(id, nama, ukuran, tanggal) }
    }

    fun deleteLocalKeramba(kerambaId: Int) {
        viewModelScope.launch { repository.deleteLocalKeramba(kerambaId) }
    }

    fun loadKerambaAndBiota(): LiveData<Map<KerambaDomain, List<BiotaDomain>>> {
        return Transformations.map(
            repository.kerambaAndBiotaList
        ) { listKerambaAndBiota ->
            listKerambaAndBiota.map {
                kerambaMapper.mapFromEntity(it.keramba) to it.biotaList.map { biota ->
                    biotaMapper.mapFromEntity(
                        biota
                    )
                }.filter { biotaDomain ->
                    biotaDomain.tanggal_panen == 0L
                }
            }.toMap()
        }
    }

    fun fetchKeramba() {
        _requestGetResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                repository.refreshKeramba()

                _requestGetResult.value = NetworkResult.Loaded()

            } catch (e: Exception) {
                if (e !is UnknownHostException) {
                    _requestGetResult.value = NetworkResult.Error(e.message.toString())
                } else {
                    _requestGetResult.value = NetworkResult.Error("")
                }
            }
        }
    }

    private fun doneInit() {
        _init.value = true
    }

    fun startInit() {
        fetchKeramba()

        doneInit()
    }

    fun restartInit() {
        _init.value = false
    }

    init {
        startInit()
    }
}