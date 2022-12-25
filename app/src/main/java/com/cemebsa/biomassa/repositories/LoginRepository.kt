package com.cemebsa.biomassa.repositories

import android.content.SharedPreferences
import com.cemebsa.biomassa.models.network.LoggedInUser
import com.cemebsa.biomassa.models.network.Result
import com.cemebsa.biomassa.models.network.container.LoginContainer
import com.cemebsa.biomassa.abstractions.service.MonitoringService
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
@Singleton
class LoginRepository @Inject constructor(
    private val monitoringService: MonitoringService,
    private val sharedPreferences: SharedPreferences
) {
    suspend fun loginUser(username: String, password: String): Result<LoggedInUser> {
        val response: Response<LoginContainer> =
            monitoringService.loginUserAsync(username, password)

        return when (response.code()) {
            200 -> {
                val loggedInUser: LoggedInUser = response.body()!!.data[0]

                setLoggedInUser(loggedInUser)

                Result.Success(loggedInUser)
            }

            500 -> Result.Error(Exception("Internal Server Error"))

            400 -> {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                throw Exception(jsonObj.getString("message"))
            }

            else -> Result.Error(Exception("HTTP Request Failed"))
        }
    }

    fun isUserLoggedIn(): Result<LoggedInUser> {
        val token = sharedPreferences.getString("token", "")
        val userId = sharedPreferences.getString("user_id", "")
        val username = sharedPreferences.getString("username", "")

        return if (!(token.isNullOrBlank() || userId.isNullOrBlank() || username.isNullOrBlank())) {
            Result.Success(
                LoggedInUser(
                    token, username, userId
                )
            )
        } else {
            Result.Error(Exception(""))
        }
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        sharedPreferences.edit()

            .putString("token", loggedInUser.token)

            .putString("user_id", loggedInUser.user_id)

            .putString("username", loggedInUser.username)

            .apply()
        // @see https://developer.android.com/training/articles/keystore
    }

    fun logOutUser() {
        sharedPreferences.edit()
            .remove("token")

            .remove("user_id")

            .remove("username")

            .apply()
    }
}