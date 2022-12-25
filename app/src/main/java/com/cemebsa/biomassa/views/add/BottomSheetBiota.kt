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
import com.cemebsa.biomassa.databinding.BottomSheetBiotaBinding
import com.cemebsa.biomassa.models.domain.BiotaDomain
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.utils.convertLongToDateString
import com.cemebsa.biomassa.utils.convertStringToDateLong
import com.cemebsa.biomassa.viewmodels.BiotaViewModel
import com.cemebsa.biomassa.viewmodels.KerambaViewModel
import com.cemebsa.biomassa.views.components.DatePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


private const val ARG_PARAM1 = "biotaId"

private const val ARG_PARAM2 = "kerambaId"

@AndroidEntryPoint
class BottomSheetBiota : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener,
    DatePickerDialog.OnDateSetListener {

    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private lateinit var binding: BottomSheetBiotaBinding

    private lateinit var mapKeramba: Map<String, Int>

    private var kerambaId: Int? = 0

    private var biotaId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            biotaId = if (it.getInt(ARG_PARAM1) > 0) it.getInt(ARG_PARAM1) else null

            kerambaId = if (it.getInt(ARG_PARAM2) > 0) it.getInt(ARG_PARAM2) else null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetBiotaBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomSheetBiota = this@BottomSheetBiota

        setupDropdown()

        setupObserver()
    }

    private fun setupObserver() {
        biotaId?.let {
            biotaViewModel.loadBiotaData(biotaId!!).observe(viewLifecycleOwner, { bind(it) })
        }

        biotaViewModel.requestPostAddResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                    binding.apply {
                        saveBiotaBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }
                }
                is NetworkResult.Loading -> {
                    binding.apply {
                        saveBiotaBtn.visibility = View.GONE

                        progressLoading.visibility = View.VISIBLE
                    }
                }
                is NetworkResult.Loaded -> {
                    binding.apply {
                        saveBiotaBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != ""){
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    if (biotaViewModel.selectedKerambaId.value != null) {
                        biotaViewModel.fetchBiota(biotaViewModel.selectedKerambaId.value!!)
                    }

                    biotaViewModel.donePostAddRequest()

                    this.dismiss()
                }
                is NetworkResult.Error -> {
                    binding.apply {
                        saveBiotaBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    biotaViewModel.donePostAddRequest()
                }
            }
        })

        biotaViewModel.requestPutUpdateResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                    binding.apply {
                        saveBiotaBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }
                }
                is NetworkResult.Loading -> {
                    binding.apply {
                        saveBiotaBtn.visibility = View.GONE

                        progressLoading.visibility = View.VISIBLE
                    }
                }
                is NetworkResult.Loaded -> {
                    binding.apply {
                        saveBiotaBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != ""){
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    biotaId?.let {
                        biotaViewModel.updateLocalBiota(
                            biotaId!!,
                            binding.jenisBiotaEt.text.toString().trim(),
                            binding.bobotBibitEt.text.toString(),
                            binding.panjangBibitEt.text.toString(),
                            binding.jumlahBibitEt.text.toString(),
                            if (binding.tanggalTebarEt.text.toString() != "") {
                                convertStringToDateLong(
                                    binding.tanggalTebarEt.text.toString(),
                                    "EEEE dd-MMM-yyyy"
                                )
                            } else {
                                0L
                            }
                        )
                    }

                    biotaViewModel.donePutUpdateRequest()

                    this.dismiss()
                }
                is NetworkResult.Error -> {
                    binding.apply {
                        saveBiotaBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }
                    biotaViewModel.donePutUpdateRequest()
                }
            }
        })
    }

    private fun bind(biotaDomain: BiotaDomain) {
        binding.apply {
            titleTv.text = biotaDomain.jenis_biota

            jenisBiotaEt.setText(biotaDomain.jenis_biota, TextView.BufferType.SPANNABLE)

            bobotBibitEt.setText(biotaDomain.bobot.toString(), TextView.BufferType.SPANNABLE)

            panjangBibitEt.setText(biotaDomain.panjang.toString(), TextView.BufferType.SPANNABLE)

            jumlahBibitEt.setText(
                biotaDomain.jumlah_bibit.toString(),
                TextView.BufferType.SPANNABLE
            )

            binding.tanggalTebarEt.setText(
                convertLongToDateString(biotaDomain.tanggal_tebar, "EEEE dd-MMM-yyyy"),
                TextView.BufferType.EDITABLE
            )

            saveBiotaBtn.text = getString(R.string.edit_biota)
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


    fun saveBiota() {
        binding.apply {
            if (isEntryValid(
                    jenisBiotaEt.text.toString().trim(),
                    bobotBibitEt.text.toString(),
                    panjangBibitEt.text.toString(),
                    jumlahBibitEt.text.toString(),
                    if (tanggalTebarEt.text.toString() != "") {
                        convertStringToDateLong(tanggalTebarEt.text.toString(), "EEEE dd-MMM-yyyy")
                    } else {
                        0L
                    }
                )
            ) {
                if (biotaId != null) {
                    biotaViewModel.updateBiota(
                        biotaId!!,
                        jenisBiotaEt.text.toString().trim(),
                        bobotBibitEt.text.toString(),
                        panjangBibitEt.text.toString(),
                        jumlahBibitEt.text.toString(),
                        if (tanggalTebarEt.text.toString() != "") {
                            convertStringToDateLong(
                                tanggalTebarEt.text.toString(),
                                "EEEE dd-MMM-yyyy"
                            )
                        } else {
                            0L
                        }
                    )
                } else{
                        biotaViewModel.insertBiota(
                            jenisBiotaEt.text.toString().trim(),
                            bobotBibitEt.text.toString(),
                            panjangBibitEt.text.toString(),
                            jumlahBibitEt.text.toString(),
                            if (tanggalTebarEt.text.toString() != "") {
                                convertStringToDateLong(
                                    tanggalTebarEt.text.toString(),
                                    "EEEE dd-MMM-yyyy"
                                )
                            } else {
                                0L
                            }
                        )
                    }
            } else {
                if (TextUtils.isEmpty(jenisBiotaEt.text)) {
                    jenisBiotaEt.error = "Jenis biota harus diisi!"
                }

                if (TextUtils.isEmpty(bobotBibitEt.text)) {
                    bobotBibitEt.error = "Bobot bibit harus diisi!"
                }

                if (TextUtils.isEmpty(panjangBibitEt.text)) {
                    panjangBibitEt.error = "Panjang bibit harus diisi!"
                }

                if (TextUtils.isEmpty(jumlahBibitEt.text)) {
                    jumlahBibitEt.error = "Banyaknya bibit harus diisi!"
                }

                if (TextUtils.isEmpty(tanggalTebarEt.text)) {
                    tanggalTebarEt.error = "Tanggal tebar harus diketahui!"
                }
            }
        }
    }

    fun showDatePicker() {
        if (childFragmentManager.findFragmentByTag("DatePicker") == null) {
            DatePickerFragment.create().show(childFragmentManager, "DatePicker")
        }
    }

    private fun isEntryValid(
        jenis: String,
        bobot: String,
        panjang: String,
        jumlah: String,
        tanggal: Long,
    ): Boolean {
        return biotaViewModel.isEntryValid(jenis, bobot, panjang, jumlah, tanggal)
    }

    private fun setupDropdown() {
        kerambaViewModel.getAllKeramba().observe(viewLifecycleOwner, { listKeramba ->

            mapKeramba =
                listKeramba.map { keramba -> keramba.nama_keramba to keramba.keramba_id }.toMap()

            val kerambaList = mapKeramba.keys.toList()

            val arrayAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kerambaList)

            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.kerambaDropdown.adapter = arrayAdapter


            kerambaId?.let {
                val kerambaIdlist: List<Int> = mapKeramba.values.toList()

                val index: Int = kerambaIdlist.indexOf(kerambaId!!)

                binding.kerambaDropdown.setSelection(index)
            }

            binding.kerambaDropdown.onItemSelectedListener = this@BottomSheetBiota
        })
    }

    override fun onItemSelected(parent: AdapterView<*>, p1: View?, pos: Int, id: Long) {
        val namaKeramba = parent.getItemAtPosition(pos)

        if (namaKeramba != null) {
            biotaViewModel.selectkerambaId(mapKeramba[namaKeramba]!!)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate: Calendar = Calendar.getInstance()
        selectedDate.set(year, month, dayOfMonth)

        binding.tanggalTebarEt.setText(
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
            BottomSheetBiota().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, biotaId)

                    putInt(ARG_PARAM2, kerambaId)
                }
            }
    }
}