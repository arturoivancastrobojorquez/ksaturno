package com.example.ksaturno.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R
import java.text.SimpleDateFormat
import java.util.Locale

class RenewalAlertAdapter(private var alerts: List<RenewalAlert>) :
    RecyclerView.Adapter<RenewalAlertAdapter.RenewalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RenewalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_renewal_alert, parent, false)
        return RenewalViewHolder(view)
    }

    override fun onBindViewHolder(holder: RenewalViewHolder, position: Int) {
        holder.bind(alerts[position])
    }

    override fun getItemCount(): Int = alerts.size

    fun updateData(newAlerts: List<RenewalAlert>) {
        alerts = newAlerts
        notifyDataSetChanged()
    }

    class RenewalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val diasRestantes: TextView = itemView.findViewById(R.id.tv_dias_restantes)
        private val fechaRenovacion: TextView = itemView.findViewById(R.id.tv_fecha_renovacion)
        private val clienteUnidad: TextView = itemView.findViewById(R.id.tv_cliente_unidad)
        private val monto: TextView = itemView.findViewById(R.id.tv_monto_renovacion)
        private val periodo: TextView = itemView.findViewById(R.id.tv_periodo)

        fun bind(alert: RenewalAlert) {
            diasRestantes.text = "${alert.diasRestantes} DÍAS RESTANTES"
            
            // Formatear fecha si es necesario, asumiendo String YYYY-MM-DD
            fechaRenovacion.text = "Vence: ${alert.fechaInicio}"
            
            clienteUnidad.text = "${alert.cliente} - ${alert.unidad}"
            
            monto.text = "$ ${String.format("%.2f", alert.monto)}"
            periodo.text = "Pago: ${alert.periodoPago.capitalize()}"
        }
        
        private fun String.capitalize(): String {
            return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
    }
}
