package com.example.ksaturno.instalaciones

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R
import com.example.ksaturno.checklist.CompletedChecklistItem

class InstallationEvidenceAdapter(
    private var items: List<CompletedChecklistItem>,
    private val listener: EvidenceItemListener
) : RecyclerView.Adapter<InstallationEvidenceAdapter.EvidenceViewHolder>() {

    interface EvidenceItemListener {
        fun onTakePhotoClick(item: CompletedChecklistItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvidenceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_evidence, parent, false)
        return EvidenceViewHolder(view)
    }

    override fun onBindViewHolder(holder: EvidenceViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<CompletedChecklistItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class EvidenceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val descriptionTextView: TextView = itemView.findViewById(R.id.text_view_evidence_description)
        private val statusImageView: ImageView = itemView.findViewById(R.id.image_view_status)
        private val takePhotoButton: Button = itemView.findViewById(R.id.button_take_photo)

        fun bind(item: CompletedChecklistItem) {
            descriptionTextView.text = item.descripcion
            
            // TODO: You can change the status icon based on whether evidence has been uploaded for this item.
            statusImageView.setImageResource(R.drawable.ic_camera) // Default state

            takePhotoButton.setOnClickListener {
                listener.onTakePhotoClick(item)
            }
        }
    }
}
