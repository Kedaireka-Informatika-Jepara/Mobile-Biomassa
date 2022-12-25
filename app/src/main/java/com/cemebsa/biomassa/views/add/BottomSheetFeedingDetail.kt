package com.cemebsa.biomassa.views.add

import android.annotation.SuppressLint
import android.app.TimePickerDialog
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
import com.cemebsa.biomassa.databinding.BottomSheetFeedingDetailBinding
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.utils.convertStringToDateLong
import com.cemebsa.biomassa.viewmodels.FeedingDetailViewModel
import com.cemebsa.biomassa.viewmodels.PakanViewModel
import com.cemebsa.biomassa.views.components.TimePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

private const val ARG_PARAM = "feedingId"

@AndroidEntryPoint
class BottomSheetFeedingDetail : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: BottomSheetFeedingDetailBinding

    private val pakanViewModel by activityViewModels<PakanViewModel>()

    private val feedingDetailViewModel by activityViewModels<FeedingDetailViewModel>()

    private lateinit var mapPakan: Map<String, Int>

    private var feedingId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            feedingId = if (it.getInt(ARG_PARAM) > 0) it.getInt(ARG_PARAM) else null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetFeedingDetailBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomSheetFeedingDetail = this@BottomSheetFeedingDetail

        setupObserver()

        setupDropdown()
    }

    private fun setupObserver() {
        feedingDetailViewModel.requestPostAddResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                    binding.apply {
                        saveFeedingDetailBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }
                }
                is NetworkResult.Loading -> {
                    binding.apply {
                        saveFeedingDetailBtn.visibility = View.GONE

                        progressLoading.visibility = View.VISIBLE
                    }
                }
                is NetworkResult.Loaded -> {
                    binding.apply {
                        saveFeedingDetailBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    feedingId?.let { feedingDetailViewModel.fetchFeedingDetail(feedingId!!) }

                    feedingDetailViewModel.donePostAddRequest()

                    this.dismiss()
                }
                is NetworkResult.Error -> {
                    binding.apply {
                        saveFeedingDetailBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    feedingDetailViewModel.donePostAddRequest()
                }
            }
        })
    }

    private fun setupDropdown() {
        pakanViewModel.getAll().observe(viewLifecycleOwner, { listPakan ->
            mapPakan = listPakan.map { it.jenis_pakan to it.pakan_id }.toMap()

            val pakanList = mapPakan.keys.toList()

            val arrayAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, pakanList)

            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.pakanDropdown.adapter = arrayAdapter

            binding.pakanDropdown.onItemSelectedListener = this@BottomSheetFeedingDetail

        })
    }

    fun showTimePicker() {
        if (childFragmentManager.findFragmentByTag("TimePicker") == null) {
            TimePickerFragment.create().show(childFragmentManager, "TimePicker")
        }
    }

    fun saveFeedingDetail() {
        if (isEntryValid(
                binding.ukuranTebarEt.text.toString(),
                binding.banyakPakanEt.text.toString(),
                binding.jamFeedingEt.text.toString()
            )
        ) {
            feedingId?.let {
                feedingDetailViewModel.insertFeedingDetail(
                    feedingId!!,
                    binding.ukuranTebarEt.text.toString(),
                    binding.banyakPakanEt.text.toString(),
                    convertStringToDateLong(binding.jamFeedingEt.text.toString(), "H:m")
                )
            }
        } else {
            if (TextUtils.isEmpty(binding.ukuranTebarEt.text.toString())) {
                binding.ukuranTebarEt.error = "Ukuran Tebar Harus Diisi!"
            }
            if (TextUtils.isEmpty(binding.banyakPakanEt.text.toString())) {
                binding.banyakPakanEt.error = "Banyak Pakan Harus Diisi!"
            }
            if (TextUtils.isEmpty(binding.jamFeedingEt.text.toString())) {
                binding.jamFeedingEt.error = "Jam Tebar Harus Diisi!"
            }
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

    override fun onItemSelected(parent: AdapterView<*>, p1: View?, pos: Int, id: Long) {
        val namaPakan = parent.getItemAtPosition(pos)

        if (namaPakan != null) {
            feedingDetailViewModel.selectPakanId(mapPakan[namaPakan]!!)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    @SuppressLint("SetTextI18n")
    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        binding.jamFeedingEt.setText(
            "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}",
            TextView.BufferType.EDITABLE
        )
    }

    fun isEntryValid(ukuran: String,kuantitas: String, tanggal: String): Boolean {
        return feedingDetailViewModel.isEntryValid(ukuran, kuantitas, tanggal)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment.
         */
        @JvmStatic
        fun newInstance(feedingId: Int) =
            BottomSheetFeedingDetail().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM, feedingId)
                }
            }
    }
}