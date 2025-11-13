package com.example.ksaturno.instalaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R

class InstallationChecklistFragment : Fragment(), InstallationChecklistAdapter.ChecklistItemListener {

    private lateinit var viewModel: InstallationChecklistViewModel
    private lateinit var adapter: InstallationChecklistAdapter
    private val args: InstallationChecklistFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_installation_checklist, container, false)
        
        // Get installationId from navigation arguments
        val installationId = args.installationId

        // Setup ViewModel with the factory
        val factory = InstallationChecklistViewModelFactory(installationId)
        viewModel = ViewModelProvider(this, factory).get(InstallationChecklistViewModel::class.java)

        // Setup RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_checklist) // You need to add this ID to your XML
        adapter = InstallationChecklistAdapter(emptyList(), this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Setup Observers
        viewModel.masterChecklistItems.observe(viewLifecycleOwner) {
            adapter.updateItems(it)
        }
        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_LONG).show()
        }

        // Setup Navigation
        view.findViewById<Button>(R.id.button_next_to_evidence).setOnClickListener {
            val action = InstallationChecklistFragmentDirections.actionStep2ToStep3(installationId)
            findNavController().navigate(action)
        }

        return view
    }

    override fun onItemStateChanged(itemId: Int, isChecked: Boolean, comments: String) {
        // Notify the ViewModel to save the state
        viewModel.saveItemState(itemId, isChecked, comments)
    }
}
