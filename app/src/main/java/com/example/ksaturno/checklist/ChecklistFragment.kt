package com.example.ksaturno.checklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R
import com.example.ksaturno.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * VISTA: El Fragment que controla la UI para la Lista de Verificación.
 */
class ChecklistFragment : Fragment() {

    private lateinit var viewModel: ChecklistViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_checklist, container, false)

        val repository = ChecklistRepository(RetrofitClient.instance)
        val factory = ChecklistViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ChecklistViewModel::class.java)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_checklist)
        val adapter = ChecklistAdapter(emptyList(), { onEditItem(it) }, { onDeleteItem(it) })
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.checklistItems.observe(viewLifecycleOwner) {
            adapter.updateItems(it)
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<FloatingActionButton>(R.id.fab_add_checklist_item).setOnClickListener {
            showAddOrEditDialog(null)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Lista de Verificación"
    }

    private fun onEditItem(item: ChecklistItem) {
        showAddOrEditDialog(item)
    }

    private fun onDeleteItem(item: ChecklistItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Ítem")
            .setMessage("¿Estás seguro de que deseas eliminar este ítem?")
            .setPositiveButton("Eliminar") { _, _ -> viewModel.deleteChecklistItem(item) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showAddOrEditDialog(item: ChecklistItem?) {
        val context = requireContext()
        val isEditMode = item != null

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
        }

        val descriptionEditText = EditText(context).apply { hint = "Descripción" }
        val isRequiredCheckBox = CheckBox(context).apply { text = "Es Requerido" }

        layout.addView(descriptionEditText)
        layout.addView(isRequiredCheckBox)

        if (isEditMode) {
            descriptionEditText.setText(item?.description)
            isRequiredCheckBox.isChecked = item?.isRequired == 1
        }

        AlertDialog.Builder(context)
            .setTitle(if (isEditMode) "Editar Ítem" else "Nuevo Ítem")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val description = descriptionEditText.text.toString()
                val isRequired = if (isRequiredCheckBox.isChecked) 1 else 0

                if (description.isNotBlank()) {
                    if (isEditMode) {
                        val updatedItem = item!!.copy(
                            description = description,
                            isRequired = isRequired
                        )
                        viewModel.updateChecklistItem(updatedItem)
                    } else {
                        val request = CreateChecklistItemRequest(
                            description = description,
                            isRequired = isRequired
                        )
                        viewModel.createChecklistItem(request)
                    }
                } else {
                    Toast.makeText(context, "La descripción es obligatoria", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
