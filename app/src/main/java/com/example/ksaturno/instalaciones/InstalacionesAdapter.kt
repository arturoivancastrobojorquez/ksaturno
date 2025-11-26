package com.example.ksaturno.instalaciones

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R
import java.text.SimpleDateFormat
import java.util.Locale

class InstalacionesAdapter(
    private var instalaciones: List<InstalacionDisplay>,
    private val onEditClick: (InstalacionDisplay) -> Unit,
    private val onDeleteClick: (InstalacionDisplay) -> Unit,
    private val onGenerateInvoiceClick: (InstalacionDisplay) -> Unit
) : RecyclerView.Adapter<InstalacionesAdapter.InstalacionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstalacionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_instalacion, parent, false)
        return InstalacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: InstalacionViewHolder, position: Int) {
        val instalacion = instalaciones[position]
        holder.bind(instalacion, onEditClick, onDeleteClick, onGenerateInvoiceClick)
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
        private val editButton: ImageView = itemView.findViewById(R.id.btn_edit_instalacion)
        private val deleteButton: ImageView = itemView.findViewById(R.id.btn_delete_instalacion)
        private val invoiceButton: ImageView = itemView.findViewById(R.id.btn_generate_invoice)

        fun bind(
            instalacion: InstalacionDisplay,
            onEditClick: (InstalacionDisplay) -> Unit,
            onDeleteClick: (InstalacionDisplay) -> Unit,
            onGenerateInvoiceClick: (InstalacionDisplay) -> Unit
        ) {
            idTextView.text = "Instalacion ID: ${instalacion.idInstalacion}"
            unidadTextView.text = "Unidad: ${instalacion.nombreUnidad}"
            tecnicoTextView.text = "Técnico: ${instalacion.nombreTecnico}"
            
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateString = try {
                formatter.format(instalacion.fechaInstalacion)
            } catch (e: Exception) {
                instalacion.fechaInstalacion.toString()
            }
            fechaTextView.text = "Fecha: $dateString"
            estadoTextView.text = "Estado: ${instalacion.estado ?: "N/A"}"
            
            editButton.setOnClickListener { onEditClick(instalacion) }
            deleteButton.setOnClickListener { onDeleteClick(instalacion) }
            
            // Only enable invoice button if installation is not yet 'completada' (optional logic)
            // Or keep it enabled but warn the user.
            invoiceButton.setOnClickListener { onGenerateInvoiceClick(instalacion) }
            
            // Example visual cue: green if completed, grey otherwise
             if (instalacion.estado?.lowercase() == "completada") {
                 invoiceButton.alpha = 0.5f // Dim it if already done
                 // invoiceButton.isEnabled = false // Optionally disable
             } else {
                 invoiceButton.alpha = 1.0f
             }
        }
    }
}
