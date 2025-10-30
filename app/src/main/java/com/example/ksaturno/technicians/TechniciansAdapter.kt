package com.example.ksaturno.technicians

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R

class TechniciansAdapter(
    private var technicians: List<Technician>,
    private val onEditClick: (Technician) -> Unit,
    private val onDeleteClick: (Technician) -> Unit
) : RecyclerView.Adapter<TechniciansAdapter.TechnicianViewHolder>() {

    fun updateTechnicians(newTechnicians: List<Technician>) {
        technicians = newTechnicians
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TechnicianViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_technician, parent, false)
        return TechnicianViewHolder(view)
    }

    override fun onBindViewHolder(holder: TechnicianViewHolder, position: Int) {
        holder.bind(technicians[position])
    }

    override fun getItemCount(): Int = technicians.size

    inner class TechnicianViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.text_view_technician_name)
        private val emailTextView: TextView = itemView.findViewById(R.id.text_view_technician_email)
        private val editImageView: ImageView = itemView.findViewById(R.id.image_view_edit_technician)
        private val deleteImageView: ImageView = itemView.findViewById(R.id.image_view_delete_technician)

        fun bind(technician: Technician) {
            nameTextView.text = technician.name
            emailTextView.text = technician.email
            editImageView.setOnClickListener { onEditClick(technician) }
            deleteImageView.setOnClickListener { onDeleteClick(technician) }
        }
    }
}
