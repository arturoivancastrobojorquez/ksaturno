package com.example.ksaturno.units

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R
// Import the class with an alias to avoid the name collision with kotlin.Unit
import com.example.ksaturno.units.Unit as DomainUnit

class UnitsAdapter(
    private var units: List<DomainUnit>,
    private val onEditClick: (DomainUnit) -> Unit,
    private val onDeleteClick: (DomainUnit) -> Unit
) : RecyclerView.Adapter<UnitsAdapter.UnitViewHolder>() {

    fun updateUnits(newUnits: List<DomainUnit>) {
        units = newUnits
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_unit, parent, false)
        return UnitViewHolder(view)
    }

    override fun onBindViewHolder(holder: UnitViewHolder, position: Int) {
        val unit = units[position]
        holder.bind(unit)
    }

    override fun getItemCount(): Int = units.size

    inner class UnitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.text_view_unit_name)
        private val editImageView: ImageView = itemView.findViewById(R.id.image_view_edit_unit)
        private val deleteImageView: ImageView = itemView.findViewById(R.id.image_view_delete_unit)

        fun bind(unit: DomainUnit) {
            nameTextView.text = unit.nombreUnidad

            editImageView.setOnClickListener { onEditClick(unit) }
            deleteImageView.setOnClickListener { onDeleteClick(unit) }
        }
    }
}
