package com.cemebsa.biomassa.views.summary.biota

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cemebsa.biomassa.abstractions.view.IOnItemClickListener
import com.cemebsa.biomassa.adapters.HeaderButtonAdapter
import com.cemebsa.biomassa.adapters.PengukuranListAdapter
import com.cemebsa.biomassa.adapters.PerhitunganListAdapter
import com.cemebsa.biomassa.databinding.FragmentBiotaDataBinding
import com.cemebsa.biomassa.databinding.FragmentBiotaHitungBinding
import com.cemebsa.biomassa.models.domain.PengukuranDomain
import com.cemebsa.biomassa.models.domain.PerhitunganDomain
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.viewmodels.PengukuranViewModel
import com.cemebsa.biomassa.viewmodels.PerhitunganViewModel
import com.cemebsa.biomassa.views.action.BottomSheetActionPengukuran
import com.cemebsa.biomassa.views.action.BottomSheetActionPerhitungan
import com.cemebsa.biomassa.views.add.BottomSheetPengukuran
import com.cemebsa.biomassa.views.add.BottomSheetPerhitungan
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM1 = "biotaId"

private const val ARG_PARAM2 = "kerambaId"

@AndroidEntryPoint
class BiotaHitungFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentBiotaHitungBinding

    private val perhitunganViewModel by activityViewModels<PerhitunganViewModel>()

    private var kerambaId: Int? = 0

    private var biotaId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            biotaId = it.getInt(ARG_PARAM1)

            kerambaId = it.getInt(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBiotaHitungBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPerhitunganList()

        setupObserver()

        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun setupObserver() {
        perhitunganViewModel.requestGetResult.observe(viewLifecycleOwner, { result ->
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

                    binding.perhitunganList.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        perhitunganViewModel.doneToastException()
                    }

                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.perhitunganList.visibility = View.VISIBLE
                }
            }
        })

        biotaId?.let {
            perhitunganViewModel.init.observe(viewLifecycleOwner, {
                if (it == false) {
                    perhitunganViewModel.startInit(biotaId!!)
                }
            })
        }
    }

    private fun setupPerhitunganList() {
        biotaId?.let {
            perhitunganViewModel.getAllBiotaData(biotaId!!).observe(viewLifecycleOwner, { list ->

                val headerButtonAdapter = HeaderButtonAdapter()

                headerButtonAdapter.clickListener = object : IOnItemClickListener<Unit> {
                    override fun onCLick() {
                        if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetPerhitungan") == null) {

                            val bottomSheetPerhitungan = BottomSheetPerhitungan.newInstance(
                                biotaId = if (biotaId != null) biotaId!! else 0,
                                kerambaId = if (kerambaId != null) kerambaId!! else 0
                            )

                            bottomSheetPerhitungan.show(
                                requireActivity().supportFragmentManager,
                                "BottomSheetPerhitungan"
                            )
                        }
                    }

                    override fun onClick(obj: Unit) {}

                    override fun onLongClick(obj: Unit): Boolean = true
                }

                val perhitunganListAdapter = PerhitunganListAdapter()

                perhitunganListAdapter.clickListener =
                    object : IOnItemClickListener<PerhitunganDomain> {
                        override fun onCLick() {}

                        override fun onClick(obj: PerhitunganDomain) {}

                        override fun onLongClick(obj: PerhitunganDomain): Boolean {
                            if (childFragmentManager.findFragmentByTag("BottomSheetAdd") == null && childFragmentManager.findFragmentByTag(
                                    "BottomSheetActionPerhitungan"
                                ) == null
                            ) {
                                val bottomSheetAction =
                                    BottomSheetActionPerhitungan.newInstance(perhitunganId = obj.perhitungan_id)

                                bottomSheetAction.show(
                                    childFragmentManager,
                                    "BottomSheetActionPerhitungan"
                                )
                            }
                            return true
                        }
                    }

                val concatAdapter = ConcatAdapter(headerButtonAdapter, perhitunganListAdapter)

                binding.perhitunganList.adapter = concatAdapter

                perhitunganListAdapter.submitList(list)
            })
        }
    }

    override fun onRefresh() {
        biotaId?.let {
            perhitunganViewModel.fetchPerhitungan(biotaId!!)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment.
         */
        @JvmStatic
        fun newInstance(biotaId: Int, kerambaId: Int) =
            BiotaHitungFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, biotaId)

                    putInt(ARG_PARAM2, kerambaId)
                }
            }
    }
}