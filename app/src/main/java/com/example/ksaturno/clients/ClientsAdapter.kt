package com.example.ksaturno.clients

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R

class ClientsAdapter(
    private var clients: List<Client>,
    private val onEditClick: (Client) -> Unit,
    private val onDeleteClick: (Client) -> Unit
) : RecyclerView.Adapter<ClientsAdapter.ClientViewHolder>() {

    fun updateClients(newClients: List<Client>) {
        clients = newClients
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_client, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]
        holder.bind(client)
    }

    override fun getItemCount(): Int = clients.size

    inner class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.text_view_client_name)
        private val representativeTextView: TextView = itemView.findViewById(R.id.text_view_client_representative)
        private val editImageView: ImageView = itemView.findViewById(R.id.image_view_edit_client)
        private val deleteImageView: ImageView = itemView.findViewById(R.id.image_view_delete_client)

        fun bind(client: Client) {
            nameTextView.text = client.nombre
            representativeTextView.text = client.representante

            editImageView.setOnClickListener { onEditClick(client) }
            deleteImageView.setOnClickListener { onDeleteClick(client) }
        }
    }
}
