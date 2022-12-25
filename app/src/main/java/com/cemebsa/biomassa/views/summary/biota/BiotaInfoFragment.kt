package com.cemebsa.biomassa.views.summary.biota

import android.annotation.SuppressLint
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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.databinding.FragmentBiotaInfoBinding
import com.cemebsa.biomassa.models.domain.BiotaDomain
import com.cemebsa.biomassa.models.domain.PengukuranDomain
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.utils.convertLongToDateString
import com.cemebsa.biomassa.viewmodels.BiotaViewModel
import com.cemebsa.biomassa.viewmodels.PengukuranViewModel
import com.cemebsa.biomassa.views.add.BottomSheetBiota
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

private const val ARG_PARAM1 = "biotaId"

private const val ARG_PARAM2 = "kerambaId"

@AndroidEntryPoint
class BiotaInfoFragment : Fragment() {
    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private val pengukuranViewModel by activityViewModels<PengukuranViewModel>()

    private var listData = ArrayList<PengukuranDomain>()

    private lateinit var binding: FragmentBiotaInfoBinding

    private lateinit var navController: NavController

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
        binding = FragmentBiotaInfoBinding.inflate(inflater, container, false)

        navController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObserver()
    }

    private fun setupObserver() {
        biotaId?.let {
            biotaViewModel.loadBiotaData(biotaId!!)
                .observe(viewLifecycleOwner, { biota -> bind(biota) })

            pengukuranViewModel.getAllBiotaData(biotaId!!)
                .observe(viewLifecycleOwner, { list ->
                    if (pengukuranViewModel.requestGetResult.value is NetworkResult.Loaded || pengukuranViewModel.requestGetResult.value is NetworkResult.Error) {
                        setupLineChart(list)
                    }
                })
        }

        pengukuranViewModel.requestGetResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> { }
                is NetworkResult.Loading -> {
                    binding.apply {
                        panjangChartCard.visibility = View.VISIBLE

                        bobotChartCard.visibility = View.VISIBLE

                        progressLoadingPanjangChart.visibility = View.VISIBLE

                        progressLoadingBobotChart.visibility = View.VISIBLE

                        panjangChart.visibility = View.GONE

                        bobotChart.visibility = View.GONE
                    }
                }
                is NetworkResult.Loaded -> {
                    binding.apply {
                        panjangChartCard.visibility = View.GONE

                        bobotChartCard.visibility = View.GONE

                        progressLoadingPanjangChart.visibility = View.GONE

                        progressLoadingBobotChart.visibility = View.GONE
                    }
                }
                is NetworkResult.Error -> {
                    binding.apply {
                        panjangChartCard.visibility = View.GONE

                        bobotChartCard.visibility = View.GONE

                        progressLoadingPanjangChart.visibility = View.GONE

                        progressLoadingBobotChart.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun bind(biota: BiotaDomain) {
        with(binding) {
            jenisBiotaTv.text = biota.jenis_biota

            bobotBibitTv.text = biota.bobot.toString()

            ukuranBibitTv.text = biota.panjang.toString()

            jumlahBibitTv.text = biota.jumlah_bibit.toString()

            tanggalTebarTv.text = convertLongToDateString(biota.tanggal_tebar, "EEEE dd-MMM-yyyy")

            kerambaId?.let {
                editBtn.setOnClickListener { onEditBiota(kerambaId!!, biota.biota_id) }
            }
        }
    }

    private fun setupLineChart(list: List<PengukuranDomain>) {
        listData.clear()

        if (list.isEmpty()) {
            binding.panjangChartCard.visibility = View.GONE

            binding.bobotChartCard.visibility = View.GONE
        } else {
            binding.panjangChartCard.visibility = View.VISIBLE

            binding.bobotChartCard.visibility = View.VISIBLE

            binding.panjangChart.visibility = View.VISIBLE

            binding.bobotChart.visibility = View.VISIBLE

            if (list.size > 10) {
                listData.addAll(list.slice(0..9).reversed())
            } else {
                listData.addAll(list.reversed())
            }

            initChart(binding.panjangChart, listData)

            initChart(binding.bobotChart, listData)
        }
    }


    private fun initChart(chart: LineChart, list: List<PengukuranDomain>) {
        chart.apply {
            axisLeft.setDrawGridLines(false)

            axisRight.isEnabled = false

            description.isEnabled = false

            val xAxis: XAxis = this.xAxis

            xAxis.setDrawGridLines(false)

            xAxis.setDrawAxisLine(false)

            legend.isEnabled = true

            legend.textSize = 15f

            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP

            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER

            legend.orientation = Legend.LegendOrientation.HORIZONTAL

            legend.setDrawInside(false)

            animateX(1500, Easing.EaseInSine)

            xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE

            xAxis.valueFormatter = object : IndexAxisValueFormatter() {
                @SuppressLint("SimpleDateFormat")
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    val index = value.toInt()

                    return if (index < list.size) {
                        SimpleDateFormat("d MMM")
                            .format(list[index].tanggal_ukur).toString()
                    } else {
                        ""
                    }
                }
            }

            xAxis.setDrawLabels(true)

            val entries: ArrayList<Entry> = ArrayList()

            when (chart.id) {
                R.id.panjang_chart -> {

                    //TODO: Set value for xAxis.granularity
//                    xAxis.granularity = list.maxOf { it.panjang }.toFloat()

                    for (i in list.indices) {
                        val panjang = list[i]

                        entries.add(Entry(i.toFloat(), panjang.panjang.toFloat()))
                    }

                    val lineDataSet = LineDataSet(entries, "Panjang Biota")

                    val data = LineData(lineDataSet)

                    this.data = data

                    this.invalidate()
                }

                R.id.bobot_chart -> {
//                    xAxis.granularity = list.maxOf { it.bobot }.toFloat()

                    for (i in list.indices) {
                        val bobot = list[i]

                        entries.add(Entry(i.toFloat(), bobot.bobot.toFloat()))
                    }

                    val lineDataSet = LineDataSet(entries, "Bobot Biota")

                    val data = LineData(lineDataSet)

                    this.data = data

                    this.invalidate()
                }
            }

            data.setValueTextSize(12f)

            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    axisLeft.axisLineColor = Color.WHITE
                    axisLeft.textColor = Color.WHITE
                    xAxis.textColor = Color.WHITE
                    legend.textColor = Color.WHITE
                    data.setValueTextColor(Color.WHITE)
                }
            }
        }
    }

    private fun onEditBiota(kerambaId: Int, biotaId: Int) {
        if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetBiota") == null) {

            val bottomSheetBiota =
                BottomSheetBiota.newInstance(biotaId = biotaId, kerambaId = kerambaId)

            bottomSheetBiota.show(requireActivity().supportFragmentManager, "BottomSheetBiota")
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
            BiotaInfoFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, biotaId)

                    putInt(ARG_PARAM2, kerambaId)
                }
            }
    }
}