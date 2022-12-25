package com.cemebsa.biomassa.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cemebsa.biomassa.models.domain.BiotaDomain
import com.cemebsa.biomassa.models.network.container.BiotaContainer
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.repositories.BiotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class BiotaViewModel @Inject constructor(
    private val repository: BiotaRepository
) : ViewModel() {

    private val _init = MutableLiveData(true)
    val init: LiveData<Boolean> = _init

    private val _selectedKerambaId = MutableLiveData<Int>()
    val selectedKerambaId: LiveData<Int> = _selectedKerambaId

    private val _requestGetResult = MutableLiveData<NetworkResult>()
    val requestGetResult: LiveData<NetworkResult> = _requestGetResult

    private val _requestGetHistoryResult = MutableLiveData<NetworkResult>()
    val requestGetHistoryResult: LiveData<NetworkResult> = _requestGetHistoryResult

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

    fun loadBiotaData(id: Int): LiveData<BiotaDomain> = repository.loadBiotaData(id)

    fun getAllBiota(id: Int): LiveData<List<BiotaDomain>> = repository.getAllBiota(id)

    fun getAllBiotaHistory(id: Int): LiveData<List<BiotaDomain>> = repository.getAllBiotaHistory(id)

    fun selectkerambaId(id: Int) {
        _selectedKerambaId.value = id
    }

    fun isEntryValid(
        jenis: String,
        bobot: String,
        panjang: String,
        jumlah: String,
        tanggal: Long
    ): Boolean {
        return !(jenis.isBlank() || bobot.isBlank() || panjang.isBlank() || jumlah.isBlank() || tanggal == 0L || _selectedKerambaId.value == null)
    }

    fun insertBiota(jenis: String, bobot: String, panjang: String, jumlah: String, tanggal: Long) {
        viewModelScope.launch {
            _requestPostAddResult.value = NetworkResult.Loading()

            try {
                val container: BiotaContainer = repository.addBiota(
                    BiotaDomain(
                        biota_id = 0,
                        keramba_id = _selectedKerambaId.value!!,
                        jenis_biota = jenis,
                        bobot = bobot.toDouble(),
                        panjang = panjang.toDouble(),
                        jumlah_bibit = jumlah.toInt(),
                        tanggal_tebar = tanggal,
                        tanggal_panen = 0L
                    )
                )

                _requestPostAddResult.value = NetworkResult.Loaded(container.message)

            } catch (e: Exception) {
                _requestPostAddResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun updateBiota(
        biotaId: Int,
        jenis: String,
        bobot: String,
        panjang: String,
        jumlah: String,
        tanggal: Long
    ) {
        _requestPutUpdateResult.value = NetworkResult.Loading()
        viewModelScope.launch {
            try {
                val container: BiotaContainer = repository.updateBiota(
                    BiotaDomain(
                        biota_id = biotaId,
                        jenis_biota = jenis,
                        bobot = bobot.toDouble(),
                        panjang = panjang.toDouble(),
                        jumlah_bibit = jumlah.toInt(),
                        tanggal_tebar = tanggal,
                        keramba_id = _selectedKerambaId.value!!,
                        tanggal_panen = 0L
                    )
                )

                _requestPutUpdateResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {
                _requestPutUpdateResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun updateLocalBiota(
        biotaId: Int,
        jenis: String,
        bobot: String,
        panjang: String,
        jumlah: String,
        tanggal: Long
    ) {
        viewModelScope.launch {
            repository.updateLocalBiota(
                biotaId,
                jenis,
                bobot,
                panjang,
                jumlah,
                selectedKerambaId.value!!,
                tanggal
            )
        }
    }

    fun deleteBiota(biotaId: Int) {
        _requestDeleteResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                val container: BiotaContainer = repository.deleteBiota(biotaId)

                _requestDeleteResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {
                _requestPutUpdateResult.value = NetworkResult.Error(e.message.toString())
            }
        }

    }

    fun deleteLocalBiota(biotaId: Int) {
        viewModelScope.launch { repository.deleteLocalBiota(biotaId) }
    }

    fun fetchBiota(kerambaId: Int) {
        _requestGetResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                repository.refreshBiota(kerambaId)

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

    fun fetchBiotaHistory(kerambaId: Int) {
        _requestGetHistoryResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                repository.refreshBiotaHistory(kerambaId)

                _requestGetHistoryResult.value = NetworkResult.Loaded()
            } catch (e: Exception) {
                if (e !is UnknownHostException) {
                    _requestGetHistoryResult.value = NetworkResult.Error(e.message.toString())
                } else {
                    _requestGetHistoryResult.value = NetworkResult.Error("")
                }
            }
        }
    }

    private fun doneInit() {
        _init.value = true
    }

    fun startInit(kerambaId: Int) {
        fetchBiota(kerambaId)

        fetchBiotaHistory(kerambaId)

        doneInit()
    }

    fun restartInit() {
        _init.value = false
    }
}