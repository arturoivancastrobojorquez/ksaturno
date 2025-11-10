package com.example.ksaturno.servicios

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R

class ServiciosAdapter(
    private var servicios: List<Servicio>,
    private val onEditClick: (Servicio) -> Unit,
    private val onDeleteClick: (Servicio) -> Unit
) : RecyclerView.Adapter<ServiciosAdapter.ServicioViewHolder>() {

    fun updateServicios(newServicios: List<Servicio>) {
        servicios = newServicios
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_servicio, parent, false)
        return ServicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServicioViewHolder, position: Int) {
        holder.bind(servicios[position])
    }

    override fun getItemCount(): Int = servicios.size

    inner class ServicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val typeTextView: TextView = itemView.findViewById(R.id.text_view_service_type)
        private val unitTextView: TextView = itemView.findViewById(R.id.text_view_service_unit)
        private val statusTextView: TextView = itemView.findViewById(R.id.text_view_service_status)
        private val amountTextView: TextView = itemView.findViewById(R.id.text_view_service_amount)
        private val editButton: ImageView = itemView.findViewById(R.id.image_view_edit_service)
        private val deleteButton: ImageView = itemView.findViewById(R.id.image_view_delete_service)

        fun bind(servicio: Servicio) {
            typeTextView.text = "Tipo: ${servicio.tipo}"
            // Note: To show the unit name, you would need to fetch the units list 
            // and find the unit by its ID (servicio.idUnidad).
            unitTextView.text = "Unidad ID: ${servicio.idUnidad}"
            statusTextView.text = "Estado: ${servicio.estado}"
            amountTextView.text = "Monto: $${servicio.monto}"

            editButton.setOnClickListener { onEditClick(servicio) }
            deleteButton.setOnClickListener { onDeleteClick(servicio) }
        }
    }
}
