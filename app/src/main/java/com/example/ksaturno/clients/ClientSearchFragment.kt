package com.example.ksaturno.clients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R
import com.example.ksaturno.RetrofitClient

class ClientSearchFragment : Fragment() {

    private lateinit var viewModel: ClientSearchViewModel
    private lateinit var clientSearchAdapter: ClientSearchAdapter
    private var allClients: List<Client> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_client_search, container, false)

        val repository = ClientsRepository(RetrofitClient.instance)
        val factory = ClientSearchViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ClientSearchViewModel::class.java)

        val searchView: SearchView = view.findViewById(R.id.search_view_client)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_client_search)

        clientSearchAdapter = ClientSearchAdapter(emptyList()) { selectedClient ->
            val result = Bundle().apply {
                putInt("selectedClientId", selectedClient.id)
                putString("selectedClientName", selectedClient.name)
            }
            setFragmentResult("clientSearchRequest", result)
            parentFragmentManager.popBackStack()
        }

        recyclerView.adapter = clientSearchAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.clients.observe(viewLifecycleOwner) {
            allClients = it
            clientSearchAdapter.updateClients(it)
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = allClients.filter { client ->
                    // Safe filter: only include clients with a non-null name that contains the search text
                    client.name?.contains(newText ?: "", ignoreCase = true) ?: false
                }
                clientSearchAdapter.updateClients(filteredList)
                return true
            }
        })

        return view
    }
}

class ClientSearchAdapter(
    private var clients: List<Client>,
    private val onClientSelected: (Client) -> Unit
) : RecyclerView.Adapter<ClientSearchAdapter.ClientViewHolder>() {

    fun updateClients(newClients: List<Client>) {
        clients = newClients
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_client_search, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        holder.bind(clients[position])
    }

    override fun getItemCount(): Int = clients.size

    inner class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.text_view_client_name)

        fun bind(client: Client) {
            nameTextView.text = client.name ?: "(Nombre no disponible)"
            itemView.setOnClickListener { onClientSelected(client) }
        }
    }
}
