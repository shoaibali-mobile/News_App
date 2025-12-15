package com.shoaib.demodatadog

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.shoaib.demodatadog.databinding.ActivityMainBinding
import com.shoaib.demodatadog.util.DatadogTracker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DatadogTracker.startScreen("main_activity", "Main Activity")

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, systemBars.bottom)
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        
        // Track fragment navigation changes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val fragmentName = when (destination.id) {
                R.id.homeFragment -> "HomeFragment"
                R.id.categoryFragment -> "CategoryFragment"
                R.id.searchFragment -> "SearchFragment"
                R.id.favoritesFragment -> "FavoritesFragment"
                else -> destination.label?.toString() ?: "Unknown"
            }
            DatadogTracker.trackFragmentSelected(fragmentName, destination.id.toString())
        }
    }

    override fun onDestroy() {
        DatadogTracker.stopScreen("main_activity")
        super.onDestroy()
    }
}
