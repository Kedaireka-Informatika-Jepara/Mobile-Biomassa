package com.cemebsa.biomassa.views.action

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.cemebsa.biomassa.models.network.enums.NetworkResult
import com.cemebsa.biomassa.viewmodels.FeedingDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM = "detailId"

@AndroidEntryPoint
class BottomSheetActionFeedingDetail: BottomSheetAction() {
    private val feedingDetailViewModel by activityViewModels<FeedingDetailViewModel>()

    private var detailId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            detailId = if (it.getInt(ARG_PARAM) > 0) it.getInt(ARG_PARAM) else null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        super.binding.editBtn.visibility = View.GONE

        feedingDetailViewModel.requestDeleteResult.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkResult.Initial -> {}
                is NetworkResult.Loading -> {}
                is NetworkResult.Loaded -> {
                    if (result.message != ""){
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    detailId?.let { feedingDetailViewModel.deleteLocalFeedingDetail(detailId!!) }

                    feedingDetailViewModel.doneDeleteRequest()

                    this@BottomSheetActionFeedingDetail.dismiss()
                }
                is NetworkResult.Error -> {
                    if (result.message != "") {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    feedingDetailViewModel.doneDeleteRequest()
                }
            }
        })


    }

    override fun showAlertDialogDelete() {
        super.showAlertDialogDelete()

        detailId?.let {
            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Konfirmasi Hapus")

            builder.setMessage("Apa anda yakin untuk menghapus data ini?")

            builder.setPositiveButton("Ya") { _, _ ->
                feedingDetailViewModel.deleteFeedingDetail(detailId!!)
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
        fun newInstance(detailId: Int) =
            BottomSheetActionFeedingDetail().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM, detailId)
                }
            }
    }
}