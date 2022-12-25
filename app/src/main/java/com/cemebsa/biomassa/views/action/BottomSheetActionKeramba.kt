package com.cemebsa.biomassa.views.action

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.viewmodels.KerambaViewModel
import com.cemebsa.biomassa.views.add.BottomSheetKeramba
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM = "kerambaId"

@AndroidEntryPoint
class BottomSheetActionKeramba : BottomSheetAction() {
    private val kerambaViewModel by activityViewModels<KerambaViewModel>()

    private var kerambaId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            kerambaId = if (it.getInt(ARG_PARAM) > 0) it.getInt(ARG_PARAM) else null
        }
    }

    override fun showBottomSheetEdit() {
        super.showBottomSheetEdit()

        if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetKeramba") == null) {
            kerambaId?.let {
                val bottomSheetKeramba = BottomSheetKeramba.newInstance(kerambaId!!)

                bottomSheetKeramba.show(
                    requireActivity().supportFragmentManager,
                    "BottomSheetKeramba"
                )
            }

            this@BottomSheetActionKeramba.dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kerambaViewModel.requestDeleteResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                }
                is NetworkResult.Loading -> {
                }
                is NetworkResult.Loaded -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    kerambaId?.let { kerambaViewModel.deleteLocalKeramba(kerambaId!!) }

                    kerambaViewModel.doneDeleteRequest()

                    this@BottomSheetActionKeramba.dismiss()
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()

                        kerambaViewModel.doneToastException()
                    }

                    kerambaViewModel.doneDeleteRequest()
                }
            }
        })
    }

    override fun showAlertDialogDelete() {
        super.showAlertDialogDelete()

        kerambaId?.let {
            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Konfirmasi Hapus")

            builder.setMessage("Apa anda yakin untuk menghapus keramba ini?")

            builder.setPositiveButton("Ya") { _, _ ->

                kerambaViewModel.deleteKeramba(kerambaId!!)
            }

            builder.setNegativeButton("Batal") { _, _ -> }

            builder.show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment.
         */
        @JvmStatic
        fun newInstance(kerambaId: Int) =
            BottomSheetActionKeramba().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM, kerambaId)
                }
            }
    }
}