package com.mocii.simuladorprestamos.ui.prestamo.master

import Cuota
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mocii.simuladorprestamos.databinding.RowItemInstallmentBinding
import com.mocii.simuladorprestamos.model.Current


class CuotasAdapter(val context: Context?, var items: List<Cuota> = ArrayList()) : RecyclerView.Adapter<ViewHolderCuota>() {

    interface OnItemClickListener {
        fun onItemClick(buyItem: Cuota, container: View)
    }

    private var onItemClickListener: OnItemClickListener? = null
    private var backgroundColorDefault = 0
    var titleBy = ""
    var colorPrimary = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCuota {
        return ViewHolderCuota(RowItemInstallmentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolderCuota, position: Int) {
        val item = items[position]
        holder.tvCuotaNumero.text = ""+item.numero
        holder.tvCuotaFechaPago.text = ""+item.fechaVencimiento
        holder.tvCuotaTotal.text = Current.coin + " "+item.totalCuota
        holder.tvCuotaCapital.text = Current.coin + " "+item.capital
        holder.tvCuotaInteres.text = Current.coin + " "+item.interes
        holder.tvCuotaSaldo.text = Current.coin + " "+item.saldoBase
        holder.cvCuota.setOnClickListener {
            onItemClickListener?.onItemClick(item,holder.cvCuota)
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
    fun fillList(items: List<Cuota>) {
        this.items += items
        notifyDataSetChanged()
    }

    fun clear() {
        this.items = emptyList()
    }
}

class ViewHolderCuota (binding: RowItemInstallmentBinding) : RecyclerView.ViewHolder(binding.root) {
    val tvCuotaNumero: TextView = binding.tvCuotaNumeroRow
    val tvCuotaFechaPago: TextView = binding.tvCuotaFechaPagoRow
    val tvCuotaTotal: TextView = binding.tvCuotaTotalRow
    val tvCuotaCapital: TextView = binding.tvCuotaCapitalRow
    val tvCuotaInteres: TextView = binding.tvCuotaInteresRow
    val tvCuotaSaldo: TextView = binding.tvCuotaSaldoRow
    val cvCuota: CardView = binding.cvCuotaRow
}