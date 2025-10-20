package com.example.ksaturno.units

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R

class UnitsAdapter(
    private var units: List<Unit>,
    private val onEditClick: (Unit) -> kotlin.Unit,
    private val onDeleteClick: (Unit) -> kotlin.Unit
) : RecyclerView.Adapter<UnitsAdapter.UnitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_unit, parent, false)
        return UnitViewHolder(view)
    }

    override fun onBindViewHolder(holder: UnitViewHolder, position: Int) {
        val unit = units[position]
        holder.bind(unit)
    }

    override fun getItemCount(): Int = units.size

    fun updateUnits(newUnits: List<Unit>) {
        units = newUnits
        notifyDataSetChanged()
    }

    inner class UnitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.text_view_unit_name)
        private val statusTextView: TextView = itemView.findViewById(R.id.text_view_unit_status)
        private val editImageView: ImageView = itemView.findViewById(R.id.image_view_edit_unit)
        private val deleteImageView: ImageView = itemView.findViewById(R.id.image_view_delete_unit)

        fun bind(unit: Unit) {
            nameTextView.text = unit.name
            statusTextView.text = unit.status
            editImageView.setOnClickListener { onEditClick(unit) }
            deleteImageView.setOnClickListener { onDeleteClick(unit) }
        }
    }
}
