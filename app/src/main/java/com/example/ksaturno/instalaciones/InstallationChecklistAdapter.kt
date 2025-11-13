package com.example.ksaturno.instalaciones

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R
import com.example.ksaturno.checklist.ChecklistItem
import com.google.android.material.textfield.TextInputEditText

class InstallationChecklistAdapter(
    private var items: List<ChecklistItem>,
    private val listener: ChecklistItemListener
) : RecyclerView.Adapter<InstallationChecklistAdapter.ChecklistItemViewHolder>() {

    interface ChecklistItemListener {
        fun onItemStateChanged(itemId: Int, isChecked: Boolean, comments: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_installation_checklist, parent, false)
        return ChecklistItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChecklistItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<ChecklistItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ChecklistItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_item)
        private val commentsEditText: TextInputEditText = itemView.findViewById(R.id.edit_text_comments)

        fun bind(item: ChecklistItem) {
            checkBox.setOnCheckedChangeListener(null)
            commentsEditText.removeTextChangedListener(itemView.tag as? TextWatcher)

            // Corrected to use the 'description' property from the data class
            checkBox.text = item.description
            checkBox.isChecked = false
            commentsEditText.setText("")

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                listener.onItemStateChanged(item.id, isChecked, commentsEditText.text.toString())
            }

            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (checkBox.isChecked) {
                        listener.onItemStateChanged(item.id, true, s.toString())
                    }
                }
            }
            commentsEditText.addTextChangedListener(textWatcher)
            itemView.tag = textWatcher
        }
    }
}
