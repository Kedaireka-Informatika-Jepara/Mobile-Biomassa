package com.cemebsa.biomassa.views.summary.biota

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.adapters.BiotaFragmentTabAdapter
import com.cemebsa.biomassa.databinding.FragmentBiotaTabBinding
import com.cemebsa.biomassa.viewmodels.BiotaViewModel
import com.cemebsa.biomassa.viewmodels.PengukuranViewModel
import com.cemebsa.biomassa.viewmodels.PerhitunganViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BiotaTabFragment : Fragment() {
    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private val pengukuranViewModel by activityViewModels<PengukuranViewModel>()

    private val perhitunganViewModel by activityViewModels<PerhitunganViewModel>()

    private val navArgs by navArgs<BiotaTabFragmentArgs>()

    private lateinit var binding: FragmentBiotaTabBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBiotaTabBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()

        setupTabLayout()

        setupObserver()
    }

    private fun setupObserver() {
        pengukuranViewModel.init.observe(viewLifecycleOwner, {
            if (it == false) {
                pengukuranViewModel.startInit(navArgs.biotaId)
            }
        })
        perhitunganViewModel.init.observe(viewLifecycleOwner, {
            if (it == false) {
                perhitunganViewModel.startInit(navArgs.biotaId)
            }
        })
    }

    private fun setupTabLayout() {
        val adapter =
            BiotaFragmentTabAdapter(this, biotaId = navArgs.biotaId, kerambaId = navArgs.kerambaId)

        with(binding) {
            pager.adapter = adapter

            TabLayoutMediator(tabLayout, pager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.info)
                    1 -> tab.text = getString(R.string.data)
                    2 -> tab.text = "Hitung"
                }
            }.attach()
        }
    }

    private fun setupNavigation() {
        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.homeFragment, R.id.settingsFragment))

        binding.toolbarFragment.setupWithNavController(navController, appBarConfiguration)

        binding.toolbarFragment.setNavigationOnClickListener {
            navController.navigateUp(appBarConfiguration)
        }

        navArgs.biotaId.let {
            biotaViewModel.loadBiotaData(navArgs.biotaId).observe(viewLifecycleOwner, { biota ->
                binding.toolbarFragment.title = biota.jenis_biota
            })
        }
    }
}