package com.cemebsa.biomassa.views.summary.panen

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
import com.cemebsa.biomassa.adapters.HeaderButtonAdapter
import com.cemebsa.biomassa.adapters.PanenListAdapter
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.databinding.FragmentPanenBinding
import com.cemebsa.biomassa.views.add.BottomSheetPanen
import com.cemebsa.biomassa.utils.observeOnce
import com.cemebsa.biomassa.viewmodels.BiotaViewModel
import com.cemebsa.biomassa.viewmodels.PanenViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM = "kerambaId"

@AndroidEntryPoint
class PanenFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private val panenViewModel by activityViewModels<PanenViewModel>()

    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private lateinit var binding: FragmentPanenBinding

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
        binding = FragmentPanenBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPanenList()

        setupObserver()

        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun fetchPanen() {
        kerambaId?.let {
            panenViewModel.fetchPanen(kerambaId!!)
        }
    }

    private fun setupObserver() {
        panenViewModel.requestGetResult.observe(viewLifecycleOwner, { result ->
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

                    binding.panenList.visibility = View.VISIBLE
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        panenViewModel.doneToastException()
                    }

                    if (binding.swipeRefresh.isRefreshing) {
                        binding.swipeRefresh.isRefreshing = false
                    }

                    binding.panenList.visibility = View.VISIBLE
                }
            }
        })

        panenViewModel.init.observe(viewLifecycleOwner, {
            if (it == false) {
                kerambaId?.let { panenViewModel.fetchPanen(kerambaId!!) }
            }
        })
    }

    private fun setupPanenList() {

        kerambaId?.let {
            panenViewModel.getlistPanen(kerambaId!!).observe(viewLifecycleOwner, {
                val panenListAdapter = PanenListAdapter()

                val panenHeaderAdapter = HeaderButtonAdapter()

                panenHeaderAdapter.clickListener = object : IOnItemClickListener<Unit> {
                    override fun onCLick() {
                        if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetPanen") == null) {

                            kerambaId?.let {
                                biotaViewModel.getAllBiota(kerambaId!!)
                                    .observeOnce(viewLifecycleOwner, { list ->
                                        if (list.isEmpty()) {
                                            Toast.makeText(
                                                requireContext(),
                                                "Tidak ada biota",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            val bottomSheetPanen =
                                                BottomSheetPanen.newInstance(kerambaId = kerambaId!!)

                                            bottomSheetPanen.show(
                                                requireActivity().supportFragmentManager,
                                                "BottomSheetPanen"
                                            )
                                        }
                                    })
                            }
                        }
                    }

                    override fun onClick(obj: Unit) {}

                    override fun onLongClick(obj: Unit): Boolean = true
                }

                val concatAdapter = ConcatAdapter(panenHeaderAdapter, panenListAdapter)

                binding.panenList.adapter = concatAdapter

                panenListAdapter.submitList(it)
            })
        }


    }

    override fun onRefresh() {
        fetchPanen()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment BiotaFragment.
         */
        @JvmStatic
        fun newInstance(kerambaId: Int) =
            PanenFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM, kerambaId)
                }
            }
    }
}