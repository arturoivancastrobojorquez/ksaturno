package com.example.ksaturno.facturas

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R
import java.text.SimpleDateFormat
import java.util.Locale

class FacturasAdapter(private var facturas: List<Factura>) :
    RecyclerView.Adapter<FacturasAdapter.FacturaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacturaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_factura, parent, false)
        return FacturaViewHolder(view)
    }

    override fun onBindViewHolder(holder: FacturaViewHolder, position: Int) {
        val factura = facturas[position]
        holder.bind(factura)
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

        fun bind(factura: Factura) {
            numeroTextView.text = "Factura #${factura.numeroFactura}"
            
            // Unidad y Servicio
            val unidad = factura.nombreUnidad ?: "Sin unidad"
            val servicio = factura.nombreServicio ?: "General"
            unidadTextView.text = "$unidad - $servicio"

            // Fecha
            // La fecha viene como String desde la API, formateamos si es posible
            // Asumimos que viene YYYY-MM-DD
            val fechaStr = factura.fechaEmision
            fechaTextView.text = "Emisión: $fechaStr"

            // Monto
            montoTextView.text = "$ ${String.format("%.2f", factura.monto)}"

            // Estado (Color y Texto)
            estadoTextView.text = factura.estado?.uppercase() ?: "N/A"
            
            val color = when (factura.estado?.lowercase()) {
                "pagado" -> Color.parseColor("#4CAF50") // Verde
                "pendiente" -> Color.parseColor("#FFC107") // Ambar/Amarillo
                "vencido" -> Color.parseColor("#F44336") // Rojo
                else -> Color.GRAY
            }
            // Para simplificar, cambiamos el color del texto del estado, o podríamos tintar el fondo
            estadoTextView.setTextColor(color)
        }
    }
}
