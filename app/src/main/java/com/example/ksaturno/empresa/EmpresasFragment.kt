package com.example.ksaturno.empresa

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EmpresasFragment : Fragment() {

    private lateinit var viewModel: EmpresasViewModel
    private lateinit var adapter: EmpresaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate manually to avoid ViewBinding dependency for this snippet if not fully set up
        return inflater.inflate(R.layout.fragment_empresas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(EmpresasViewModel::class.java)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_empresas)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add_empresa)

        adapter = EmpresaAdapter(
            emptyList(),
            onEditClick = { empresa -> showEditDialog(empresa) },
            onDeleteClick = { empresa -> showDeleteConfirmation(empresa) }
        )

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        viewModel.empresas.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                // Replaced Toast with AlertDialog to show full error message
                AlertDialog.Builder(requireContext())
                    .setTitle("Error")
                    .setMessage(error)
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
        }

        viewModel.fetchEmpresas()

        fab.setOnClickListener {
            showAddDialog()
        }
    }

    private fun showAddDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_empresa, null)
        
        val etNombre = dialogView.findViewById<EditText>(R.id.et_nombre)
        val etDireccion = dialogView.findViewById<EditText>(R.id.et_direccion)
        val etTelefono = dialogView.findViewById<EditText>(R.id.et_telefono)
        val etCorreo = dialogView.findViewById<EditText>(R.id.et_correo)
        val etRfc = dialogView.findViewById<EditText>(R.id.et_rfc)
        val etRepresentante = dialogView.findViewById<EditText>(R.id.et_representante)
        val etFolio = dialogView.findViewById<EditText>(R.id.et_folio_factura)

        AlertDialog.Builder(requireContext())
            .setTitle("Agregar Empresa")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val request = CreateEmpresaRequest(
                    nombre = etNombre.text.toString(),
                    direccion = etDireccion.text.toString(),
                    telefono = etTelefono.text.toString(),
                    correo = etCorreo.text.toString(),
                    rfc = etRfc.text.toString(),
                    representante = etRepresentante.text.toString(),
                    folioFactura = etFolio.text.toString().toIntOrNull() ?: 0
                )
                viewModel.createEmpresa(request)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditDialog(empresa: Empresa) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_empresa, null)

        val etNombre = dialogView.findViewById<EditText>(R.id.et_nombre)
        val etDireccion = dialogView.findViewById<EditText>(R.id.et_direccion)
        val etTelefono = dialogView.findViewById<EditText>(R.id.et_telefono)
        val etCorreo = dialogView.findViewById<EditText>(R.id.et_correo)
        val etRfc = dialogView.findViewById<EditText>(R.id.et_rfc)
        val etRepresentante = dialogView.findViewById<EditText>(R.id.et_representante)
        val etFolio = dialogView.findViewById<EditText>(R.id.et_folio_factura)

        // Pre-fill data
        etNombre.setText(empresa.nombre)
        etDireccion.setText(empresa.direccion)
        etTelefono.setText(empresa.telefono)
        etCorreo.setText(empresa.correo)
        etRfc.setText(empresa.rfc)
        etRepresentante.setText(empresa.representante)
        etFolio.setText(empresa.folioFactura.toString())

        AlertDialog.Builder(requireContext())
            .setTitle("Editar Empresa")
            .setView(dialogView)
            .setPositiveButton("Actualizar") { _, _ ->
                val updatedEmpresa = empresa.copy(
                    nombre = etNombre.text.toString(),
                    direccion = etDireccion.text.toString(),
                    telefono = etTelefono.text.toString(),
                    correo = etCorreo.text.toString(),
                    rfc = etRfc.text.toString(),
                    representante = etRepresentante.text.toString(),
                    folioFactura = etFolio.text.toString().toIntOrNull() ?: 0
                )
                viewModel.updateEmpresa(updatedEmpresa)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDeleteConfirmation(empresa: Empresa) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Empresa")
            .setMessage("¿Seguro que deseas eliminar a ${empresa.nombre}?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteEmpresa(empresa.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
