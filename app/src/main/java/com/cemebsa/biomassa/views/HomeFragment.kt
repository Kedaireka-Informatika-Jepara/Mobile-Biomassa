package com.cemebsa.biomassa.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.adapters.HomeFragmentTabAdapter
import com.cemebsa.biomassa.databinding.FragmentHomeBinding
import com.cemebsa.biomassa.viewmodels.KerambaViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var navController: NavController

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this.viewLifecycleOwner

        navController = findNavController()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()

        setupTabLayout()

        setupToolbarSearch()
    }

    private fun setupTabLayout() {
        val tabAdapter = HomeFragmentTabAdapter(this)

        with(binding) {
            pager.adapter = tabAdapter

            TabLayoutMediator(tabLayout, pager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.keramba)
                    1 -> tab.text = getString(R.string.jenis_pakan)
                }
            }.attach()
        }
    }

    private fun setupToolbarSearch() {
        binding.toolbarFragment.inflateMenu(R.menu.search_menu)

        val search = binding.toolbarFragment.menu.findItem(R.id.appSearchBar)

        val searchView = search.actionView as SearchView

        searchView.queryHint = "Nama Keramba"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                kerambaViewModel.setQuerySearch(newText)
                return true
            }
        })

        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> search.isVisible = true
                    1 -> search.isVisible = false
                }
            }
        })
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