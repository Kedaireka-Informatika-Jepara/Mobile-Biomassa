package com.cemebsa.biomassa.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.abstractions.view.IOnItemClickListener
import com.cemebsa.biomassa.databinding.HeaderCardBinding
import com.cemebsa.biomassa.utils.convertLongToDateString

class HeaderCardAdapter(
    val tanggal: Long,
) : RecyclerView.Adapter<HeaderCardAdapter.HeaderViewHolder>() {

    lateinit var clickListener: IOnItemClickListener<Unit>

    inner class HeaderViewHolder(private val binding: HeaderCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {
                tanggalPemberianPakanTv.text = convertLongToDateString(tanggal, "EEEE dd-MMM-yyyy")

                editBtn.setOnClickListener { clickListener.onCLick() }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val withDataBinding: HeaderCardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.header_card,
            parent,
            false
        )
        return HeaderViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: HeaderCardAdapter.HeaderViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 1
    }
}