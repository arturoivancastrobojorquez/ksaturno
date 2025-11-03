package com.example.ksaturno

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.ksaturno.categories.CategoriesFragment
import com.example.ksaturno.checklist.ChecklistFragment
import com.example.ksaturno.clients.ClientSearchFragment
import com.example.ksaturno.clients.ClientsFragment
import com.example.ksaturno.technicians.TechniciansFragment
import com.example.ksaturno.units.UnitsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val mainLayout: View = findViewById(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, v.paddingBottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            supportActionBar?.title = "Inicio"
            replaceFragment(Fragment()) 
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Confirmar Salida")
                        .setMessage("¿Estás seguro de que deseas cerrar la aplicación?")
                        .setPositiveButton("Sí") { _, _ ->
                            finish()
                        }
                        .setNegativeButton("No", null)
                        .show()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        supportActionBar?.title = item.title
        when (item.itemId) {
            R.id.navigation_home -> {
                replaceFragment(Fragment()) 
                return true
            }
            R.id.navigation_clients -> {
                replaceFragment(ClientsFragment())
                return true
            }
            R.id.navigation_operations, R.id.navigation_finance, R.id.navigation_more -> {
                val anchorView = bottomNavigationView.findViewById<View>(item.itemId)
                val menuRes = when (item.itemId) {
                    R.id.navigation_operations -> R.menu.operations_submenu
                    R.id.navigation_finance -> R.menu.finance_submenu
                    else -> R.menu.more_submenu
                }
                showPopupMenu(anchorView, menuRes)
                return false 
            }
        }
        return false
    }

    private fun showPopupMenu(view: View, menuRes: Int) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(menuRes, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.categories -> {
                    replaceFragment(CategoriesFragment())
                    true
                }
                R.id.units -> {
                    replaceFragment(UnitsFragment())
                    true
                }
                R.id.technicians -> {
                    replaceFragment(TechniciansFragment())
                    true
                }
                R.id.checklist_items -> {
                    replaceFragment(ChecklistFragment())
                    true
                }
                else -> {
                    Toast.makeText(this, menuItem.title, Toast.LENGTH_SHORT).show()
                    true
                }
            }
        }
        popupMenu.show()
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
        
        if (fragment is ClientSearchFragment) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }
}
