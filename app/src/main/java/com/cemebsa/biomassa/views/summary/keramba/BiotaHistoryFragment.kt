package com.cemebsa.biomassa.views.summary.keramba

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.adapters.BiotaHistoryListAdapter
import com.cemebsa.biomassa.databinding.FragmentBiotaHistoryBinding
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.viewmodels.BiotaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BiotaHistoryFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private lateinit var binding: FragmentBiotaHistoryBinding

    private lateinit var navController: NavController

    private val navArgs by navArgs<BiotaHistoryFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBiotaHistoryBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        setupNavigation()

        setupObserver()

        setupBiotaHistoryList()

        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun setupObserver() {
        biotaViewModel.init.observe(viewLifecycleOwner, {
            if (it == false){
                biotaViewModel.startInit(navArgs.kerambaId)
            }
        })

        biotaViewModel.requestGetHistoryResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                }
                is NetworkResult.Loading -> {
                    if (!binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = true
                    }
                }
                is NetworkResult.Loaded -> {
                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.biotaHistoryList.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        biotaViewModel.doneToastException()
                    }

                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.biotaHistoryList.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setupBiotaHistoryList() {
        val biotaHistoryListAdapter = BiotaHistoryListAdapter()

        binding.biotaHistoryList.adapter = biotaHistoryListAdapter

        biotaViewModel.getAllBiotaHistory(navArgs.kerambaId).observe(viewLifecycleOwner, { list ->
            biotaHistoryListAdapter.submitList(list)
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

    override fun onRefresh() {
        navArgs.kerambaId.let {
            biotaViewModel.fetchBiotaHistory(navArgs.kerambaId)
        }
    }
}