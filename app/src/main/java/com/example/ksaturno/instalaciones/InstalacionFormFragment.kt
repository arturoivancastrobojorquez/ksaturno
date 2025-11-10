package com.example.ksaturno.instalaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ksaturno.databinding.FragmentInstalacionFormBinding
import com.example.ksaturno.servicios.Servicio
import com.example.ksaturno.technicians.Technician

class InstalacionFormFragment : Fragment() {

    private var _binding: FragmentInstalacionFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InstalacionFormViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInstalacionFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(InstalacionFormViewModel::class.java)

        viewModel.fetchSpinnerData()

        // The observer for units has been removed.
        viewModel.servicios.observe(viewLifecycleOwner) { setupSpinner(binding.spinnerServicio, it.map { s -> s.tipo }) }
        viewModel.tecnicos.observe(viewLifecycleOwner) { setupSpinner(binding.spinnerTecnico, it.map { t -> t.nombre }) }

        viewModel.saveResult.observe(viewLifecycleOwner) {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            if (it.success) {
                findNavController().popBackStack()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }

        binding.btnGuardarInstalacion.setOnClickListener { saveInstalacion() }
    }

    private fun setupSpinner(spinner: Spinner, data: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun saveInstalacion() {
        // The Unit object is no longer needed here.
        val servicio = binding.spinnerServicio.selectedItem as? Servicio
        val tecnico = binding.spinnerTecnico.selectedItem as? Technician

        val request = CreateInstalacionRequest(
            idServicio = servicio?.idServicio ?: 0,
            // The idUnidad field has been removed.
            idTecnico = tecnico?.idTecnico ?: 0,
            fechaInstalacion = binding.etFechaInstalacion.text.toString(),
            componentesInstalados = binding.etComponentes.text.toString(),
            estado = binding.etEstado.text.toString(),
            comentarios = binding.etComentarios.text.toString()
        )

        // Basic validations
        if (request.idServicio == 0 || request.idTecnico == 0) {
            Toast.makeText(context, "Por favor, selecciona servicio y técnico", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.saveInstalacion(request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
