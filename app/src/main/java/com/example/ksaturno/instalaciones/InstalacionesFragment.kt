package com.example.ksaturno.instalaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ksaturno.R
import com.example.ksaturno.databinding.FragmentInstalacionesBinding
import com.example.ksaturno.empresa.Empresa

class InstalacionesFragment : Fragment() {

    private var _binding: FragmentInstalacionesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InstalacionesViewModel
    private lateinit var instalacionesAdapter: InstalacionesAdapter
    private var currentInstallationForInvoice: InstalacionDisplay? = null

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
        setupObservers()

        binding.fabAddInstalacion.setOnClickListener {
            findNavController().navigate(R.id.action_new_installation_to_step1)
        }
    }

    private fun setupRecyclerView() {
        instalacionesAdapter = InstalacionesAdapter(
            emptyList(),
            onEditClick = { instalacion ->
                val bundle = Bundle().apply {
                    putInt("installationId", instalacion.idInstalacion)
                }
                findNavController().navigate(R.id.action_InstalacionesFragment_to_editInstalacionFragment, bundle)
            },
            onDeleteClick = { instalacion ->
                showDeleteConfirmationDialog(instalacion)
            },
            onGenerateInvoiceClick = { instalacion ->
                currentInstallationForInvoice = instalacion
                viewModel.loadEmpresasForInvoice()
            }
        )
        binding.rvInstalaciones.apply {
            adapter = instalacionesAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
    
    private fun setupObservers() {
        viewModel.instalacionesDisplay.observe(viewLifecycleOwner) {
            instalacionesAdapter.updateData(it)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Error")
                    .setMessage(errorMessage)
                    .setPositiveButton("Entendido", null)
                    .show()
            }
        }

        viewModel.deleteResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Instalación eliminada correctamente", Toast.LENGTH_SHORT).show()
                viewModel.fetchInstalaciones()
            }
        }
        
        viewModel.invoiceResult.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Facturación")
                    .setMessage(message)
                    .setPositiveButton("Aceptar", null)
                    .show()
                viewModel.clearInvoiceResult()
            }
        }
        
        viewModel.availableEmpresas.observe(viewLifecycleOwner) { empresas ->
             if (empresas.isNotEmpty() && currentInstallationForInvoice != null) {
                 showCompanySelectionDialog(currentInstallationForInvoice!!, empresas)
                 currentInstallationForInvoice = null 
             } else if (currentInstallationForInvoice != null) {
                 Toast.makeText(context, "No hay empresas registradas para facturar", Toast.LENGTH_LONG).show()
                 currentInstallationForInvoice = null
             }
        }
    }

    private fun showDeleteConfirmationDialog(instalacion: InstalacionDisplay) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Instalación")
            .setMessage("¿Estás seguro de que deseas eliminar la instalación ID ${instalacion.idInstalacion}?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteInstalacion(instalacion.idInstalacion)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun showCompanySelectionDialog(instalacion: InstalacionDisplay, empresas: List<Empresa>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, empresas.map { it.nombre })
        
        AlertDialog.Builder(requireContext())
            .setTitle("Seleccionar Empresa")
            .setAdapter(adapter) { _, which ->
                val selectedEmpresa = empresas[which]
                showInvoiceConfirmation(instalacion, selectedEmpresa)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun showInvoiceConfirmation(instalacion: InstalacionDisplay, empresa: Empresa) {
        AlertDialog.Builder(requireContext())
            // Título personalizado con el nombre de la empresa para mayor claridad
            .setTitle("Facturar a: ${empresa.nombre}") 
            .setMessage("Se generará la factura con el folio actual de esta empresa.\n\n" +
                    "• La instalación pasará a 'completada'.\n" +
                    "• Se creará el registro en facturas.\n" +
                    "• Se actualizará el folio de la empresa.\n\n" +
                    "¿Deseas continuar?")
            .setPositiveButton("Sí, Generar") { _, _ ->
                viewModel.generateInvoice(instalacion.idInstalacion, empresa.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
