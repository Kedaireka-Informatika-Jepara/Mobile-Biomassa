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
import com.cemebsa.biomassa.databinding.BottomSheetPanenBinding
import com.cemebsa.biomassa.models.domain.BiotaDomain
import com.cemebsa.biomassa.models.domain.KerambaDomain
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.utils.convertLongToDateString
import com.cemebsa.biomassa.viewmodels.BiotaViewModel
import com.cemebsa.biomassa.viewmodels.KerambaViewModel
import com.cemebsa.biomassa.viewmodels.PanenViewModel
import com.cemebsa.biomassa.views.components.DatePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

private const val ARG_PARAM = "kerambaId"

@AndroidEntryPoint
class BottomSheetPanen : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener,
    DatePickerDialog.OnDateSetListener {
    private lateinit var binding: BottomSheetPanenBinding

    private val panenViewModel by activityViewModels<PanenViewModel>()

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private lateinit var mapKerambatoBiota: Map<KerambaDomain, List<BiotaDomain>>

    private lateinit var kerambaList: List<KerambaDomain>

    private lateinit var biotaList: List<BiotaDomain>

    private var kerambaId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            kerambaId = if (it.getInt(ARG_PARAM) > 0) it.getInt(ARG_PARAM) else null
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

        binding = BottomSheetPanenBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomSheetPanen = this@BottomSheetPanen

        setupDropdown()

        setupObserver()
    }

    private fun setupObserver() {
        panenViewModel.requestPostAddResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                    binding.apply {
                        savePanenBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }
                }
                is NetworkResult.Loading -> {
                    binding.apply {
                        savePanenBtn.visibility = View.GONE

                        progressLoading.visibility = View.VISIBLE
                    }
                }
                is NetworkResult.Loaded -> {
                    binding.apply {
                        savePanenBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    panenViewModel.inputKerambaId.value?.let {
                        biotaViewModel.fetchBiota(it)

                        panenViewModel.fetchPanen(it)
                    }

                    panenViewModel.donePostAddRequest()

                    this.dismiss()
                }
                is NetworkResult.Error -> {
                    binding.apply {
                        savePanenBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    panenViewModel.donePostAddRequest()
                }
            }

        })
    }

    fun savePanen() {
        binding.apply {
            if (isEntryValid(
                    binding.panjangBiotaEt.text.toString(),
                    binding.bobotBiotaEt.text.toString(),
                    binding.jumlahBiotaEt.text.toString(),
                    binding.jumlahGagalBiotaEt.text.toString(),
                    binding.tanggalPanenEt.text.toString()
                )
            ) {
                panenViewModel.insertPanen(
                    binding.panjangBiotaEt.text.toString(),
                    binding.bobotBiotaEt.text.toString(),
                    binding.jumlahBiotaEt.text.toString(),
                    binding.jumlahGagalBiotaEt.text.toString(),
                    binding.tanggalPanenEt.text.toString()
                )
            } else {
                if (TextUtils.isEmpty(panjangBiotaEt.text)) {
                    panjangBiotaEt.error = "Panjang biota harus diisi!"
                }

                if (TextUtils.isEmpty(bobotBiotaEt.text)) {
                    bobotBiotaEt.error = "Bobot biota harus diisi!"
                }

                if (TextUtils.isEmpty(jumlahBiotaEt.text)) {
                    jumlahBiotaEt.error = "Jumlah biota harus diisi!"
                }

                if (TextUtils.isEmpty(jumlahGagalBiotaEt.text)) {
                    jumlahGagalBiotaEt.error = "Jumlah gagal panen harus diisi!"
                }

                if (TextUtils.isEmpty(tanggalPanenEt.text)) {
                    tanggalPanenEt.error = "Tanggal panen harus diisi!"
                }
            }
        }
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

            kerambaId?.let {
                val index = kerambaIdlist.indexOf(kerambaId!!)

                binding.kerambaDropdown.setSelection(index)
            }

            binding.kerambaDropdown.onItemSelectedListener = this@BottomSheetPanen

            binding.biotaDropdown.onItemSelectedListener = this@BottomSheetPanen
        })
    }

    fun showDatePicker() {
        if (childFragmentManager.findFragmentByTag("DatePicker") == null) {
            DatePickerFragment.create().show(childFragmentManager, "DatePicker")
        }
    }

    private fun isEntryValid(
        panjang: String,
        bobot: String,
        jumlahHidup: String,
        jumlahMati: String,
        tanggal: String
    ): Boolean {
        return panenViewModel.isEntryValid(panjang, bobot, jumlahHidup, jumlahMati, tanggal)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate: Calendar = Calendar.getInstance()
        selectedDate.set(year, month, dayOfMonth)

        binding.tanggalPanenEt.setText(
            convertLongToDateString(selectedDate.timeInMillis, "EEEE dd-MMM-yyyy"),
            TextView.BufferType.EDITABLE
        )
    }


    override fun onItemSelected(parent: AdapterView<*>, p1: View?, pos: Int, p3: Long) {
        when (parent.id) {
            binding.kerambaDropdown.id -> {
                val keramba = kerambaList[pos]

                panenViewModel.selectKeramba(keramba.keramba_id)

                biotaList = mapKerambatoBiota[keramba]!!

                val biotaAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    biotaList.map { biotaDomain -> biotaDomain.jenis_biota }
                )

                biotaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                binding.biotaDropdown.adapter = biotaAdapter
            }

            binding.biotaDropdown.id -> {
                val biota = biotaList[pos]

                panenViewModel.selectBiota(biota.biota_id)
            }

        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment.
         */
        @JvmStatic
        fun newInstance(kerambaId: Int) =
            BottomSheetPanen().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM, kerambaId)
                }
            }
    }
}