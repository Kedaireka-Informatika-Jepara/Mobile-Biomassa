package com.cemebsa.biomassa.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cemebsa.biomassa.R
import com.cemebsa.biomassa.databinding.ListPanenBinding
import com.cemebsa.biomassa.models.domain.PanenDomain
import com.cemebsa.biomassa.models.relation.PanenAndBiota
import com.cemebsa.biomassa.utils.convertLongToDateString


class PanenListAdapter : ListAdapter<PanenAndBiota, PanenListAdapter.ViewHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val withDataBinding: ListPanenBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_panen,
            parent,
            false
        )
        return ViewHolder(withDataBinding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ListPanenBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(panen: PanenAndBiota) {
            val entity = panen.panen

            val panenDomain = PanenDomain(
                activity_id = entity.activity_id,
                tanggal_panen = entity.tanggal_panen,
                panjang = entity.panjang,
                bobot = entity.bobot,
                jumlah_hidup = entity.jumlah_hidup,
                jumlah_mati = entity.jumlah_mati,
                //mortality_rate = entity.mortality_rate,
                biota_id = entity.biota_id,
                keramba_id = entity.keramba_id
            )

            binding.apply {
                jenisBiotaTv.text = panen.biota.jenis_biota

                ukuranBiotaTv.text = panenDomain.panjang.toString()

                bobotBiotaTv.text = panenDomain.bobot.toString()

                jumlahPanenBiotaTv.text = panenDomain.jumlah_hidup.toString()

                gagalPanenBiotaTv.text = panenDomain.jumlah_mati.toString()

                tanggalPanenBiotaTv.text =
                    convertLongToDateString(panenDomain.tanggal_panen, "EEEE dd-MMM-yyyy")

                executePendingBindings()
            }
        }

    }

    object DiffCallBack : DiffUtil.ItemCallback<PanenAndBiota>() {
        override fun areItemsTheSame(oldItem: PanenAndBiota, newItem: PanenAndBiota): Boolean {
            return oldItem.panen.activity_id == newItem.panen.activity_id
        }

        override fun areContentsTheSame(oldItem: PanenAndBiota, newItem: PanenAndBiota): Boolean {
            return oldItem == newItem
        }
    }
}