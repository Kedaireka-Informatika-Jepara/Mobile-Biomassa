package com.cemebsa.biomassa.views.add

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.databinding.BottomSheetPengukuranBinding
import com.cemebsa.biomassa.databinding.BottomSheetPerhitunganBinding
import com.cemebsa.biomassa.models.domain.BiotaDomain
import com.cemebsa.biomassa.models.domain.KerambaDomain
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.utils.convertLongToDateString
import com.cemebsa.biomassa.viewmodels.KerambaViewModel
import com.cemebsa.biomassa.viewmodels.PengukuranViewModel
import com.cemebsa.biomassa.viewmodels.PerhitunganViewModel
import com.cemebsa.biomassa.views.components.DatePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

private const val ARG_PARAM1 = "biotaId"

private const val ARG_PARAM2 = "kerambaId"

@AndroidEntryPoint
class BottomSheetPerhitungan : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener,
    DatePickerDialog.OnDateSetListener {

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private val perhitunganViewModel by activityViewModels<PerhitunganViewModel>()

    private lateinit var mapKerambatoBiota: Map<KerambaDomain, List<BiotaDomain>>

    private lateinit var kerambaList: List<KerambaDomain>

    private lateinit var biotaList: List<BiotaDomain>

    private lateinit var binding: BottomSheetPerhitunganBinding

    private var kerambaId: Int? = 0

    private var biotaId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            biotaId = if (it.getInt(ARG_PARAM1) > 0) it.getInt(ARG_PARAM1) else null

            kerambaId = if (it.getInt(ARG_PARAM2) > 0) it.getInt(ARG_PARAM2) else null
        }
    }

    override fun onStart() {
        super.onStart()

        val view: FrameLayout = dialog?.findViewById(R.id.design_bottom_sheet)!!

        view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

        val behavior = BottomSheetBehavior.from(view)

        behavior.peekHeight = 3000
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
    }

    override fun onCancel(dialog: DialogInterface) {
        dialog.dismiss()
        super.onCancel(dialog)
    }

    override fun dismiss() {
        hideKeyBoard()
        super.dismiss()
    }

    private fun hideKeyBoard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = dialog?.window?.currentFocus
        view?.let {
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetPerhitunganBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomSheetPerhitungan = this@BottomSheetPerhitungan

        setupDropdown()

        setupObserver()
    }

    private fun isEntryValid(hidup: String, mati: String, tanggal: String): Boolean =
        perhitunganViewModel.isEntryValid(hidup, mati, tanggal)

    fun savePerhitungan() {
        binding.apply {
            if (isEntryValid(
                    jumlahHidupEt.text.toString(),
                    jumlahMatiEt.text.toString(),
                    tanggalHitungEt.text.toString()
                )
            ) {
                perhitunganViewModel.insertPerhitungan(
                    jumlahHidupEt.text.toString(),
                    jumlahMatiEt.text.toString(),
                    tanggalHitungEt.text.toString()
                )

            } else {
                if (TextUtils.isEmpty(jumlahMatiEt.text)) {
                    jumlahMatiEt.error = "Jumlah mati harus diisi!"
                }

                if (TextUtils.isEmpty(jumlahHidupEt.text)) {
                    jumlahHidupEt.error = "Jumlah hidup harus diisi!"
                }

                if (TextUtils.isEmpty(tanggalHitungEt.text)) {
                    tanggalHitungEt.error = "Tanggal perhitungan harus diisi!"
                }
            }
        }
    }

    private fun setupObserver() {
        perhitunganViewModel.requestPostAddResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                    binding.apply {
                        savePerhitunganBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }
                }
                is NetworkResult.Loading -> {
                    binding.apply {
                        savePerhitunganBtn.visibility = View.GONE

                        progressLoading.visibility = View.VISIBLE
                    }
                }
                is NetworkResult.Loaded -> {
                    binding.apply {
                        savePerhitunganBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != ""){
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    biotaId?.let { perhitunganViewModel.fetchPerhitungan(biotaId!!) }

                    perhitunganViewModel.donePostAddRequest()

                    this.dismiss()
                }
                is NetworkResult.Error -> {
                    binding.apply {
                        savePerhitunganBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    perhitunganViewModel.donePostAddRequest()
                }
            }
        })
    }

    private fun setupDropdown() {
        kerambaViewModel.loadKerambaAndBiota().observe(viewLifecycleOwner, {
            mapKerambatoBiota = it

            kerambaList = it.keys.toList()

            val kerambaIdlist = kerambaList.map { keramba -> keramba.keramba_id }

            val kerambaAdapter =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    kerambaList.map { kerambaDomain -> kerambaDomain.nama_keramba }
                )

            kerambaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.kerambaDropdown.adapter = kerambaAdapter

            kerambaId.let {
                val index = kerambaIdlist.indexOf(kerambaId!!)

                binding.kerambaDropdown.setSelection(index)
            }

            binding.kerambaDropdown.onItemSelectedListener = this@BottomSheetPerhitungan

            binding.biotaDropdown.onItemSelectedListener = this@BottomSheetPerhitungan
        })
    }

    override fun onItemSelected(parent: AdapterView<*>, p1: View?, pos: Int, p3: Long) {
        when (parent.id) {
            binding.kerambaDropdown.id -> {
                val keramba = kerambaList[pos]

                biotaList = mapKerambatoBiota[keramba]!!

                val biotaIdlist = biotaList.map { biota -> biota.biota_id }

                val biotaAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    biotaList.map { biotaDomain -> biotaDomain.jenis_biota }
                )

                biotaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                binding.biotaDropdown.adapter = biotaAdapter

                biotaId?.let {
                    val index = biotaIdlist.indexOf(biotaId!!)

                    binding.biotaDropdown.setSelection(index)
                }
            }
            binding.biotaDropdown.id -> {
                val biota = biotaList[pos]

                perhitunganViewModel.selectBiotaId(biota.biota_id)
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    fun showDatePicker() {
        if (childFragmentManager.findFragmentByTag("DatePicker") == null) {
            DatePickerFragment.create().show(childFragmentManager, "DatePicker")
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate: Calendar = Calendar.getInstance()
        selectedDate.set(year, month, dayOfMonth)

        binding.tanggalHitungEt.setText(
            convertLongToDateString(selectedDate.timeInMillis, "EEEE dd-MMM-yyyy"),
            TextView.BufferType.EDITABLE
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment.
         */
        @JvmStatic
        fun newInstance(biotaId: Int, kerambaId: Int) =
            BottomSheetPerhitungan().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, biotaId)

                    putInt(ARG_PARAM2, kerambaId)
                }
            }
    }
}