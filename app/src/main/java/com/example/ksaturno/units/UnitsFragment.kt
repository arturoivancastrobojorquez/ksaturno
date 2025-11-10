package com.example.ksaturno.units

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R
import com.example.ksaturno.clients.Client
import com.google.android.material.floatingactionbutton.FloatingActionButton
// Import the class with an alias to avoid the name collision with kotlin.Unit
import com.example.ksaturno.units.Unit as DomainUnit

class UnitsFragment : Fragment() {

    private lateinit var viewModel: UnitsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_units, container, false)
        viewModel = ViewModelProvider(this).get(UnitsViewModel::class.java)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_units)
        val adapter = UnitsAdapter(emptyList(), ::onEditUnit, ::onDeleteUnit)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.units.observe(viewLifecycleOwner) {
            adapter.updateUnits(it)
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        view.findViewById<FloatingActionButton>(R.id.fab_add_unit).setOnClickListener {
            showAddOrEditUnitDialog(null)
        }

        return view
    }

    private fun onEditUnit(unit: DomainUnit) {
        showAddOrEditUnitDialog(unit)
    }

    private fun onDeleteUnit(unit: DomainUnit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Unidad")
            .setMessage("¿Estás seguro de que deseas eliminar esta unidad?")
            .setPositiveButton("Sí") { _, _ -> viewModel.deleteUnit(unit.idUnidad) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showAddOrEditUnitDialog(unit: DomainUnit?) {
        val isEditMode = unit != null
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_unit, null)

        val clientSpinner: Spinner = dialogView.findViewById(R.id.spinner_client)
        val nameEditText: EditText = dialogView.findViewById(R.id.edit_text_unit_name)
        val installDateEditText: EditText = dialogView.findViewById(R.id.edit_text_install_date)
        val simCardEditText: EditText = dialogView.findViewById(R.id.edit_text_sim_card)
        val commentsEditText: EditText = dialogView.findViewById(R.id.edit_text_comments)
        val statusEditText: EditText = dialogView.findViewById(R.id.edit_text_status)

        viewModel.clients.observe(viewLifecycleOwner) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it)
            clientSpinner.adapter = adapter

            if (isEditMode) {
                val clientPosition = it.indexOfFirst { client -> client.id == unit?.idCliente }
                clientSpinner.setSelection(clientPosition)
            }
        }

        if (isEditMode) {
            nameEditText.setText(unit?.nombreUnidad)
            installDateEditText.setText(unit?.fechaInstalacion)
            simCardEditText.setText(unit?.tarjetaSim)
            commentsEditText.setText(unit?.comentarios)
            statusEditText.setText(unit?.estatus)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (isEditMode) "Editar Unidad" else "Agregar Unidad")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val selectedClient = clientSpinner.selectedItem as Client
                val unitName = nameEditText.text.toString()
                val installDate = installDateEditText.text.toString()
                val simCard = simCardEditText.text.toString()
                val comments = commentsEditText.text.toString()
                val status = statusEditText.text.toString()

                if (isEditMode) {
                    val updatedUnit = unit!!.copy(
                        idCliente = selectedClient.id,
                        nombreUnidad = unitName,
                        fechaInstalacion = installDate,
                        tarjetaSim = simCard,
                        comentarios = comments,
                        estatus = status,
                        ultimaFechaInstalacion = unit.ultimaFechaInstalacion // Preserve this field
                    )
                    viewModel.updateUnit(updatedUnit)
                } else {
                    val newUnit = CreateUnitRequest(
                        idCliente = selectedClient.id,
                        nombreUnidad = unitName,
                        fechaInstalacion = installDate,
                        ultimaFechaInstalacion = null, // Pass null for a new unit
                        comentarios = comments,
                        estatus = status,
                        tarjetaSim = simCard,
                        categoryId = 1 // TODO: Reemplazar con ID de categoría real
                    )
                    viewModel.createUnit(newUnit)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
