package com.cemebsa.biomassa.views.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.databinding.FragmentLoginBinding
import com.cemebsa.biomassa.models.network.LoggedInUserView
import com.cemebsa.biomassa.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val loginViewModel by viewModels<LoginViewModel>()

    private lateinit var binding: FragmentLoginBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        this.binding = FragmentLoginBinding.inflate(inflater, container, false)

        navController = findNavController()

        return this.binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        binding.loginFragment = this@LoginFragment

        setupObserver()

        binding.password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                startLogin()
            }
            false
        }
    }

    fun startLogin() {
        val loginButton = this.binding.login

        val loadingProgressBar = this.binding.loading

        val usernameEditText = this.binding.username

        val passwordEditText = this.binding.password

        loginButton.isEnabled = false

        loadingProgressBar.visibility = View.VISIBLE

        loginViewModel.login(
            usernameEditText.text.toString(),
            passwordEditText.text.toString()
        )

        hideKeyBoard()
    }

    private fun setupObserver() {
        val loginButton = this.binding.login
        val loadingProgressBar = this.binding.loading

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loginResult.error?.let {
                    showLoginFailed(it)

                    loadingProgressBar.visibility = View.GONE

                    if (!loginButton.isEnabled) {
                        loginButton.isEnabled = true
                    }
                }
                loginResult.success?.let {
                    updateUiWithUser(it)

                    navController.navigate(LoginFragmentDirections.actionLoginFragmentToNavBarFragment())
                }
            })
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + ' ' + model.displayName
        Toast.makeText(requireContext(), welcome, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(string: String) {
        Toast.makeText(requireContext(), string, Toast.LENGTH_LONG).show()
    }

    private fun hideKeyBoard() {
        if (activity!!.currentFocus == null) {
            return
        }
        val inputMethodManager =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
    }
}