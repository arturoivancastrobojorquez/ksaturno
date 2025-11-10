package com.example.ksaturno.instalaciones

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R
import java.text.SimpleDateFormat
import java.util.Locale

class InstalacionesAdapter(private var instalaciones: List<InstalacionDisplay>) :
    RecyclerView.Adapter<InstalacionesAdapter.InstalacionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstalacionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_instalacion, parent, false)
        return InstalacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: InstalacionViewHolder, position: Int) {
        val instalacion = instalaciones[position]
        holder.bind(instalacion)
    }

    override fun getItemCount(): Int = instalaciones.size

    fun updateData(newInstalaciones: List<InstalacionDisplay>) {
        instalaciones = newInstalaciones
        notifyDataSetChanged()
    }

    class InstalacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val idTextView: TextView = itemView.findViewById(R.id.tv_instalacion_id)
        private val unidadTextView: TextView = itemView.findViewById(R.id.tv_unidad_nombre)
        private val tecnicoTextView: TextView = itemView.findViewById(R.id.tv_tecnico_nombre)
        private val fechaTextView: TextView = itemView.findViewById(R.id.tv_fecha_instalacion)
        private val estadoTextView: TextView = itemView.findViewById(R.id.tv_instalacion_estado)
        // You can add a TextView for service type in your item_instalacion.xml if you want
        // private val servicioTextView: TextView = itemView.findViewById(R.id.tv_servicio_nombre)

        fun bind(instalacion: InstalacionDisplay) {
            idTextView.text = "Instalacion ID: ${instalacion.idInstalacion}"
            // The data is now pre-processed, so we just display it.
            unidadTextView.text = "Unidad: ${instalacion.nombreUnidad}"
            tecnicoTextView.text = "Técnico: ${instalacion.nombreTecnico}"
            
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            fechaTextView.text = "Fecha: ${formatter.format(instalacion.fechaInstalacion)}"
            estadoTextView.text = "Estado: ${instalacion.estado ?: "N/A"}"
            // servicioTextView.text = "Servicio: ${instalacion.nombreServicio}"
        }
    }
}
