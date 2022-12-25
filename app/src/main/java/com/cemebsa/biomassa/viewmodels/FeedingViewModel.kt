package com.cemebsa.biomassa.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cemebsa.biomassa.models.domain.FeedingDomain
import com.cemebsa.biomassa.models.network.container.FeedingContainer
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.repositories.FeedingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class FeedingViewModel @Inject constructor(
    private val repository: FeedingRepository
) : ViewModel() {
    private val _init = MutableLiveData(true)
    val init: LiveData<Boolean> = _init

    private val _loadedFeedingId = MutableLiveData<Int>()

    private val _selectedKerambaId = MutableLiveData<Int>()
    val selectedKerambaId: LiveData<Int> = _selectedKerambaId

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

    fun loadFeedingData(id: Int): LiveData<FeedingDomain> = repository.loadFeedingDataByFeedingId(id)

    fun setFeedingId(id: Int) {
        _loadedFeedingId.value = id
    }

    fun getAllFeeding(id: Int): LiveData<List<FeedingDomain>> = repository.getAllFeeding(id)

    fun selectKerambaId(id: Int) {
        _selectedKerambaId.value = id
    }

    fun isEntryValid(
        tanggal: Long
    ): Boolean {
        return !(_selectedKerambaId.value == null || tanggal == 0L)
    }

    fun insertFeeding(tanggal: Long) {
        viewModelScope.launch {
            _requestPostAddResult.value = NetworkResult.Loading()

            try {
                val container: FeedingContainer = repository.addFeeding(
                    FeedingDomain(
                        feeding_id = 0,
                        keramba_id = _selectedKerambaId.value!!,
                        tanggal_feeding = tanggal
                    )
                )

                _requestPostAddResult.value = NetworkResult.Loaded(container.message)

            } catch (e: Exception) {
                _requestPostAddResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun updateFeeding(
        feedingId: Int,
        tanggal: Long
    ) {
        _requestPutUpdateResult.value = NetworkResult.Loading()
        viewModelScope.launch {
            try {
                val container: FeedingContainer = repository.updateFeeding(
                    FeedingDomain(
                        feeding_id = feedingId,
                        keramba_id = _selectedKerambaId.value!!,
                        tanggal_feeding = tanggal
                    )
                )

                _requestPutUpdateResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {
                _requestPutUpdateResult.value = NetworkResult.Error(e.message.toString())
            }
        }
    }

    fun updateLocalFeeding(
        feedingId: Int,
        tanggal: Long
    ) {
        viewModelScope.launch {
            repository.updateLocalFeeding(
                feedingId,
                selectedKerambaId.value!!,
                tanggal
            )
        }
    }

    fun deleteFeeding(feedingId: Int) {
        _requestDeleteResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                val container: FeedingContainer = repository.deleteFeeding(feedingId)

                _requestDeleteResult.value = NetworkResult.Loaded(container.message)
            } catch (e: Exception) {
                _requestPutUpdateResult.value = NetworkResult.Error(e.message.toString())
            }
        }

    }

    fun deleteLocalFeeding(feedingId: Int) {
        viewModelScope.launch { repository.deleteLocalFeeding(feedingId) }
    }

    fun fetchFeeding(kerambaId: Int) {
        _requestGetResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            try {
                repository.refreshFeeding(kerambaId)

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

    fun startInit(kerambaId: Int) {
        fetchFeeding(kerambaId)

        doneInit()
    }

    fun restartInit() {
        _init.value = false
    }
}