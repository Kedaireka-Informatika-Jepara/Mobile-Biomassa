package com.cemebsa.biomassa.views.action

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.viewmodels.BiotaViewModel
import com.cemebsa.biomassa.views.add.BottomSheetBiota
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM1 = "biotaId"

private const val ARG_PARAM2 = "kerambaId"

@AndroidEntryPoint
class BottomSheetActionBiota : BottomSheetAction() {
    private val biotaViewModel by activityViewModels<BiotaViewModel>()

    private var kerambaId: Int? = 0

    private var biotaId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            biotaId = if (it.getInt(ARG_PARAM1) > 0) it.getInt(ARG_PARAM1) else null

            kerambaId = if (it.getInt(ARG_PARAM2) > 0) it.getInt(ARG_PARAM2) else null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        biotaViewModel.requestDeleteResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                }
                is NetworkResult.Loading -> {

                }
                is NetworkResult.Loaded -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    biotaId?.let { biotaViewModel.deleteLocalBiota(biotaId!!) }

                    biotaViewModel.doneDeleteRequest()

                    this@BottomSheetActionBiota.dismiss()
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    biotaViewModel.doneDeleteRequest()
                }
            }
        })
    }

    override fun showBottomSheetEdit() {
        super.showBottomSheetEdit()

        if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetBiota") == null) {
            if (biotaId != null && kerambaId != null) {
                val bottomSheetBiota =
                    BottomSheetBiota.newInstance(biotaId = biotaId!!, kerambaId = kerambaId!!)

                bottomSheetBiota.show(requireActivity().supportFragmentManager, "BottomSheetBiota")
            }

            this@BottomSheetActionBiota.dismiss()
        }
    }

    override fun showAlertDialogDelete() {
        super.showAlertDialogDelete()

        biotaId?.let {
            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Konfirmasi Hapus")

            builder.setMessage("Apa anda yakin untuk menghapus biota ini?")

            builder.setPositiveButton("Ya") { _, _ ->

                biotaViewModel.deleteBiota(biotaId!!)
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
        fun newInstance(biotaId: Int, kerambaId: Int) =
            BottomSheetActionBiota().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, biotaId)

                    putInt(ARG_PARAM2, kerambaId)
                }
            }
    }
}