package com.cemebsa.biomassa.views.summary.biota

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
import com.cemebsa.biomassa.adapters.BiotaListAdapter
import com.cemebsa.biomassa.adapters.HeaderButtonAdapter
import com.cemebsa.biomassa.databinding.FragmentBiotaBinding
import com.cemebsa.biomassa.models.domain.BiotaDomain
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.viewmodels.BiotaViewModel
import com.cemebsa.biomassa.viewmodels.PengukuranViewModel
import com.cemebsa.biomassa.viewmodels.PerhitunganViewModel
import com.cemebsa.biomassa.views.action.BottomSheetActionBiota
import com.cemebsa.biomassa.views.add.BottomSheetBiota
import com.cemebsa.biomassa.views.summary.SummaryFragmentDirections
import dagger.hilt.android.AndroidEntryPoint


private const val ARG_PARAM = "kerambaId"

@AndroidEntryPoint
class BiotaFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private val pengukuranViewModel by activityViewModels<PengukuranViewModel>()

    private val perhitunganViewModel by activityViewModels<PerhitunganViewModel>()

    private lateinit var binding: FragmentBiotaBinding

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
        binding = FragmentBiotaBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBiotaList()

        setupObserver()

        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun setupObserver() {
        biotaViewModel.requestGetResult.observe(viewLifecycleOwner, { result ->
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

                    binding.biotaList.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        biotaViewModel.doneToastException()
                    }

                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.biotaList.visibility = View.VISIBLE
                }
            }
        })

        kerambaId?.let {
            biotaViewModel.init.observe(viewLifecycleOwner, {
                if (it == false) {
                    biotaViewModel.startInit(kerambaId!!)
                }
            })
        }
    }

    private fun setupBiotaList() {
        kerambaId?.let {
            biotaViewModel.getAllBiota(kerambaId!!).observe(viewLifecycleOwner, { listBiota ->

                val biotaHeaderAdapter = HeaderButtonAdapter()

                biotaHeaderAdapter.clickListener = object : IOnItemClickListener<Unit> {
                    override fun onCLick() {
                        if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetBiota") == null) {
                            val bottomSheetBiota =
                                BottomSheetBiota.newInstance(biotaId = 0, kerambaId = kerambaId!!)

                            bottomSheetBiota.show(
                                requireActivity().supportFragmentManager,
                                "BottomSheetBiota"
                            )
                        }
                    }

                    override fun onClick(obj: Unit) {}

                    override fun onLongClick(obj: Unit): Boolean = true

                }

                val biotaListAdapter = BiotaListAdapter()

                biotaListAdapter.clickListener = object : IOnItemClickListener<BiotaDomain> {
                    override fun onCLick() {}

                    override fun onClick(obj: BiotaDomain) {
                        kerambaId?.let {
                            navController.navigate(
                                SummaryFragmentDirections.actionSummaryFragmentToBiotaTabFragment(
                                    obj.biota_id,
                                    kerambaId!!
                                )
                            )
                        }

                        pengukuranViewModel.restartInit()
                        perhitunganViewModel.restartInit()
                    }

                    override fun onLongClick(obj: BiotaDomain): Boolean
                    //long click listener
                    {
                        if (childFragmentManager.findFragmentByTag("BottomSheetAdd") == null && childFragmentManager.findFragmentByTag(
                                "BottomSheetActionBiota"
                            ) == null
                        ) {

                            val bottomSheetAction = BottomSheetActionBiota.newInstance(
                                biotaId = obj.biota_id,
                                kerambaId = obj.keramba_id
                            )

                            bottomSheetAction.show(childFragmentManager, "BottomSheetActionBiota")
                        }
                        return true
                    }
                }

                val concatAdapter = ConcatAdapter(biotaHeaderAdapter, biotaListAdapter)

                binding.biotaList.adapter = concatAdapter

                biotaListAdapter.submitList(listBiota)
            })
        }
    }

    override fun onRefresh() {
        kerambaId?.let {
            biotaViewModel.fetchBiota(kerambaId!!)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment.
         */
        @JvmStatic
        fun newInstance(kerambaId: Int) =
            BiotaFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM, kerambaId)
                }
            }
    }
}