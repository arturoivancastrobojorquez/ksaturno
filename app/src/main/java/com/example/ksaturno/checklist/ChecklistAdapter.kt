package com.example.ksaturno.checklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R

/**
 * ADAPTADOR: Conecta los datos de los ítems de la lista con el RecyclerView.
 */
class ChecklistAdapter(
    private var items: List<ChecklistItem>,
    private val onEditClick: (ChecklistItem) -> Unit,
    private val onDeleteClick: (ChecklistItem) -> Unit
) : RecyclerView.Adapter<ChecklistAdapter.ChecklistItemViewHolder>() {

    fun updateItems(newItems: List<ChecklistItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checklist, parent, false)
        return ChecklistItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChecklistItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ChecklistItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val descriptionTextView: TextView = itemView.findViewById(R.id.text_view_checklist_description)
        private val requiredTextView: TextView = itemView.findViewById(R.id.text_view_checklist_required)
        private val editImageView: ImageView = itemView.findViewById(R.id.image_view_edit_checklist_item)
        private val deleteImageView: ImageView = itemView.findViewById(R.id.image_view_delete_checklist_item)

        fun bind(item: ChecklistItem) {
            descriptionTextView.text = item.description
            requiredTextView.text = if (item.isRequired == 1) "Requerido" else "Opcional"
            
            editImageView.setOnClickListener { onEditClick(item) }
            deleteImageView.setOnClickListener { onDeleteClick(item) }
        }
    }
}
