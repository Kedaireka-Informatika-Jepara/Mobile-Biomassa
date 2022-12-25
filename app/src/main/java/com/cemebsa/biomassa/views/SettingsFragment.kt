package com.cemebsa.biomassa.views

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.databinding.FragmentSettingsBinding
import com.cemebsa.biomassa.modules.BASE_URL
import com.cemebsa.biomassa.viewmodels.KerambaViewModel
import com.cemebsa.biomassa.viewmodels.PakanViewModel
import com.cemebsa.biomassa.viewmodels.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    private val settingViewModel by viewModels<SettingViewModel>()

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private val pakanViewModel by activityViewModels<PakanViewModel>()

    private val _fragmentTag = "Setting_Fragment"

    private lateinit var navController: NavController

    @Inject lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            settingsFragment = this@SettingsFragment
        }

        setupNavigation()

        setupNightModeSwitch()
    }

    private fun setupNightModeSwitch() {

        val isNightMode: Boolean = sharedPreferences.getBoolean("NIGHT_MODE", false)

        if (isNightMode) {
            binding.modeGelapSwitch.isChecked = true
        }

        binding.modeGelapSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                sharedPreferences.edit().putBoolean("NIGHT_MODE", true).apply()

            } else {

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                sharedPreferences.edit().putBoolean("NIGHT_MODE", false).apply()
            }
        }
    }

    fun launchUri(view: View) {
        when (view.id) {
            R.id.about_tv -> {}

            R.id.web_tv -> {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(BASE_URL)
                )
                startActivity(intent)
            }

            R.id.sosmed_tv -> {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/cemebsakedaireka/")
                )
                startActivity(intent)
            }
            R.id.logout_button -> {
                settingViewModel.logOut()

                kerambaViewModel.restartInit()

                pakanViewModel.restartInit()

                val host = NavHostFragment.create(R.navigation.nav_graph)

                val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()

                fragmentTransaction?.replace(R.id.nav_host_fragment, host)

                fragmentTransaction?.setPrimaryNavigationFragment(host)

                fragmentTransaction?.commit()
            }
            else -> {}
        }
    }

    private fun setupNavigation() {
        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.homeFragment, R.id.settingsFragment))

        binding.toolbarFragment.setupWithNavController(navController, appBarConfiguration)

        binding.toolbarFragment.setNavigationOnClickListener {
            navController.navigateUp(appBarConfiguration)
        }
    }
}