package com.cemebsa.biomassa.views.summary.pakan

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
import androidx.recyclerview.widget.ConcatAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.abstractions.view.IOnItemClickListener
import com.cemebsa.biomassa.adapters.FeedingDetailListAdapter
import com.cemebsa.biomassa.adapters.HeaderButtonAdapter
import com.cemebsa.biomassa.adapters.HeaderCardAdapter
import com.cemebsa.biomassa.databinding.FragmentFeedingDetailBinding
import com.cemebsa.biomassa.models.domain.FeedingDetailDomain
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.viewmodels.FeedingDetailViewModel
import com.cemebsa.biomassa.viewmodels.PakanViewModel
import com.cemebsa.biomassa.views.action.BottomSheetActionFeedingDetail
import com.cemebsa.biomassa.views.add.BottomSheetFeeding
import com.cemebsa.biomassa.views.add.BottomSheetFeedingDetail
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedingDetailFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentFeedingDetailBinding

    private lateinit var navController: NavController

    private val args by navArgs<FeedingDetailFragmentArgs>()

    private val feedingDetailViewModel by activityViewModels<FeedingDetailViewModel>()

    private val pakanViewModel by activityViewModels<PakanViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for kerambaIdthis fragment
        binding = FragmentFeedingDetailBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()

        setupFeedingDetailList()

        setupObserver()

        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun setupFeedingDetailList() {
        val feedingDetailHeaderAdapter =
            HeaderButtonAdapter()

        feedingDetailHeaderAdapter.clickListener = object : IOnItemClickListener<Unit> {
            override fun onCLick() { //show bottom sheet to add feeding detail
                if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetFeedingDetail") == null) {
                    val bottomSheetFeedingDetail =
                        BottomSheetFeedingDetail.newInstance(feedingId = args.feedingId)

                    bottomSheetFeedingDetail.show(
                        requireActivity().supportFragmentManager,
                        "BottomSheetFeedingDetail"
                    )
                }
            }

            override fun onClick(obj: Unit) {}

            override fun onLongClick(obj: Unit): Boolean = true
        }

        val feedingDetailListAdapter = FeedingDetailListAdapter()

        feedingDetailListAdapter.clickListener =
            object : IOnItemClickListener<FeedingDetailDomain> {
                override fun onCLick() {}

                override fun onClick(obj: FeedingDetailDomain) {}

                override fun onLongClick(obj: FeedingDetailDomain): Boolean {
                    if (childFragmentManager.findFragmentByTag("BottomSheetAdd") == null && childFragmentManager.findFragmentByTag(
                            "BottomSheetActionFeedingDetail"
                        ) == null
                    ) {
                        val bottomSheetAction =
                            BottomSheetActionFeedingDetail.newInstance(detailId = obj.detail_id)

                        bottomSheetAction.show(
                            childFragmentManager,
                            "BottomSheetActionFeedingDetail"
                        )
                    }
                    return true
                }
            }

        feedingDetailViewModel.loadFeedingData(args.feedingId).observe(viewLifecycleOwner, {

            val headerCardAdapter = HeaderCardAdapter(
                tanggal = it.tanggal_feeding)

            headerCardAdapter.clickListener = object : IOnItemClickListener<Unit>{
                override fun onCLick() {
                    if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetFeeding") == null) {
                        val bottomSheetFeeding =
                            BottomSheetFeeding.newInstance(feedingId = args.feedingId, kerambaId = args.kerambaId)

                        bottomSheetFeeding.show(
                            requireActivity().supportFragmentManager,
                            "BottomSheetFeeding"
                        )
                    }
                }

                override fun onClick(obj: Unit) {}

                override fun onLongClick(obj: Unit): Boolean = true
            }

            val concatAdapter =
                ConcatAdapter(
                    headerCardAdapter,
                    feedingDetailHeaderAdapter,
                    feedingDetailListAdapter
                )

            binding.feedingDetailList.adapter = concatAdapter
        })


        feedingDetailViewModel.getAllFeedingDetailAndPakan(args.feedingId)
            .observe(viewLifecycleOwner, { list ->
                feedingDetailListAdapter.submitList(list)
            })
    }

    private fun setupObserver() {
        pakanViewModel.init.observe(viewLifecycleOwner, {
            if (it == false) {
                pakanViewModel.startInit()
            }
        })


        feedingDetailViewModel.loadKerambaData(args.kerambaId).observe(viewLifecycleOwner, {
            binding.toolbarFragment.title = "Pakan ${it.nama_keramba}"
        })

        pakanViewModel.requestGetResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                }
                is NetworkResult.Loading -> {
                    if (!binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = true
                    }
                }
                is NetworkResult.Loaded -> {
                    feedingDetailViewModel.fetchFeedingDetail(args.feedingId)
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        pakanViewModel.doneToastException()
                    }

                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.feedingDetailList.visibility = View.VISIBLE
                }
            }
        })

        feedingDetailViewModel.requestGetResult.observe(viewLifecycleOwner, { result ->
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

                    binding.feedingDetailList.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        feedingDetailViewModel.doneToastException()
                    }

                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.feedingDetailList.visibility = View.VISIBLE
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

    override fun onRefresh() {
        feedingDetailViewModel.fetchFeedingDetail(args.feedingId)
    }
}