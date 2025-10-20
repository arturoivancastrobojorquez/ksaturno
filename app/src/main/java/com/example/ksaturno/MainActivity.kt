package com.example.ksaturno

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.ksaturno.categories.CategoriesFragment
import com.example.ksaturno.units.UnitsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_home, R.id.navigation_clients -> {
                supportActionBar?.title = item.title
                Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
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
                return false // Important: Do not consume the event
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
                else -> {
                    Toast.makeText(this, menuItem.title, Toast.LENGTH_SHORT).show()
                    true
                }
            }
        }
        popupMenu.show()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}
