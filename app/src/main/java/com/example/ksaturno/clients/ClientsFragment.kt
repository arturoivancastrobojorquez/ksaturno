package com.example.ksaturno.clients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class ClientsFragment : Fragment() {

    private lateinit var viewModel: ClientsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_clients, container, false)

        val repository = ClientsRepository(RetrofitClient.instance)
        val factory = ClientsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ClientsViewModel::class.java)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_clients)
        val adapter = ClientsAdapter(emptyList(), { onEditClient(it) }, { onDeleteClient(it) })
        
        // --- FIX: Set the LayoutManager ---
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.clients.observe(viewLifecycleOwner) {
            adapter.updateClients(it)
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<FloatingActionButton>(R.id.fab_add_client).setOnClickListener {
            showAddOrEditClientDialog(null)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Clientes"
    }

    private fun onEditClient(client: Client) {
        showAddOrEditClientDialog(client)
    }

    private fun onDeleteClient(client: Client) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Cliente")
            .setMessage("¿Estás seguro de que deseas eliminar este cliente?")
            .setPositiveButton("Eliminar") { _, _ -> viewModel.deleteClient(client) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showAddOrEditClientDialog(client: Client?) {
        val context = requireContext()
        val isEditMode = client != null

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
        }

        val nameEditText = EditText(context).apply { hint = "Nombre" }
        val addressEditText = EditText(context).apply { hint = "Dirección" }
        val phoneEditText = EditText(context).apply { hint = "Teléfono" }
        val emailEditText = EditText(context).apply { hint = "Email" }
        val representativeEditText = EditText(context).apply { hint = "Representante" }

        layout.addView(nameEditText)
        layout.addView(addressEditText)
        layout.addView(phoneEditText)
        layout.addView(emailEditText)
        layout.addView(representativeEditText)

        if (isEditMode) {
            nameEditText.setText(client?.name)
            addressEditText.setText(client?.address)
            phoneEditText.setText(client?.phone)
            emailEditText.setText(client?.email)
            representativeEditText.setText(client?.representative)
        }

        AlertDialog.Builder(context)
            .setTitle(if (isEditMode) "Editar Cliente" else "Nuevo Cliente")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                if (isEditMode) {
                    val updatedClient = client!!.copy(
                        name = nameEditText.text.toString(),
                        address = addressEditText.text.toString(),
                        phone = phoneEditText.text.toString(),
                        email = emailEditText.text.toString(),
                        representative = representativeEditText.text.toString()
                    )
                    viewModel.updateClient(updatedClient)
                } else {
                    val request = CreateClientRequest(
                        name = nameEditText.text.toString(),
                        address = addressEditText.text.toString(),
                        phone = phoneEditText.text.toString(),
                        email = emailEditText.text.toString(),
                        representative = representativeEditText.text.toString()
                    )
                    viewModel.createClient(request)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
