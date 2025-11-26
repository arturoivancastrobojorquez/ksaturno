package com.example.ksaturno.empresa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R

class EmpresaAdapter(
    private var empresas: List<Empresa>,
    private val onEditClick: (Empresa) -> Unit,
    private val onDeleteClick: (Empresa) -> Unit
) : RecyclerView.Adapter<EmpresaAdapter.EmpresaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpresaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_empresa, parent, false)
        return EmpresaViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmpresaViewHolder, position: Int) {
        val empresa = empresas[position]
        holder.bind(empresa, onEditClick, onDeleteClick)
    }

    override fun getItemCount(): Int = empresas.size

    fun updateData(newEmpresas: List<Empresa>) {
        empresas = newEmpresas
        notifyDataSetChanged()
    }

    class EmpresaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_empresa_name)
        private val rfcTextView: TextView = itemView.findViewById(R.id.tv_empresa_rfc)
        private val repTextView: TextView = itemView.findViewById(R.id.tv_empresa_representante)
        private val editButton: ImageView = itemView.findViewById(R.id.btn_edit_empresa)
        private val deleteButton: ImageView = itemView.findViewById(R.id.btn_delete_empresa)

        fun bind(
            empresa: Empresa,
            onEditClick: (Empresa) -> Unit,
            onDeleteClick: (Empresa) -> Unit
        ) {
            nameTextView.text = empresa.nombre
            rfcTextView.text = "RFC: ${empresa.rfc}"
            repTextView.text = "Rep: ${empresa.representante}"

            editButton.setOnClickListener { onEditClick(empresa) }
            deleteButton.setOnClickListener { onDeleteClick(empresa) }
        }
    }
}
