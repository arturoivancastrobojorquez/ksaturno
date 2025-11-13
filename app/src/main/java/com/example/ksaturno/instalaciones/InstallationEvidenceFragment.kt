package com.example.ksaturno.instalaciones

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ksaturno.R
import com.example.ksaturno.checklist.CompletedChecklistItem
import java.io.File

class InstallationEvidenceFragment : Fragment(), InstallationEvidenceAdapter.EvidenceItemListener {

    private lateinit var viewModel: InstallationEvidenceViewModel
    private lateinit var adapter: InstallationEvidenceAdapter
    private val args: InstallationEvidenceFragmentArgs by navArgs()

    private var latestTmpUri: Uri? = null
    private var currentChecklistItem: CompletedChecklistItem? = null

    // Modern way to handle camera results
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                currentChecklistItem?.let {
                    // Photo taken successfully, now save the evidence record
                    viewModel.saveEvidence(it.idListaVerificacion, uri, "Descripción de la foto")
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_installation_evidence, container, false)
        
        val installationId = args.installationId
        val factory = InstallationEvidenceViewModelFactory(installationId)
        viewModel = ViewModelProvider(this, factory).get(InstallationEvidenceViewModel::class.java)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_evidence) // Add this ID to your XML
        adapter = InstallationEvidenceAdapter(emptyList(), this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        setupObservers()

        view.findViewById<Button>(R.id.button_finish_installation).setOnClickListener {
            // Navigate back to the main installations list or another destination
            findNavController().popBackStack(R.id.new_installation, false)
        }

        return view
    }

    private fun setupObservers() {
        viewModel.evidencePendingItems.observe(viewLifecycleOwner) {
            adapter.updateItems(it)
        }
        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_LONG).show()
        }
        viewModel.saveSuccess.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(requireContext(), "Evidencia guardada correctamente", Toast.LENGTH_SHORT).show()
                // TODO: You could refresh the list or update the item's UI state here
                viewModel.onSaveHandled()
            }
        }
    }

    override fun onTakePhotoClick(item: CompletedChecklistItem) {
        currentChecklistItem = item
        
        // Create a temporary file to store the image
        val tmpFile = File.createTempFile("evidence_", ".png", requireContext().cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        // Get a content URI for the file using a FileProvider
        latestTmpUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            tmpFile
        )

        // Launch the camera
        takePictureLauncher.launch(latestTmpUri)
    }
}
