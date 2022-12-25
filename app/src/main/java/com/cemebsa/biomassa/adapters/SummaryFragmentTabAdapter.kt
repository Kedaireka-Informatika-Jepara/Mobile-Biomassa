package com.cemebsa.biomassa.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cemebsa.biomassa.views.summary.biota.BiotaFragment
import com.cemebsa.biomassa.views.summary.keramba.InfoFragment
import com.cemebsa.biomassa.views.summary.pakan.FeedingFragment
import com.cemebsa.biomassa.views.summary.panen.PanenFragment


class SummaryFragmentTabAdapter constructor(fragment: Fragment, private val kerambaId: Int): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int =  4

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> InfoFragment.newInstance(kerambaId)
            1 -> BiotaFragment.newInstance(kerambaId)
            2 -> FeedingFragment.newInstance(kerambaId)
            else -> PanenFragment.newInstance(kerambaId)
        }
    }
}