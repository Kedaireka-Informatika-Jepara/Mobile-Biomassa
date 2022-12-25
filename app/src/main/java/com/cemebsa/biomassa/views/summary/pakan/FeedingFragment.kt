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
import androidx.recyclerview.widget.ConcatAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cemebsa.biomassa.abstractions.view.IOnItemClickListener
import com.cemebsa.biomassa.adapters.FeedingListAdapter
import com.cemebsa.biomassa.adapters.HeaderButtonAdapter
import com.cemebsa.biomassa.databinding.FragmentPakanBinding
import com.cemebsa.biomassa.models.domain.FeedingDomain
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.viewmodels.FeedingViewModel
import com.cemebsa.biomassa.views.action.BottomSheetActionFeeding
import com.cemebsa.biomassa.views.add.BottomSheetFeeding
import com.cemebsa.biomassa.views.summary.SummaryFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM = "kerambaId"

@AndroidEntryPoint
class FeedingFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private val feedingViewModel by activityViewModels<FeedingViewModel>()

    private lateinit var binding: FragmentPakanBinding

    private lateinit var navController: NavController

    private var kerambaId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            kerambaId = it.getInt(ARG_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPakanBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFeedingList()

        setupObserver()

        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun setupObserver() {
        feedingViewModel.requestGetResult.observe(viewLifecycleOwner, { result ->
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

                    binding.feedingList.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        feedingViewModel.doneToastException()
                    }

                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.feedingList.visibility = View.VISIBLE
                }
            }
        })

        kerambaId?.let {
            feedingViewModel.init.observe(viewLifecycleOwner, {
                if (it == false) {
                    feedingViewModel.startInit(kerambaId!!)
                }
            })
        }
    }

    private fun setupFeedingList() {
        kerambaId?.let {

            feedingViewModel.getAllFeeding(kerambaId!!).observe(viewLifecycleOwner, { listFeeding ->
                val feedingHeaderAdapter = HeaderButtonAdapter()

                feedingHeaderAdapter.clickListener = object : IOnItemClickListener<Unit> {
                    override fun onCLick() {
                        if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetFeeding") == null) {

                            val bottomSheetFeeding =
                                BottomSheetFeeding.newInstance(
                                    kerambaId = kerambaId!!,
                                    feedingId = 0
                                )

                            bottomSheetFeeding.show(
                                requireActivity().supportFragmentManager,
                                "BottomSheetFeeding"
                            )
                        }
                    }

                    override fun onClick(obj: Unit) {}

                    override fun onLongClick(obj: Unit): Boolean = true
                }

                val feedingListAdapter = FeedingListAdapter()

                feedingListAdapter.clickListener = object : IOnItemClickListener<FeedingDomain> {
                    override fun onCLick() {}

                    override fun onClick(obj: FeedingDomain) {
                        navController.navigate(
                            SummaryFragmentDirections.actionSummaryFragmentToFeedingDetailFragment(
                                obj.keramba_id,
                                obj.feeding_id
                            )
                        )
                    }

                    override fun onLongClick(obj: FeedingDomain): Boolean //long click listener
                    {
                        if (childFragmentManager.findFragmentByTag("BottomSheetAdd") == null && childFragmentManager.findFragmentByTag(
                                "BottomSheetActionFeeding"
                            ) == null
                        ) {

                            val bottomSheetAction = BottomSheetActionFeeding.newInstance(
                                feedingId = obj.feeding_id,
                                kerambaId = obj.keramba_id
                            )

                            bottomSheetAction.show(childFragmentManager, "BottomSheetActionFeeding")
                        }
                        return true
                    }
                }

                val concatAdapter = ConcatAdapter(feedingHeaderAdapter, feedingListAdapter)

                binding.feedingList.adapter = concatAdapter

                feedingListAdapter.submitList(listFeeding)
            })
        }
    }

    private fun fetchFeeding() {
        kerambaId?.let {
            feedingViewModel.fetchFeeding(kerambaId!!)
        }
    }

    override fun onRefresh() {
        fetchFeeding()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment BiotaFragment.
         */
        @JvmStatic
        fun newInstance(kerambaId: Int) =
            FeedingFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM, kerambaId)
                }
            }
    }
}