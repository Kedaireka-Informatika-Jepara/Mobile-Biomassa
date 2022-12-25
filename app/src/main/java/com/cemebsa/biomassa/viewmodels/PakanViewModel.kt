package com.cemebsa.biomassa.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cemebsa.biomassa.models.domain.PakanDomain
import com.cemebsa.biomassa.models.network.container.PakanContainer
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.repositories.PakanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class PakanViewModel @Inject constructor(
    private val repository: PakanRepository
) : ViewModel() {
    private val _init = MutableLiveData(true)
    val init: LiveData<Boolean> = _init

    private val _requestGetResult = MutableLiveData<NetworkResult>()
    val requestGetResult: LiveData<NetworkResult> = _requestGetResult

    private val _requestPostAddResult = MutableLiveData<NetworkResult>()
    val requestPostAddResult: LiveData<NetworkResult> = _requestPostAddResult

    private val _requestPutUpdateResult = MutableLiveData<NetworkResult>()
    val requestPutUpdateResult: LiveData<NetworkResult> = _requestPutUpdateResult

    private val _requestDeleteResult = MutableLiveData<NetworkResult>()
    val requestDeleteResult: LiveData<NetworkResult> = _requestDeleteResult

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

    fun loadPakanData(pakanId: Int): LiveData<PakanDomain> = repository.loadPakanData(pakanId)

    fun getAll(): LiveData<List<PakanDomain>> = repository.pakanList

    fun insertPakan(jenis_pakan: String, deskripsi: String = "") {
        _requestPostAddResult.value = NetworkResult.Loading()


        viewModelScope.launch {
            try {
                val container: PakanContainer = repository.addPakan(
                    PakanDomain(
                        pakan_id = 0,
                        jenis_pakan = jenis_pakan,
                        deskripsi = deskripsi
                    )
                )

                _requestPostAddResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {
                _requestPostAddResult.value = NetworkResult.Error(e.message.toString())
            }

        }
    }

    fun updatePakan(pakanId: Int, jenis_pakan: String, deskripsi: String = "") {
        _requestPutUpdateResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                val container: PakanContainer = repository.updatePakan(
                    PakanDomain(
                        pakan_id = pakanId,
                        jenis_pakan = jenis_pakan,
                        deskripsi = deskripsi
                    )
                )

                _requestPutUpdateResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {
                _requestPutUpdateResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun deletePakan(pakanId: Int) {
        _requestDeleteResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                val container: PakanContainer = repository.deletePakan(pakanId)

                _requestDeleteResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {
                _requestDeleteResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun updateLocalPakan(pakanId: Int, jenis_pakan: String, deskripsi: String = "") {
        viewModelScope.launch { repository.updateLocalPakan(pakanId, jenis_pakan, deskripsi)}
    }

    fun deleteLocalPakan(pakanId: Int) {
        viewModelScope.launch { repository.deleteLocalPakan(pakanId) }
    }

    fun isEntryValid(jenis_pakan: String): Boolean = jenis_pakan.isNotBlank()

    fun fetchPakan() {
        _requestGetResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                repository.refreshPakan()

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
        fetchPakan()

        doneInit()
    }

    fun restartInit() {
        _init.value = false
    }


    init {
        startInit()
    }
}