package com.cemebsa.biomassa.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.abstractions.view.IOnItemClickListener
import com.cemebsa.biomassa.databinding.ListPakanBinding
import com.cemebsa.biomassa.models.domain.PakanDomain

class PakanListAdapter: ListAdapter<PakanDomain, PakanListAdapter.ViewHolder>(DiffCallBack){

    lateinit var clickListener: IOnItemClickListener<PakanDomain>

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val withDataBinding: ListPakanBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_pakan,
            parent,
            false
        )
        return ViewHolder(withDataBinding)
    }

    inner class ViewHolder(val binding: ListPakanBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(pakan: PakanDomain){
            binding.apply {
                jenisPakanTv.text = pakan.jenis_pakan

                pakanCard.setOnClickListener { clickListener.onClick(pakan) }

                pakanCard.setOnLongClickListener { clickListener.onLongClick(pakan) }

                executePendingBindings()
            }
        }

    }

    object DiffCallBack: DiffUtil.ItemCallback<PakanDomain>() {
        override fun areItemsTheSame(oldItem: PakanDomain, newItem: PakanDomain): Boolean {
            return oldItem.pakan_id == newItem.pakan_id
        }

        override fun areContentsTheSame(oldItem: PakanDomain, newItem: PakanDomain): Boolean {
            return oldItem == newItem
        }
    }

}