package com.cemebsa.biomassa.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.abstractions.view.IOnItemClickListener
import com.cemebsa.biomassa.databinding.HeaderButtonBinding

class HeaderButtonAdapter (): RecyclerView.Adapter<HeaderButtonAdapter.HeaderViewHolder>() {

    lateinit var clickListener: IOnItemClickListener<Unit>

    inner class HeaderViewHolder(private val binding: HeaderButtonBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind() {
            binding.addBtn.setOnClickListener { clickListener.onCLick() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val withDataBinding: HeaderButtonBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.header_button,
            parent,
            false
        )
        return HeaderViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 1
    }
}