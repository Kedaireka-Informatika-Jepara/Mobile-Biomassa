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
import com.cemebsa.biomassa.adapters.PakanListAdapter
import com.cemebsa.biomassa.databinding.FragmentAddPakanBinding
import com.cemebsa.biomassa.models.domain.PakanDomain
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.viewmodels.PakanViewModel
import com.cemebsa.biomassa.views.action.BottomSheetActionPakan
import com.cemebsa.biomassa.views.add.BottomSheetPakan
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PakanFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private val pakanViewModel by activityViewModels<PakanViewModel>()

    private lateinit var binding: FragmentAddPakanBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddPakanBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }

        setupPakanList()

        setupObserver()

        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun setupObserver() {
        pakanViewModel.init.observe(viewLifecycleOwner, {
            if (it == false) {
                pakanViewModel.startInit()
            }
        })


        pakanViewModel.requestGetResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {}
                is NetworkResult.Loading -> {
                    if (!binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = true
                    }
                }
                is NetworkResult.Loaded -> {
                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.pakanList.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        pakanViewModel.doneToastException()
                    }

                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.pakanList.visibility = View.VISIBLE
                }
            }
        })

        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun setupPakanList() {
        val pakanListAdapter = PakanListAdapter()

        pakanListAdapter.clickListener = object : IOnItemClickListener<PakanDomain> {
            override fun onCLick() {}

            override fun onClick(obj: PakanDomain) // clickListener
            {
                if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetPakan") == null) {
                    val bottomSheetPakan = BottomSheetPakan.newInstance(obj.pakan_id)

                    bottomSheetPakan.show(
                        requireActivity().supportFragmentManager,
                        "BottomSheetPakan"
                    )
                }
            }

            override fun onLongClick(obj: PakanDomain): Boolean
            // longClickListener
            {
                if (childFragmentManager.findFragmentByTag("BottomSheetAdd") == null && childFragmentManager.findFragmentByTag(
                        "BottomSheetActionPakan"
                    ) == null
                ) {
                    val bottomSheetAction =
                        BottomSheetActionPakan.newInstance(pakanId = obj.pakan_id)

                    bottomSheetAction.show(childFragmentManager, "BottomSheetActionPakan")
                }
                return true
            }
        }

        binding.pakanList.adapter = pakanListAdapter

        pakanViewModel.getAll().observe(viewLifecycleOwner, {
            pakanListAdapter.submitList(it)
        })
    }

    override fun onRefresh() {
        pakanViewModel.fetchPakan()
    }
}