package com.cemebsa.biomassa.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.abstractions.view.IOnItemClickListener
import com.cemebsa.biomassa.databinding.ListBiotaBinding
import com.cemebsa.biomassa.models.domain.BiotaDomain

class BiotaListAdapter : ListAdapter<BiotaDomain, BiotaListAdapter.ViewHolder>(DiffCallBack) {

    lateinit var clickListener: IOnItemClickListener<BiotaDomain>

    object DiffCallBack: DiffUtil.ItemCallback<BiotaDomain>() {
        override fun areItemsTheSame(oldItem: BiotaDomain, newItem: BiotaDomain): Boolean {
            return oldItem.biota_id == newItem.biota_id
        }

        override fun areContentsTheSame(oldItem: BiotaDomain, newItem: BiotaDomain): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val withDataBinding: ListBiotaBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_biota,
            parent,
            false
        )

        return ViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val biota = getItem(position)
        holder.bind(biota)
    }

    inner class ViewHolder(private val binding: ListBiotaBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(biotaDomain: BiotaDomain){
            binding.apply {
                jenisBiotaTv.text = biotaDomain.jenis_biota

                jumlahBibitTv.text = biotaDomain.jumlah_bibit.toString()

                biotaCard.setOnClickListener { clickListener.onClick(biotaDomain) }

                biotaCard.setOnLongClickListener { clickListener.onLongClick(biotaDomain)}

                executePendingBindings()
            }
        }

    }
}