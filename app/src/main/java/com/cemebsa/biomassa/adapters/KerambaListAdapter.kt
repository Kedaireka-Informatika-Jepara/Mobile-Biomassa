package com.cemebsa.biomassa.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.abstractions.view.IOnItemClickListener
import com.cemebsa.biomassa.databinding.ListKerambaBinding
import com.cemebsa.biomassa.models.domain.KerambaDomain

class KerambaListAdapter: ListAdapter<KerambaDomain, KerambaListAdapter.ViewHolder>(DiffCallBack), Filterable {

    lateinit var clickListener: IOnItemClickListener<KerambaDomain>

    private var list = mutableListOf<KerambaDomain>()

    fun setData(list: List<KerambaDomain>?){
        this.list.clear()
        this.list.addAll(list!!)
        submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val withDataBinding: ListKerambaBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_keramba,
            parent,
            false
        )
        return ViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val keramba = getItem(position)

        holder.bind(keramba)
    }

    inner class ViewHolder(private val listKerambaBinding: ListKerambaBinding):
    RecyclerView.ViewHolder(listKerambaBinding.root){

        fun bind(kerambaDomain: KerambaDomain){
            listKerambaBinding.apply {
                namaKerambaTv.text = kerambaDomain.nama_keramba

                kerambaCard.setOnClickListener { clickListener.onClick(kerambaDomain) }

                kerambaCard.setOnLongClickListener { clickListener.onLongClick(kerambaDomain) }

                executePendingBindings()
            }
        }
    }

    object DiffCallBack: DiffUtil.ItemCallback<KerambaDomain>() {
        override fun areItemsTheSame(oldItem: KerambaDomain, newItem: KerambaDomain): Boolean {
            return oldItem.keramba_id == newItem.keramba_id
        }

        override fun areContentsTheSame(oldItem: KerambaDomain, newItem: KerambaDomain): Boolean {
            return oldItem == newItem
        }
    }

    override fun getFilter(): Filter {
        return customFilter
    }

    private val customFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            val filteredList = mutableListOf<KerambaDomain>()

            if (constraint == null || constraint.isEmpty()){
                filteredList.clear()
                filteredList.addAll(list)
            } else {
                filteredList.clear()
                for (keramba in list){
                    if (keramba.nama_keramba.lowercase().contains(constraint.toString().lowercase())){
                        filteredList.add(keramba)
                    }
                }
            }
            results.values = filteredList

            return results
        }
        @Suppress("UNCHECKED_CAST")
        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            submitList(p1?.values as MutableList<KerambaDomain>)
        }
    }
}


