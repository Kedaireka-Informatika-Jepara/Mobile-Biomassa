package com.cemebsa.biomassa.views.action

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.viewmodels.PakanViewModel
import com.cemebsa.biomassa.views.add.BottomSheetPakan
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM = "pakanId"

@AndroidEntryPoint
class BottomSheetActionPakan : BottomSheetAction() {
    private val pakanViewModel by activityViewModels<PakanViewModel>()

    private var pakanId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            pakanId = if (it.getInt(ARG_PARAM) > 0) it.getInt(ARG_PARAM) else null
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pakanViewModel.requestDeleteResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                }
                is NetworkResult.Loading -> {
                }
                is NetworkResult.Loaded -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    pakanId?.let { pakanViewModel.deleteLocalPakan(pakanId!!) }

                    pakanViewModel.doneDeleteRequest()

                    this@BottomSheetActionPakan.dismiss()
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    pakanViewModel.doneDeleteRequest()
                }
            }
        })
    }

    override fun showBottomSheetEdit() {
        super.showBottomSheetEdit()

        if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetPakan") == null) {
            pakanId?.let {
                val bottomSheetPakan = BottomSheetPakan.newInstance(pakanId = pakanId!!)

                bottomSheetPakan.show(
                    requireActivity().supportFragmentManager,
                    "BottomSheetPakan"
                )
            }

            this@BottomSheetActionPakan.dismiss()
        }
    }

    override fun showAlertDialogDelete() {
        super.showAlertDialogDelete()

        pakanId?.let {
            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Konfirmasi Hapus")

            builder.setMessage("Apa anda yakin untuk menghapus pakan ini?")

            builder.setPositiveButton("Ya") { _, _ ->

                pakanViewModel.deletePakan(pakanId!!)
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
        fun newInstance(pakanId: Int) =
            BottomSheetActionPakan().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM, pakanId)
                }
            }
    }
}