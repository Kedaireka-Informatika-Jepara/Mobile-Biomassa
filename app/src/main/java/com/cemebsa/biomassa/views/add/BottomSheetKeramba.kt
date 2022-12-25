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
import android.widget.DatePicker
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.databinding.BottomSheetKerambaBinding
import com.cemebsa.biomassa.models.domain.KerambaDomain
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.utils.convertLongToDateString
import com.cemebsa.biomassa.utils.convertStringToDateLong
import com.cemebsa.biomassa.viewmodels.KerambaViewModel
import com.cemebsa.biomassa.views.components.DatePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

private const val ARG_PARAM = "kerambaId"

@AndroidEntryPoint
class BottomSheetKeramba : BottomSheetDialogFragment(), DatePickerDialog.OnDateSetListener {
    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private lateinit var binding: BottomSheetKerambaBinding

    private var kerambaId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            kerambaId = if (it.getInt(ARG_PARAM) > 0) it.getInt(ARG_PARAM) else null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetKerambaBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomSheetKeramba = this@BottomSheetKeramba

        setupObserver()
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

    fun onSaveKerambaBtnClicked() {
        binding.apply {
            if (isEntryValid(
                    namaKerambaEt.text.toString().trim(),
                    ukuranKerambaEt.text.toString(),
                    if (tanggalInstallEt.text.toString() != "") {
                        convertStringToDateLong(
                            tanggalInstallEt.text.toString(),
                            "EEEE dd-MMM-yyyy"
                        )
                    } else {
                        0L
                    }
                )
            ) {
                if (kerambaId != null) {
                    updateKeramba(
                        kerambaId!!,
                        namaKerambaEt.text.toString().trim(),
                        ukuranKerambaEt.text.toString(),
                        if (tanggalInstallEt.text.toString() != "") {
                            convertStringToDateLong(
                                tanggalInstallEt.text.toString(),
                                "EEEE dd-MMM-yyyy"
                            )
                        } else {
                            0L
                        }
                    )
                } else {
                    insertKeramba(
                        namaKerambaEt.text.toString().trim(),
                        ukuranKerambaEt.text.toString(),
                        if (tanggalInstallEt.text.toString() != "") {
                            convertStringToDateLong(
                                tanggalInstallEt.text.toString(),
                                "EEEE dd-MMM-yyyy"
                            )
                        } else {
                            0L
                        }
                    )
                }
            } else {
                if (TextUtils.isEmpty(namaKerambaEt.text)) {
                    namaKerambaEt.error = "Nama keramba harus diisi!"
                }

                if (TextUtils.isEmpty(ukuranKerambaEt.text)) {
                    ukuranKerambaEt.error = "Ukuran keramba harus diisi!"
                }

                if (TextUtils.isEmpty(tanggalInstallEt.text)) {
                    tanggalInstallEt.error = "Tanggal install harus diketahui!"
                }
            }
        }
    }

    private fun updateKeramba(id: Int, nama_keramba: String, ukuran: String, tanggal: Long) {
        kerambaViewModel.updateKeramba(id, nama_keramba, ukuran, tanggal)
    }

    private fun bind(kerambaDomain: KerambaDomain) {
        binding.apply {
            titleTv.text = kerambaDomain.nama_keramba

            namaKerambaEt.setText(kerambaDomain.nama_keramba, TextView.BufferType.SPANNABLE)

            ukuranKerambaEt.setText(kerambaDomain.ukuran.toString(), TextView.BufferType.SPANNABLE)

            binding.tanggalInstallEt.setText(
                convertLongToDateString(kerambaDomain.tanggal_install, "EEEE dd-MMM-yyyy"),
                TextView.BufferType.EDITABLE
            )

            saveKerambaBtn.text = getString(R.string.update_keramba)
        }
    }

    private fun setupObserver() {
        kerambaId?.let {
            kerambaViewModel.loadKerambaData(kerambaId!!)
                .observe(viewLifecycleOwner, { bind(kerambaDomain = it) })
        }

        kerambaViewModel.requestPostAddResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                    binding.apply {
                        saveKerambaBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }
                }
                is NetworkResult.Loading -> {
                    binding.apply {
                        saveKerambaBtn.visibility = View.GONE

                        progressLoading.visibility = View.VISIBLE
                    }
                }
                is NetworkResult.Loaded -> {
                    binding.apply {
                        saveKerambaBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    kerambaViewModel.fetchKeramba()

                    kerambaViewModel.donePostAddRequest()

                    this.dismiss()
                }
                is NetworkResult.Error -> {
                    binding.apply {
                        saveKerambaBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    kerambaViewModel.donePostAddRequest()
                }
            }
        })

        kerambaViewModel.requestPutUpdateResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                    binding.apply {
                        saveKerambaBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }
                }
                is NetworkResult.Loading -> {
                    binding.apply {
                        saveKerambaBtn.visibility = View.GONE

                        progressLoading.visibility = View.VISIBLE
                    }
                }
                is NetworkResult.Loaded -> {
                    binding.apply {
                        saveKerambaBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    kerambaId?.let {
                        kerambaViewModel.updateLocalKeramba(
                            kerambaId!!,
                            binding.namaKerambaEt.text.toString().trim(),
                            binding.ukuranKerambaEt.text.toString(),
                            if (binding.tanggalInstallEt.text.toString() != "") {
                                convertStringToDateLong(
                                    binding.tanggalInstallEt.text.toString(),
                                    "EEEE dd-MMM-yyyy"
                                )
                            } else {
                                0L
                            }
                        )
                    }

                    kerambaViewModel.donePutUpdateRequest()

                    this.dismiss()
                }
                is NetworkResult.Error -> {
                    binding.apply {
                        saveKerambaBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    kerambaViewModel.donePutUpdateRequest()
                }
            }
        })
    }

    fun showDatePicker() {
        if (childFragmentManager.findFragmentByTag("DatePicker") == null) {
            DatePickerFragment.create().show(childFragmentManager, "DatePicker")
        }
    }

    private fun isEntryValid(nama: String, ukuran: String, tanggal: Long): Boolean {
        return kerambaViewModel.isEntryValid(nama, ukuran, tanggal)
    }

    private fun insertKeramba(nama: String, ukuran: String, tanggal: Long) {
        kerambaViewModel.insertKeramba(nama, ukuran, tanggal)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate: Calendar = Calendar.getInstance()
        selectedDate.set(year, month, dayOfMonth)

        binding.tanggalInstallEt.setText(
            convertLongToDateString(selectedDate.timeInMillis, "EEEE dd-MMM-yyyy"),
            TextView.BufferType.EDITABLE
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment BiotaFragment.
         */
        @JvmStatic
        fun newInstance(kerambaId: Int) =
            BottomSheetKeramba().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM, kerambaId)
                }
            }
    }
}