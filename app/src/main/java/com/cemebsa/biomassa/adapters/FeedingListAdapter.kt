package com.cemebsa.biomassa.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.abstractions.view.IOnItemClickListener
import com.cemebsa.biomassa.databinding.ListFeedingBinding
import com.cemebsa.biomassa.models.domain.FeedingDomain
import com.cemebsa.biomassa.utils.convertLongToDateString

class FeedingListAdapter: ListAdapter<FeedingDomain, FeedingListAdapter.ViewHolder>(DiffCallBack) {

    lateinit var clickListener: IOnItemClickListener<FeedingDomain>

    object DiffCallBack: DiffUtil.ItemCallback<FeedingDomain>() {
        override fun areItemsTheSame(oldItem: FeedingDomain, newItem: FeedingDomain): Boolean {
            return oldItem.feeding_id == newItem.feeding_id
        }

        override fun areContentsTheSame(oldItem: FeedingDomain, newItem: FeedingDomain): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val withDataBinding: ListFeedingBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_feeding,
            parent,
            false
        )

        return ViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feeding = getItem(position)
        holder.bind(feeding)
    }

    inner class ViewHolder(private val binding: ListFeedingBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(feedingDomain: FeedingDomain){
            binding.apply {
                tanggalFeedingTv.text = convertLongToDateString(feedingDomain.tanggal_feeding, "EEEE dd-MMM-yyyy")

                feedingCard.setOnClickListener { clickListener.onClick(feedingDomain) }

                feedingCard.setOnLongClickListener { clickListener.onLongClick(feedingDomain) }

                executePendingBindings()
            }
        }

    }
}