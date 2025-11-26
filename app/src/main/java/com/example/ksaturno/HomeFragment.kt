package com.example.ksaturno

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ksaturno.databinding.FragmentHomeBinding
import com.example.ksaturno.home.HomeViewModel
import com.example.ksaturno.home.RenewalAlertAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: RenewalAlertAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // No ViewModelProvider Factory needed if ViewModel has no arguments
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        // Set current date
        val currentDate = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale.getDefault()).format(Date())
        binding.tvCurrentDate.text = currentDate

        // Initialize adapter
        adapter = RenewalAlertAdapter(emptyList())
        binding.rvRenewalAlerts.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.renewalAlerts.observe(viewLifecycleOwner) { alerts ->
            adapter.updateData(alerts)
        }

        viewModel.totalRenewals.observe(viewLifecycleOwner) { count ->
            binding.tvTotalRenewals.text = count.toString()
        }

        viewModel.totalAmount.observe(viewLifecycleOwner) { amount ->
            binding.tvTotalAmount.text = String.format("$ %.2f", amount)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.homeProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
