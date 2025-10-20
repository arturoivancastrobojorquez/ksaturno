package com.example.ksaturno.units

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R
import com.example.ksaturno.RetrofitClient
import com.example.ksaturno.clients.ClientSearchFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UnitsFragment : Fragment() {

    private lateinit var viewModel: UnitsViewModel
    private lateinit var unitsAdapter: UnitsAdapter
    private var selectedClientId: Int? = null
    private var selectedClientName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_units, container, false)

        val repository = UnitsRepository(RetrofitClient.instance)
        val factory = UnitsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(UnitsViewModel::class.java)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_units)
        unitsAdapter = UnitsAdapter(emptyList(), { onEditUnit(it) }, { onDeleteUnit(it) })
        recyclerView.adapter = unitsAdapter

        viewModel.units.observe(viewLifecycleOwner) {
            unitsAdapter.updateUnits(it)
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        val fab: FloatingActionButton = view.findViewById(R.id.fab_add_unit)
        fab.setOnClickListener {
            selectedClientId = null
            selectedClientName = null
            showAddOrEditUnitDialog(null)
        }

        setFragmentResultListener("clientSearchRequest") { _, bundle ->
            selectedClientId = bundle.getInt("selectedClientId")
            selectedClientName = bundle.getString("selectedClientName")
            showAddOrEditUnitDialog(null)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Unidades"
    }

    private fun onEditUnit(unit: Unit) {
        selectedClientId = unit.clientId
        // We would need the client name here from the unit object if available
        // selectedClientName = unit.clientName 
        showAddOrEditUnitDialog(unit)
    }

    private fun onDeleteUnit(unit: Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Unidad")
            .setMessage("¿Estás seguro de que deseas eliminar esta unidad?")
            .setPositiveButton("Eliminar") { _, _ -> viewModel.deleteUnit(unit) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showAddOrEditUnitDialog(unit: Unit?) {
        val context = requireContext()
        val isEditMode = unit != null

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_unit, null)
        val selectClientTextView: TextView = dialogView.findViewById(R.id.text_view_select_client)

        val dialog = AlertDialog.Builder(context)
            .setTitle(if (isEditMode) "Editar Unidad" else "Nueva Unidad")
            .setView(dialogView)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        selectClientTextView.setOnClickListener {
            dialog.dismiss()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ClientSearchFragment())
                .addToBackStack(null)
                .commit()
        }

        if (selectedClientName != null) {
            selectClientTextView.text = selectedClientName
        }

        val nameEditText: EditText = dialogView.findViewById(R.id.edit_text_unit_name)
        val categorySpinner: Spinner = dialogView.findViewById(R.id.spinner_category)
        val simCardEditText: EditText = dialogView.findViewById(R.id.edit_text_sim_card)
        val statusEditText: EditText = dialogView.findViewById(R.id.edit_text_status)
        val commentsEditText: EditText = dialogView.findViewById(R.id.edit_text_comments)

        val categoryAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mutableListOf("Cargando..."))
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            val categoryNames = categories.map { it.nombre }
            categoryAdapter.clear()
            categoryAdapter.addAll(categoryNames)
            categoryAdapter.notifyDataSetChanged()
            if (isEditMode) {
                val categoryPosition = categories.indexOfFirst { it.id == unit?.categoryId }
                if (categoryPosition != -1) categorySpinner.setSelection(categoryPosition)
            }
        }

        if (isEditMode) {
            nameEditText.setText(unit?.name)
            simCardEditText.setText(unit?.simCard)
            statusEditText.setText(unit?.status)
            commentsEditText.setText(unit?.comments)
            if (selectedClientName != null) {
                selectClientTextView.text = selectedClientName
            } else {
                 selectClientTextView.text = "Cliente ID: ${unit?.clientId}"
            }
        }
        
        dialog.show()
        
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { 
            val name = nameEditText.text.toString()
            val simCard = simCardEditText.text.toString()
            val status = statusEditText.text.toString()
            val comments = commentsEditText.text.toString()

            val selectedCategoryPosition = categorySpinner.selectedItemPosition
            val selectedCategory = viewModel.categories.value?.getOrNull(selectedCategoryPosition)

            if (name.isNotBlank() && selectedCategory != null && selectedClientId != null) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val currentDate = dateFormat.format(Date())

                if (isEditMode) {
                    val updatedUnit = unit!!.copy(
                        name = name, clientId = selectedClientId!!, categoryId = selectedCategory.id,
                        simCard = simCard, status = status, comments = comments
                    )
                    viewModel.updateUnit(updatedUnit)
                } else {
                    val newUnit = CreateUnitRequest(
                        name = name, clientId = selectedClientId!!, categoryId = selectedCategory.id,
                        installDate = currentDate, lastInstallDate = currentDate, comments = comments,
                        status = status, simCard = simCard
                    )
                    viewModel.createUnit(newUnit)
                }
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Nombre, cliente y categoría son obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
