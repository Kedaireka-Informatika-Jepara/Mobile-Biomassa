package com.cemebsa.biomassa.views.add

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.databinding.BottomSheetPakanBinding
import com.cemebsa.biomassa.models.domain.PakanDomain
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.viewmodels.PakanViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM = "pakanId"

@AndroidEntryPoint
class BottomSheetPakan : BottomSheetDialogFragment() {
    private val pakanViewModel by activityViewModels<PakanViewModel>()

    private lateinit var binding: BottomSheetPakanBinding

    private var pakanId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            pakanId = if (it.getInt(ARG_PARAM) > 0) it.getInt(ARG_PARAM) else null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetPakanBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomSheetPakan = this@BottomSheetPakan

        setupObserver()
    }

    private fun bind(pakan: PakanDomain) {
        binding.apply {
            titleTv.text = pakan.jenis_pakan

            namaPakanEt.setText(pakan.jenis_pakan, TextView.BufferType.SPANNABLE)

            deskripsiPakanEt.setText(pakan.deskripsi, TextView.BufferType.SPANNABLE)

            savePakanBtn.text = getString(R.string.update_pakan)
        }
    }

    private fun setupObserver() {
        pakanId?.let {
            pakanViewModel.loadPakanData(pakanId!!).observe(viewLifecycleOwner, {
                bind(it)
            })
        }

        pakanViewModel.requestPostAddResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                    binding.apply {
                        savePakanBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }
                }
                is NetworkResult.Loading -> {
                    binding.apply {
                        savePakanBtn.visibility = View.GONE

                        progressLoading.visibility = View.VISIBLE
                    }
                }
                is NetworkResult.Loaded -> {
                    binding.apply {
                        savePakanBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    pakanViewModel.fetchPakan()

                    pakanViewModel.donePostAddRequest()

                    this.dismiss()
                }
                is NetworkResult.Error -> {
                    binding.apply {
                        savePakanBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    pakanViewModel.donePostAddRequest()
                }
            }
        })

        pakanViewModel.requestPutUpdateResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                    binding.apply {
                        savePakanBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }
                }
                is NetworkResult.Loading -> {
                    binding.apply {
                        savePakanBtn.visibility = View.GONE

                        progressLoading.visibility = View.VISIBLE
                    }
                }
                is NetworkResult.Loaded -> {
                    binding.apply {
                        savePakanBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    pakanId?.let {
                        pakanViewModel.updateLocalPakan(
                            pakanId!!,
                            binding.namaPakanEt.text.toString().trim(),
                            binding.deskripsiPakanEt.text.toString().trim()
                        )
                    }

                    pakanViewModel.donePutUpdateRequest()

                    this.dismiss()
                }
                is NetworkResult.Error -> {
                    binding.apply {
                        savePakanBtn.visibility = View.VISIBLE

                        progressLoading.visibility = View.GONE
                    }

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    pakanViewModel.donePutUpdateRequest()
                }
            }
        })
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

    fun savePakan() {
        binding.apply {
            if (isEntryValid(
                    namaPakanEt.text.toString().trim(),
                )
            ) {
                if (pakanId != null) {
                    pakanViewModel.updatePakan(
                        pakanId!!,
                        namaPakanEt.text.toString().trim(),
                        deskripsiPakanEt.text.toString().trim()
                    )
                } else {
                    pakanViewModel.insertPakan(
                        namaPakanEt.text.toString().trim(),
                        deskripsiPakanEt.text.toString().trim()
                    )
                }
            } else {
                if (TextUtils.isEmpty(namaPakanEt.text)) {
                    namaPakanEt.error = "Nama pakan harus diisi!"
                }
            }
        }
    }

    private fun isEntryValid(jenis_pakan: String): Boolean {
        return pakanViewModel.isEntryValid(jenis_pakan)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment.
         */
        @JvmStatic
        fun newInstance(pakanId: Int) =
            BottomSheetPakan().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM, pakanId)
                }
            }
    }
}