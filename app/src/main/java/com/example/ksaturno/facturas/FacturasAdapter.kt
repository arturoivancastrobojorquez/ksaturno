package com.example.ksaturno.facturas

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R

class FacturasAdapter(
    private var facturas: List<Factura>,
    private val onPayClick: (Factura) -> Unit
) : RecyclerView.Adapter<FacturasAdapter.FacturaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacturaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_factura, parent, false)
        return FacturaViewHolder(view)
    }

    override fun onBindViewHolder(holder: FacturaViewHolder, position: Int) {
        val factura = facturas[position]
        holder.bind(factura, onPayClick)
    }

    override fun getItemCount(): Int = facturas.size

    fun updateData(newFacturas: List<Factura>) {
        facturas = newFacturas
        notifyDataSetChanged()
    }

    class FacturaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numeroTextView: TextView = itemView.findViewById(R.id.tv_numero_factura)
        private val estadoTextView: TextView = itemView.findViewById(R.id.tv_estado_factura)
        private val fechaTextView: TextView = itemView.findViewById(R.id.tv_fecha_emision)
        private val unidadTextView: TextView = itemView.findViewById(R.id.tv_unidad_servicio)
        private val montoTextView: TextView = itemView.findViewById(R.id.tv_monto)
        private val payButton: Button = itemView.findViewById(R.id.btn_pagar)

        fun bind(factura: Factura, onPayClick: (Factura) -> Unit) {
            numeroTextView.text = "Factura #${factura.numeroFactura}"
            
            val unidad = factura.nombreUnidad ?: "Sin unidad"
            val servicio = factura.nombreServicio ?: "General"
            unidadTextView.text = "$unidad - $servicio"

            fechaTextView.text = "Emisión: ${factura.fechaEmision}"
            montoTextView.text = "$ ${String.format("%.2f", factura.monto)}"

            estadoTextView.text = factura.estado?.uppercase() ?: "N/A"
            
            val estadoLower = factura.estado?.lowercase()
            val color: Int
            val isPayable: Boolean

            when (estadoLower) {
                "pagado" -> {
                    color = Color.parseColor("#4CAF50") // Verde
                    isPayable = false
                }
                "pendiente" -> {
                    color = Color.parseColor("#FFC107") // Ambar/Amarillo
                    isPayable = true
                }
                "vencido" -> {
                    color = Color.parseColor("#F44336") // Rojo
                    isPayable = true
                }
                else -> {
                    color = Color.GRAY
                    isPayable = false
                }
            }
            
            estadoTextView.setTextColor(color)
            
            if (isPayable) {
                payButton.visibility = View.VISIBLE
                payButton.setOnClickListener { onPayClick(factura) }
            } else {
                payButton.visibility = View.GONE
            }
        }
    }
}
