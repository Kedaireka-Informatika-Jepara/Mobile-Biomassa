package com.cemebsa.biomassa.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cemebsa.biomassa.abstractions.view.IOnItemClickListener
import com.cemebsa.biomassa.adapters.KerambaListAdapter
import com.cemebsa.biomassa.databinding.FragmentKerambaBinding
import com.cemebsa.biomassa.models.domain.KerambaDomain
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.viewmodels.BiotaViewModel
import com.cemebsa.biomassa.viewmodels.FeedingViewModel
import com.cemebsa.biomassa.viewmodels.KerambaViewModel
import com.cemebsa.biomassa.viewmodels.PanenViewModel
import com.cemebsa.biomassa.views.action.BottomSheetActionKeramba
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KerambaFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentKerambaBinding

    private lateinit var navController: NavController

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private val feedingViewModel by activityViewModels<FeedingViewModel>()

    private val panenViewModel by activityViewModels<PanenViewModel>()

    private lateinit var kerambaListAdapter: KerambaListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentKerambaBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this.viewLifecycleOwner

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupKerambaList()

        setupQuerySearch()
    }

    private fun setupKerambaList() {
        kerambaViewModel.init.observe(viewLifecycleOwner, {
            if (it == false) {
                kerambaViewModel.startInit()
            }
        })

        kerambaViewModel.getAllKeramba().observe(viewLifecycleOwner, {
            kerambaListAdapter = KerambaListAdapter()

            kerambaListAdapter.clickListener = object : IOnItemClickListener<KerambaDomain> {
                override fun onCLick() {}

                override fun onClick(obj: KerambaDomain) // clickListener
                {
                    navController.navigate(
                        HomeFragmentDirections.actionHomeFragmentToSummaryFragment(
                            obj.keramba_id
                        )
                    )

                    biotaViewModel.restartInit()

                    feedingViewModel.restartInit()

                    panenViewModel.restartInit()
                }

                override fun onLongClick(obj: KerambaDomain): Boolean
                // longClickListener
                {
                    if (childFragmentManager.findFragmentByTag("BottomSheetAdd") == null && childFragmentManager.findFragmentByTag(
                            "BottomSheetActionKeramba"
                        ) == null
                    ) {
                        val bottomSheetAction =
                            BottomSheetActionKeramba.newInstance(kerambaId = obj.keramba_id)

                        bottomSheetAction.show(childFragmentManager, "BottomSheetActionKeramba")
                    }
                    return true
                }
            }

            binding.kerambaList.adapter = kerambaListAdapter

            it.let {
                kerambaListAdapter.setData(it)
            }

            val pendingQuery = kerambaViewModel.querySearch.value

            if (pendingQuery != null) {
                kerambaListAdapter.filter.filter(pendingQuery)
            }
        })

        kerambaViewModel.requestGetResult.observe(viewLifecycleOwner, { result ->
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

                    binding.kerambaList.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        kerambaViewModel.doneToastException()
                    }

                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.kerambaList.visibility = View.VISIBLE
                }
            }
        })

        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun setupQuerySearch() {
        kerambaViewModel.querySearch.observe(viewLifecycleOwner, { query ->
            kerambaListAdapter.filter.filter(query)
        })
    }

    override fun onRefresh() {
        kerambaViewModel.fetchKeramba()
    }
}