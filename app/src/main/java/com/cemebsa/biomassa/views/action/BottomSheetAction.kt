package com.cemebsa.biomassa.views.action

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.databinding.BottomSheetActionBinding

open class BottomSheetAction: BottomSheetDialogFragment() {

    open lateinit var binding: BottomSheetActionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetActionBinding.inflate(inflater)

        binding.bottomSheetAction = this@BottomSheetAction

        binding.lifecycleOwner = viewLifecycleOwner

        binding.apply {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    editBtn.setIconTintResource(R.color.white)
                    editBtn.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    editBtn.setIconTintResource(R.color.black)
                    editBtn.setTextColor(ContextCompat.getColor(context!!, R.color.black))
                }
            }
        }

        return binding.root
    }

    open fun showBottomSheetEdit(){}

    open fun showAlertDialogDelete(){}
}