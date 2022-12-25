package com.cemebsa.biomassa.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cemebsa.biomassa.models.domain.PanenDomain
import com.cemebsa.biomassa.models.network.container.PanenContainer
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.models.relation.PanenAndBiota
import com.cemebsa.biomassa.repositories.PanenRepository
import com.cemebsa.biomassa.utils.convertStringToDateLong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class PanenViewModel @Inject constructor(
    private val repository: PanenRepository
) : ViewModel() {

    private val _init = MutableLiveData(true)
    val init: LiveData<Boolean> = _init

    private val _requestGetResult = MutableLiveData<NetworkResult>()
    val requestGetResult: LiveData<NetworkResult> = _requestGetResult

    private val _requestPostAddResult = MutableLiveData<NetworkResult>()
    val requestPostAddResult: LiveData<NetworkResult> = _requestPostAddResult

    fun doneToastException() {
        _requestGetResult.value = NetworkResult.Error("")
    }

    fun donePostAddRequest() {
        _requestPostAddResult.value = NetworkResult.Initial()
    }

    fun getlistPanen(kerambaId: Int): LiveData<List<PanenAndBiota>> = repository.getlistPanen(kerambaId)

    private val _inputKerambaId = MutableLiveData<Int>()
    val inputKerambaId: LiveData<Int> = _inputKerambaId

    private val _inputBiotaId = MutableLiveData<Int>()

    fun selectKeramba(id: Int) {
        _inputKerambaId.value = id
    }

    fun selectBiota(id: Int) {
        _inputBiotaId.value = id
    }

    fun fetchPanen(kerambaId: Int) {
        _requestGetResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                repository.refreshPanen(kerambaId)

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

    fun insertPanen(
        panjang: String,
        bobot: String,
        jumlahHidup: String,
        jumlahMati: String,
        tanggal: String
    ) {
        _requestPostAddResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                val container: PanenContainer = repository.addPanen(
                    PanenDomain(
                        activity_id = 0,
                        keramba_id = _inputKerambaId.value!!,
                        biota_id = _inputBiotaId.value!!,
                        panjang = panjang.toDouble(),
                        bobot = bobot.toDouble(),
                        jumlah_hidup = jumlahHidup.toInt(),
                        jumlah_mati = jumlahMati.toInt(),
                        //mortality_rate = jumlahHidup.toDouble() / jumlahMati.toDouble(),
                        tanggal_panen = convertStringToDateLong(tanggal, "EEEE dd-MMM-yyyy")
                    )
                )
                _requestPostAddResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {
                _requestPostAddResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun isEntryValid(
        panjang: String, bobot: String, jumlahHidup: String, jumlahMati: String, tanggal: String
    ): Boolean {
        return !(_inputBiotaId.value == null || _inputKerambaId.value == null || panjang.isBlank() || bobot.isBlank() || jumlahHidup.isBlank() || jumlahMati.isBlank() || tanggal.isBlank())
    }

    fun downloadExportedData(kerambaId: Int, name: String){
        repository.downloadExportedData(kerambaId, name)
    }

    private fun doneInit() {
        _init.value = true
    }

    fun startInit(kerambaId: Int) {
        fetchPanen(kerambaId)

        doneInit()
    }

    fun restartInit() {
        _init.value = false
    }
}