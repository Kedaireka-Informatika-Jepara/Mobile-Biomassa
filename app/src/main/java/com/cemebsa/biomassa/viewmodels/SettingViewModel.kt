package com.cemebsa.biomassa.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cemebsa.biomassa.repositories.KerambaRepository
import com.cemebsa.biomassa.repositories.LoginRepository
import com.cemebsa.biomassa.repositories.PakanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val kerambaRepository: KerambaRepository,
    private val pakanRepository: PakanRepository,
    private val loginRepository: LoginRepository,
): ViewModel() {
    fun logOut(){
        loginRepository.logOutUser()

        viewModelScope.launch {
            kerambaRepository.deleteAllLocalKeramba()
        }

        viewModelScope.launch {
            pakanRepository.deleteAllLocalPakan()
        }
    }
}