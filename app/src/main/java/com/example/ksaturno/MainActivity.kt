package com.example.ksaturno

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.ksaturno.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home, R.id.navigation_clients, R.id.navigation_operations, 
            R.id.navigation_finance, R.id.navigation_more))
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up the listener manually to handle popups
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            handleBottomNavigation(item)
        }

        // Handle the back button press for the exit confirmation
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.currentDestination?.id == navController.graph.startDestinationId) {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Confirmar Salida")
                        .setMessage("¿Estás seguro de que deseas cerrar la aplicación?")
                        .setPositiveButton("Sí") { _, _ -> finish() }
                        .setNegativeButton("No", null)
                        .show()
                } else {
                    // If not at the start, perform the default "up" navigation
                    navController.navigateUp()
                }
            }
        })
    }

    private fun handleBottomNavigation(item: MenuItem): Boolean {
        when (item.itemId) {
            // For items that need a popup menu
            R.id.navigation_operations, R.id.navigation_finance, R.id.navigation_more -> {
                val anchorView = binding.bottomNavigation.findViewById<View>(item.itemId)
                val menuRes = when (item.itemId) {
                    R.id.navigation_operations -> R.menu.operations_submenu
                    R.id.navigation_finance -> R.menu.finance_submenu
                    else -> R.menu.more_submenu
                }
                showPopupMenu(anchorView, menuRes)
                return false // Return false to not select the item in the bottom bar
            }
            // For direct navigation items
            else -> {
                // Use the NavigationUI helper to automatically navigate
                return NavigationUI.onNavDestinationSelected(item, navController)
            }
        }
    }

    private fun showPopupMenu(anchorView: View, menuRes: Int) {
        val popupMenu = PopupMenu(this, anchorView)
        popupMenu.menuInflater.inflate(menuRes, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            // Let the NavController handle the navigation for the selected submenu item
            val navigated = NavigationUI.onNavDestinationSelected(menuItem, navController)
            if (!navigated) {
                Toast.makeText(this, "Destino no implementado: ${menuItem.title}", Toast.LENGTH_SHORT).show()
            }
            true
        }
        popupMenu.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
