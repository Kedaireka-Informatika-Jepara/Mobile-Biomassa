package com.cemebsa.biomassa.views.summary.keramba

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.databinding.FragmentInfoBinding
import com.cemebsa.biomassa.models.domain.BiotaDomain
import com.cemebsa.biomassa.models.domain.KerambaDomain
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.utils.convertLongToDateString
import com.cemebsa.biomassa.viewmodels.BiotaViewModel
import com.cemebsa.biomassa.viewmodels.KerambaViewModel
import com.cemebsa.biomassa.views.add.BottomSheetKeramba
import com.cemebsa.biomassa.views.summary.SummaryFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM = "kerambaId"

@AndroidEntryPoint
class InfoFragment : Fragment() {
    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private lateinit var binding: FragmentInfoBinding

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
        binding = FragmentInfoBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObserver()

        setupFragment()
    }

    private fun setupObserver() {
        kerambaId?.let {
            kerambaViewModel.loadKerambaData(kerambaId!!)
                .observe(viewLifecycleOwner, { keramba -> bind(keramba) })

            biotaViewModel.getAllBiota(kerambaId!!).observe(viewLifecycleOwner, { list ->
                if (biotaViewModel.requestGetResult.value is NetworkResult.Loaded || biotaViewModel.requestGetResult.value is NetworkResult.Error) {
                    list?.let{ initBiotaChart(list) }
                }
            })
        }

        biotaViewModel.requestGetResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> { }
                is NetworkResult.Loading -> {
                    binding.apply {
                        chartCard.visibility = View.VISIBLE

                        progressLoading.visibility = View.VISIBLE

                        biotaChart.visibility = View.GONE
                    }
                }
                is NetworkResult.Loaded -> {
                    binding.apply {
                        progressLoading.visibility = View.GONE

                        chartCard.visibility = View.GONE
                    }
                }
                is NetworkResult.Error -> {
                    binding.apply {
                        progressLoading.visibility = View.GONE

                        chartCard.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun setupFragment() {
        binding.biotaHistoryBtn.setOnClickListener {
            kerambaId?.let {
                navController.navigate(
                    SummaryFragmentDirections.actionSummaryFragmentToBiotaHistoryFragment(kerambaId!!)
                )
            }
        }
    }

    private fun bind(keramba: KerambaDomain) {
        with(binding) {
            namaKerambaTv.text = keramba.nama_keramba

            tanggalInstallTv.text =
                convertLongToDateString(keramba.tanggal_install, "EEEE dd-MMM-yyyy")

            ukuranKerambaTv.text =
                getString(R.string.meter_kubik, keramba.ukuran.toString())

            editBtn.setOnClickListener {
                if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetKeramba") == null) {
                    val bottomSheetKeramba =
                        BottomSheetKeramba.newInstance(kerambaId = keramba.keramba_id)

                    bottomSheetKeramba.show(
                        requireActivity().supportFragmentManager,
                        "BottomSheetKeramba"
                    )
                }
            }
        }
    }

    private fun initBiotaChart(list: List<BiotaDomain>) {
        if (list.isEmpty()) {
            binding.chartCard.visibility = View.GONE
        } else {
            binding.chartCard.visibility = View.VISIBLE

            binding.biotaChart.visibility = View.VISIBLE

            val barEntries = ArrayList<PieEntry>()

            //mapping data if data is double
            val mappedData = mutableMapOf<String, Int>()

            list.forEach { mappedData[it.jenis_biota.lowercase()] = 0 }

            list.forEach {
                mappedData[it.jenis_biota.lowercase()] =
                    mappedData[it.jenis_biota.lowercase()]!! + it.jumlah_bibit
            }

            mappedData.forEach { barEntries.add(PieEntry(it.value.toFloat(), it.key)) }

            val dataSet = PieDataSet(barEntries, "")

            dataSet.setDrawIcons(false)
            dataSet.sliceSpace = 3f
            dataSet.iconsOffset = MPPointF(0F, 40F)
            dataSet.selectionShift = 5f
            dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

            val dataNow = PieData(dataSet)
            dataNow.setValueTextSize(20f)
            dataNow.setValueTextColor(Color.WHITE)

            binding.biotaChart.apply {
                data = dataNow
                animateY(1400, Easing.EaseInOutQuad)
                setDrawEntryLabels(false)
                holeRadius = 58f
                description.text = ""

                legend.textSize = 15f
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        legend.textColor = Color.WHITE
                    }
                }

                transparentCircleRadius = 61f
                isDrawHoleEnabled = true
                setHoleColor(Color.WHITE)
                centerText = "Komposisi Biota"
                setCenterTextSize(15f)

                invalidate()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment BiotaFragment.
         */
        @JvmStatic
        fun newInstance(kerambaId: Int) =
            InfoFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM, kerambaId)
                }
            }
    }
}