package com.cemebsa.biomassa.views.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.databinding.BottomSheetAddBinding

class BottomSheetAdd : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetAddBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAddBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomSheetAdd = this@BottomSheetAdd
    }

    fun launchBottomSheet(view: View) {
        when (view.id) {
            R.id.add_keramba_btn -> {
                if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetKeramba") == null) {
                    BottomSheetKeramba.newInstance(kerambaId = 0).show(requireActivity().supportFragmentManager, "BottomSheetKeramba")
                }
            }
            R.id.add_pakan_btn -> {
                if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetPakan") == null) {
                    BottomSheetPakan.newInstance(pakanId = 0).show(requireActivity().supportFragmentManager, "BottomSheetPakan")
                }
            }
            R.id.add_biota_btn -> {
                if (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheetBiota") == null) {
                    BottomSheetBiota.newInstance(biotaId = 0, kerambaId = 0).show(requireActivity().supportFragmentManager, "BottomSheetBiota")
                }
            }
        }
    }
}