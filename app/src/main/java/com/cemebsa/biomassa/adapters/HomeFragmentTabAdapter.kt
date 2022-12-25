package com.cemebsa.biomassa.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cemebsa.biomassa.views.KerambaFragment
import com.cemebsa.biomassa.views.PakanFragment

class HomeFragmentTabAdapter constructor(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int =  2

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> KerambaFragment()
            else -> PakanFragment()
        }
    }
}