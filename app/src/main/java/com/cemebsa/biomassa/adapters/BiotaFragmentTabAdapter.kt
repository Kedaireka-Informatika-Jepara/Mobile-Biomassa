package com.cemebsa.biomassa.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cemebsa.biomassa.views.summary.biota.BiotaDataFragment
import com.cemebsa.biomassa.views.summary.biota.BiotaHitungFragment
import com.cemebsa.biomassa.views.summary.biota.BiotaInfoFragment

class BiotaFragmentTabAdapter constructor(fragment: Fragment,
                                          private val biotaId: Int,
                                          private val kerambaId: Int): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BiotaInfoFragment.newInstance(biotaId, kerambaId)
            1 -> BiotaDataFragment.newInstance(biotaId, kerambaId)
            else -> BiotaHitungFragment.newInstance(biotaId, kerambaId)
        }
    }
}