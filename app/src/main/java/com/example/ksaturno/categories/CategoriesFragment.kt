package com.example.ksaturno.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R
import com.example.ksaturno.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoriesFragment : Fragment() {

    private lateinit var viewModel: CategoriesViewModel
    private lateinit var categoriesAdapter: CategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_categories, container, false)

        // Initialize ViewModel
        val repository = CategoriesRepository(RetrofitClient.instance)
        val factory = CategoriesViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(CategoriesViewModel::class.java)

        // Setup RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_categories)
        categoriesAdapter = CategoriesAdapter(emptyList(), ::onEditCategory, ::onDeleteCategory)
        recyclerView.adapter = categoriesAdapter

        // Observe categories changes
        viewModel.categories.observe(viewLifecycleOwner) {
            categoriesAdapter.updateCategories(it)
        }

        // Observe toast messages
        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        // Setup FAB
        val fab: FloatingActionButton = view.findViewById(R.id.fab_add_category)
        fab.setOnClickListener {
            showAddCategoryDialog()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Categorías"
    }

    private fun onEditCategory(category: Category) {
        showEditCategoryDialog(category)
    }

    private fun onDeleteCategory(category: Category) {
        showDeleteCategoryConfirmationDialog(category)
    }

    private fun showAddCategoryDialog() {
        val editText = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Nueva Categoría")
            .setView(editText)
            .setPositiveButton("Guardar") { _, _ ->
                val name = editText.text.toString()
                if (name.isNotBlank()) {
                    viewModel.createCategory(name)
                } else {
                    Toast.makeText(requireContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditCategoryDialog(category: Category) {
        val editText = EditText(requireContext())
        editText.setText(category.nombre)
        AlertDialog.Builder(requireContext())
            .setTitle("Editar Categoría")
            .setView(editText)
            .setPositiveButton("Guardar") { _, _ ->
                val newName = editText.text.toString()
                if (newName.isNotBlank()) {
                    viewModel.updateCategory(category.id, newName)
                } else {
                    Toast.makeText(requireContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDeleteCategoryConfirmationDialog(category: Category) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Categoría")
            .setMessage("¿Estás seguro de que deseas eliminar esta categoría?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteCategory(category)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
