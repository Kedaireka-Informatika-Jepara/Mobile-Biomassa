package com.cemebsa.biomassa.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cemebsa.biomassa.models.network.LoggedInUserView
import com.cemebsa.biomassa.models.network.LoginResult
import com.cemebsa.biomassa.models.network.Result
import com.cemebsa.biomassa.repositories.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        viewModelScope.launch {
            try {
                val result = loginRepository.loginUser(username, password)

                if (result is Result.Success) {
                    _loginResult.value =
                        LoginResult(success = LoggedInUserView(displayName = result.data.username))
                } else {
                    _loginResult.value = LoginResult(error = "Invalid username or password")
                }
            } catch (e: Exception){
                _loginResult.value = LoginResult(error = e.message.toString())
            }
        }
//        _loginResult.value =
//            LoginResult(success = LoggedInUserView(displayName = "Robertus Agung"))
    }

    init {
        val checkLoggedIn = loginRepository.isUserLoggedIn()

        if (checkLoggedIn is Result.Success) {
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = checkLoggedIn.data.username))
        }
    }
}