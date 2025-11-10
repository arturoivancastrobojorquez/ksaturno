package com.example.ksaturno.instalaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ksaturno.R
import com.example.ksaturno.databinding.FragmentInstalacionesBinding

class InstalacionesFragment : Fragment() {

    private var _binding: FragmentInstalacionesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InstalacionesViewModel
    private lateinit var instalacionesAdapter: InstalacionesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInstalacionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(InstalacionesViewModel::class.java)

        setupRecyclerView()

        // Observe the new, display-ready LiveData
        viewModel.instalacionesDisplay.observe(viewLifecycleOwner) {
            instalacionesAdapter.updateData(it)
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }

        binding.fabAddInstalacion.setOnClickListener {
            findNavController().navigate(R.id.action_installations_to_form)
        }

        // No need to call fetchInstalaciones() here if it's called in the init block of the ViewModel
    }

    private fun setupRecyclerView() {
        instalacionesAdapter = InstalacionesAdapter(emptyList())
        binding.rvInstalaciones.apply {
            adapter = instalacionesAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
