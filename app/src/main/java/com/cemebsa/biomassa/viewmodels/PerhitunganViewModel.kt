package com.cemebsa.biomassa.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cemebsa.biomassa.models.domain.PengukuranDomain
import com.cemebsa.biomassa.models.domain.PerhitunganDomain
import com.cemebsa.biomassa.models.network.container.PengukuranContainer
import com.cemebsa.biomassa.models.network.container.PerhitunganContainer
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.repositories.PengukuranRepository
import com.cemebsa.biomassa.repositories.PerhitunganRepository
import com.cemebsa.biomassa.utils.convertStringToDateLong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class PerhitunganViewModel @Inject constructor(
    private val repository: PerhitunganRepository
) : ViewModel() {

    private val _init = MutableLiveData(true)
    val init: LiveData<Boolean> = _init

    private val _requestGetResult = MutableLiveData<NetworkResult>()
    val requestGetResult: LiveData<NetworkResult> = _requestGetResult

    private val _requestPostAddResult = MutableLiveData<NetworkResult>()
    val requestPostAddResult: LiveData<NetworkResult> = _requestPostAddResult

    private val _requestDeleteResult = MutableLiveData<NetworkResult>()
    val requestDeleteResult: LiveData<NetworkResult> = _requestDeleteResult

    fun doneToastException() {
        _requestGetResult.value = NetworkResult.Error("")
    }

    fun donePostAddRequest() {
        _requestPostAddResult.value = NetworkResult.Initial()
    }

    fun doneDeleteRequest() {
        _requestDeleteResult.value = NetworkResult.Initial()
    }

    private val _selectedBiotaId = MutableLiveData<Int>()

    fun getAllBiotaData(biota_id: Int): LiveData<List<PerhitunganDomain>> =
        repository.getAllBiotaData(biota_id)

    fun selectBiotaId(biota_id: Int) {
        _selectedBiotaId.value = biota_id
    }

    fun isEntryValid(hidup: String, mati: String, tanggal: String): Boolean {
        return !(hidup.isBlank() || mati.isBlank() || _selectedBiotaId.value == null || tanggal.isBlank())
    }

    fun insertPerhitungan(hidup: String, mati: String, tanggal: String) {
        _requestPostAddResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                val container: PerhitunganContainer = repository.addPerhitungan(
                    PerhitunganDomain(
                        perhitungan_id = 0,
                        hidup = hidup.toInt(),
                        mati = mati.toInt(),
                        tanggal_hitung = convertStringToDateLong(tanggal, "EEEE dd-MMM-yyyy"),
                        biota_id = _selectedBiotaId.value!!
                    )
                )

                _requestPostAddResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {
                _requestPostAddResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun deletePerhitungan(perhitunganId: Int) {
        _requestDeleteResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                val container: PerhitunganContainer = repository.deletePerhitungan(perhitunganId)

                _requestDeleteResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {
                _requestDeleteResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun fetchPerhitungan(biotaId: Int) {
        _requestGetResult.value = NetworkResult.Loading()
        viewModelScope.launch {
            try {
                repository.refreshPerhitungan(biotaId)

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

    fun deleteLocalPerhitungan(perhitunganId: Int) {
        viewModelScope.launch { repository.deleteLocalPerhitungan(perhitunganId) }
    }

    private fun doneInit() {
        _init.value = true
    }

    fun startInit(biotaId: Int) {
        fetchPerhitungan(biotaId)

        doneInit()
    }

    fun restartInit() {
        _init.value = false
    }
}