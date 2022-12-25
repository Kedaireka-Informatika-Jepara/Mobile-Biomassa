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
import com.cemebsa.biomassa.databinding.FragmentBiotaDataBinding
import com.cemebsa.biomassa.models.domain.PengukuranDomain
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.viewmodels.PengukuranViewModel
import com.cemebsa.biomassa.views.action.BottomSheetActionPengukuran
import com.cemebsa.biomassa.views.add.BottomSheetPengukuran
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM1 = "biotaId"

private const val ARG_PARAM2 = "kerambaId"

@AndroidEntryPoint
class BiotaDataFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentBiotaDataBinding

    private val pengukuranViewModel by activityViewModels<PengukuranViewModel>()

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
        binding = FragmentBiotaDataBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPengukuranList()

        setupObserver()

        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun setupObserver() {
        pengukuranViewModel.requestGetResult.observe(viewLifecycleOwner, { result ->
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

                    binding.pengukuranList.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        pengukuranViewModel.doneToastException()
                    }

                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.pengukuranList.visibility = View.VISIBLE
                }
            }
        })

        biotaId?.let {
            pengukuranViewModel.init.observe(viewLifecycleOwner, {
                if (it == false) {
                    pengukuranViewModel.startInit(biotaId!!)
                }
            })
        }
    }

    private fun setupPengukuranList() {
        biotaId?.let {
            pengukuranViewModel.getAllBiotaData(biotaId!!).observe(viewLifecycleOwner, { list ->

                val headerButtonAdapter = HeaderButtonAdapter()

                headerButtonAdapter.clickListener = object : IOnItemClickListener<Unit> {
                    override fun onCLick() {
                        if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetPengukuran") == null) {

                            val bottomSheetPengukuran = BottomSheetPengukuran.newInstance(
                                biotaId = if (biotaId != null) biotaId!! else 0,
                                kerambaId = if (kerambaId != null) kerambaId!! else 0
                            )

                            bottomSheetPengukuran.show(
                                requireActivity().supportFragmentManager,
                                "BottomSheetPengukuran"
                            )
                        }
                    }

                    override fun onClick(obj: Unit) {}

                    override fun onLongClick(obj: Unit): Boolean = true
                }

                val pengukuranListAdapter = PengukuranListAdapter()

                pengukuranListAdapter.clickListener =
                    object : IOnItemClickListener<PengukuranDomain> {
                        override fun onCLick() {}

                        override fun onClick(obj: PengukuranDomain) {}

                        override fun onLongClick(obj: PengukuranDomain): Boolean {
                            if (childFragmentManager.findFragmentByTag("BottomSheetAdd") == null && childFragmentManager.findFragmentByTag(
                                    "BottomSheetActionPengukuran"
                                ) == null
                            ) {
                                val bottomSheetAction =
                                    BottomSheetActionPengukuran.newInstance(pengukuranId = obj.pengukuran_id)

                                bottomSheetAction.show(
                                    childFragmentManager,
                                    "BottomSheetActionPengukuran"
                                )
                            }
                            return true
                        }
                    }

                val concatAdapter = ConcatAdapter(headerButtonAdapter, pengukuranListAdapter)

                binding.pengukuranList.adapter = concatAdapter

                pengukuranListAdapter.submitList(list)
            })
        }
    }

    override fun onRefresh() {
        biotaId?.let {
            pengukuranViewModel.fetchPengukuran(biotaId!!)
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
            BiotaDataFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, biotaId)

                    putInt(ARG_PARAM2, kerambaId)
                }
            }
    }
}