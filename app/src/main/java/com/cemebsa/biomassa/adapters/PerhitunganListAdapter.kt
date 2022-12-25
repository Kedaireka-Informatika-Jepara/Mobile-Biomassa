package com.cemebsa.biomassa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.abstractions.view.IOnItemClickListener
import com.cemebsa.biomassa.databinding.ListPengukuranBinding
import com.cemebsa.biomassa.databinding.ListPerhitunganBinding
import com.cemebsa.biomassa.models.domain.PengukuranDomain
import com.cemebsa.biomassa.models.domain.PerhitunganDomain
import com.cemebsa.biomassa.utils.convertLongToDateString

class PerhitunganListAdapter: ListAdapter<PerhitunganDomain, PerhitunganListAdapter.ViewHolder>(DiffCallBack) {

    lateinit var clickListener: IOnItemClickListener<PerhitunganDomain>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val withDataBinding: ListPerhitunganBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_perhitungan,
            parent,
            false
        )

        return ViewHolder(withDataBinding, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ListPerhitunganBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(perhitungan: PerhitunganDomain) {
            binding.apply {
                jumlahHidupTv.text = perhitungan.hidup.toString()

                jumlahMatiTv.text = perhitungan.mati.toString()

                tanggalHitungTv.text = context.getString(
                    R.string.tanggal_perhitungan,
                    convertLongToDateString(perhitungan.tanggal_hitung, "EEEE dd-MMM-yyyy")
                )

                perhitunganCard.setOnLongClickListener { clickListener.onLongClick(perhitungan) }

                executePendingBindings()
            }
        }
    }

    object DiffCallBack : DiffUtil.ItemCallback<PerhitunganDomain>() {
        override fun areItemsTheSame(
            oldItem: PerhitunganDomain,
            newItem: PerhitunganDomain
        ): Boolean {
            return oldItem.perhitungan_id == oldItem.perhitungan_id
        }

        override fun areContentsTheSame(
            oldItem: PerhitunganDomain,
            newItem: PerhitunganDomain
        ): Boolean {
            return oldItem == newItem
        }
    }
}