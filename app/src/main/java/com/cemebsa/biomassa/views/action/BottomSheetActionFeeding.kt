package com.cemebsa.biomassa.views.action

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.viewmodels.FeedingViewModel
import com.cemebsa.biomassa.views.add.BottomSheetFeeding
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM1 = "feedingId"

private const val ARG_PARAM2 = "kerambaId"

@AndroidEntryPoint
class BottomSheetActionFeeding : BottomSheetAction() {
    private val feedingViewModel by activityViewModels<FeedingViewModel>()

    private var kerambaId: Int? = 0

    private var feedingId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            feedingId = if (it.getInt(ARG_PARAM1) > 0) it.getInt(ARG_PARAM1) else null

            kerambaId = if (it.getInt(ARG_PARAM2) > 0) it.getInt(ARG_PARAM2) else null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feedingViewModel.requestDeleteResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {
                }
                is NetworkResult.Loading -> {
                }
                is NetworkResult.Loaded -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    feedingId?.let { feedingViewModel.deleteLocalFeeding(feedingId!!) }

                    feedingViewModel.doneDeleteRequest()

                    this@BottomSheetActionFeeding.dismiss()
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    feedingViewModel.doneDeleteRequest()
                }
            }
        })
    }

    override fun showBottomSheetEdit() {
        super.showBottomSheetEdit()

        if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetFeeding") == null) {
            if (feedingId != null && kerambaId != null) {
                val bottomSheetFeeding =
                    BottomSheetFeeding.newInstance(feedingId = feedingId!!, kerambaId = kerambaId!!)

                bottomSheetFeeding.show(
                    requireActivity().supportFragmentManager,
                    "BottomSheetFeeding"
                )
            }

            this@BottomSheetActionFeeding.dismiss()
        }
    }

    override fun showAlertDialogDelete() {
        super.showAlertDialogDelete()

        feedingId?.let {
            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Konfirmasi Hapus")

            builder.setMessage("Apa anda yakin untuk menghapus pemberian pakan ini?")

            builder.setPositiveButton("Ya") { _, _ ->

                feedingViewModel.deleteFeeding(feedingId!!)
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
        fun newInstance(feedingId: Int, kerambaId: Int) =
            BottomSheetActionFeeding().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, feedingId)

                    putInt(ARG_PARAM2, kerambaId)
                }
            }
    }
}