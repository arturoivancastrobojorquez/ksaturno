package com.example.ksaturno.units

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R
import com.example.ksaturno.categories.Category
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Date
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

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Error")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        viewModel.successMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Éxito")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        view.findViewById<FloatingActionButton>(R.id.fab_add_unit).setOnClickListener {
            viewModel.clearSelection()
            showAddOrEditUnitDialog(null)
        }

        setFragmentResultListener("clientSearchRequest") { _, bundle ->
            val clientId = bundle.getInt("selectedClientId")
            viewModel.onClientSelected(clientId)
        }

        return view
    }

    private fun onEditUnit(unit: DomainUnit) {
        viewModel.prepareForEdit(unit)
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

        val selectClientTextView: TextView = dialogView.findViewById(R.id.text_view_select_client_for_unit)
        val categorySpinner: Spinner = dialogView.findViewById(R.id.spinner_category)
        val statusSpinner: Spinner = dialogView.findViewById(R.id.spinner_unit_status)
        val nameEditText: EditText = dialogView.findViewById(R.id.edit_text_unit_name)
        val installDateEditText: EditText = dialogView.findViewById(R.id.edit_text_install_date)
        val simCardEditText: EditText = dialogView.findViewById(R.id.edit_text_sim_card)
        val iccidEditText: EditText = dialogView.findViewById(R.id.edit_text_iccid)
        val commentsEditText: EditText = dialogView.findViewById(R.id.edit_text_comments)

        selectClientTextView.setOnClickListener { findNavController().navigate(R.id.clientSearchFragment) }

        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mutableListOf<Category>()).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        categorySpinner.adapter = categoryAdapter

        val unitStatusOptions = listOf("activa", "standby", "baja")
        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, unitStatusOptions).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        statusSpinner.adapter = statusAdapter

        viewModel.categories.observe(viewLifecycleOwner) {
            categoryAdapter.clear(); categoryAdapter.addAll(it); categoryAdapter.notifyDataSetChanged()
            if (isEditMode) { val pos = it.indexOfFirst { c -> c.id == unit?.idCategoria }; if (pos >= 0) categorySpinner.setSelection(pos) }
        }

        viewModel.selectedClient.observe(viewLifecycleOwner) {
            selectClientTextView.text = it?.nombre ?: "Seleccionar cliente..."
        }

        val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDate = Calendar.getInstance()

        fun parseDate(dateString: String?): Date? = if (dateString.isNullOrEmpty()) null else try { apiFormat.parse(dateString) } catch (e: ParseException) { null }

        if (isEditMode) {
            nameEditText.setText(unit?.nombreUnidad)
            parseDate(unit?.fechaInstalacion)?.let { selectedDate.time = it; installDateEditText.setText(displayFormat.format(it)) }
            simCardEditText.setText(unit?.tarjetaSim)
            iccidEditText.setText(unit?.iccid)
            commentsEditText.setText(unit?.comentarios)
            statusSpinner.setSelection(unitStatusOptions.indexOf(unit?.estatus).coerceAtLeast(0))
        }
        
        installDateEditText.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)
                installDateEditText.setText(displayFormat.format(selectedDate.time))
            }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH)).show()
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(if (isEditMode) "Editar Unidad" else "Agregar Unidad")
            .setView(dialogView)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnDismissListener { viewModel.clearSelection() }
        
        dialog.setOnShowListener { 
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener { 
                val selectedClient = viewModel.selectedClient.value
                val selectedCategory = categorySpinner.selectedItem as? Category
                val selectedStatus = statusSpinner.selectedItem as String
                val iccid = iccidEditText.text.toString()
                
                if (selectedClient == null) { viewModel.postValidationError("Por favor, selecciona un cliente"); return@setOnClickListener }
                if (selectedCategory == null) { viewModel.postValidationError("Por favor, selecciona una categoría"); return@setOnClickListener }
                if (installDateEditText.text.isBlank()) { viewModel.postValidationError("Por favor, selecciona una fecha"); return@setOnClickListener }
                if (iccid.isBlank()) { viewModel.postValidationError("Por favor, ingresa un ICCID"); return@setOnClickListener }

                val unitName = nameEditText.text.toString()

                if (isEditMode) {
                    val updatedUnit = unit!!.copy(
                        idCliente = selectedClient.id,
                        idCategoria = selectedCategory.id,
                        nombreUnidad = unitName,
                        fechaInstalacion = apiFormat.format(selectedDate.time),
                        tarjetaSim = simCardEditText.text.toString().ifBlank { null },
                        iccid = iccid,
                        comentarios = commentsEditText.text.toString().ifBlank { null },
                        estatus = selectedStatus
                    )
                    viewModel.updateUnit(updatedUnit)
                } else {
                    val newUnit = CreateUnitRequest(
                        id_cliente = selectedClient.id,
                        nombre_unidad = unitName,
                        fecha_instalacion = apiFormat.format(selectedDate.time),
                        tarjeta_sim = simCardEditText.text.toString().ifBlank { null },
                        iccid = iccid,
                        idcategoria = selectedCategory.id,
                        comentarios = commentsEditText.text.toString().ifBlank { null },
                        estatus = selectedStatus
                    )
                    viewModel.createUnit(newUnit)
                }
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}
