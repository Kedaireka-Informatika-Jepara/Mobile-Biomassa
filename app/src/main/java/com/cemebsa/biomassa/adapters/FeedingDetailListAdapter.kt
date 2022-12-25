package com.cemebsa.biomassa.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.abstractions.view.IOnItemClickListener
import com.cemebsa.biomassa.databinding.ListFeedingDetailBinding
import com.cemebsa.biomassa.models.domain.FeedingDetailDomain
import com.cemebsa.biomassa.models.relation.FeedingDetailAndPakan
import com.cemebsa.biomassa.utils.convertLongToDateString

class FeedingDetailListAdapter: ListAdapter<FeedingDetailAndPakan, FeedingDetailListAdapter.ViewHolder>(DiffCallBack) {

    lateinit var clickListener: IOnItemClickListener<FeedingDetailDomain>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val withDataBinding: ListFeedingDetailBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_feeding_detail,
            parent,
            false
        )
        return ViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ListFeedingDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(feedingDetailAndPakan: FeedingDetailAndPakan) {

            val feedingDetailDomain = FeedingDetailDomain(
                detail_id = feedingDetailAndPakan.feedingDetail.detail_id,
                feeding_id = feedingDetailAndPakan.feedingDetail.feeding_id,
                ukuran_tebar = feedingDetailAndPakan.feedingDetail.ukuran_tebar,
                banyak_pakan = feedingDetailAndPakan.feedingDetail.banyak_pakan,
                pakan_id = feedingDetailAndPakan.feedingDetail.pakan_id,
                waktu_feeding = feedingDetailAndPakan.feedingDetail.waktu_feeding
            )
            val pakan = feedingDetailAndPakan.pakan

            binding.apply {
                jenisPakanTv.text = pakan.jenis_pakan

                ukuranTebarTv.text = feedingDetailDomain.ukuran_tebar.toString()

                banyakPakanTv.text = feedingDetailDomain.banyak_pakan.toString()

                jamPakanTv.text =
                    convertLongToDateString(feedingDetailDomain.waktu_feeding, "H:m")

                detailCard.setOnLongClickListener { clickListener.onLongClick(feedingDetailDomain) }

                executePendingBindings()
            }
        }
    }

    object DiffCallBack : DiffUtil.ItemCallback<FeedingDetailAndPakan>() {
        override fun areItemsTheSame(
            oldItem: FeedingDetailAndPakan,
            newItem: FeedingDetailAndPakan
        ): Boolean {
            return oldItem.feedingDetail.detail_id == newItem.feedingDetail.detail_id
        }

        override fun areContentsTheSame(
            oldItem: FeedingDetailAndPakan,
            newItem: FeedingDetailAndPakan
        ): Boolean {
            return oldItem == newItem
        }
    }
}