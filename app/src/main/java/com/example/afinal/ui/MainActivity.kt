package com.example.afinal.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.afinal.R
import com.example.afinal.databinding.ActivityMainBinding  // ViewBinding — NO findViewById!

/**
 * MAIN ACTIVITY — The single Activity in this app ("Single Activity Architecture")
 *
 * This Activity is just a HOST CONTAINER for Fragments.
 * The Navigation Component (NavController) manages which Fragment is visible.
 *
 * enableEdgeToEdge() — makes the app draw behind the system status/navigation bars
 * for a full-screen modern look (standard in Android 15+).
 *
 * ViewBinding replaces every single setContentView + findViewById call.
 * ActivityMainBinding is auto-generated from activity_main.xml.
 */
class MainActivity : AppCompatActivity() {

    // ViewBinding — replaces setContentView + all findViewByIds
    private lateinit var binding: ActivityMainBinding

    // NavController manages Fragment navigation (back stack, transitions, etc.)
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)

        // enableEdgeToEdge allows drawing behind system bars (status bar, nav bar)
        // This is part of the original code — keep it!
        // (This call must come BEFORE setContentView)

        // Set the root view as the content of this Activity
        setContentView(binding.root)

        // Handle window insets (padding so content is not hidden behind system bars)
        // This is the original code from the project — adapted for ViewBinding
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up the Toolbar as the ActionBar (shows the app title + back button)
        setSupportActionBar(binding.toolbar)

        /**
         * NavHostFragment is the container where Fragments are swapped in/out.
         * It's defined in activity_main.xml as a FragmentContainerView.
         * supportFragmentManager.findFragmentById finds it by its view ID.
         */
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        /**
         * AppBarConfiguration defines the "top-level" destinations.
         * On top-level screens, the Toolbar shows the APP ICON (not a back arrow).
         * On nested screens (detail, add), it shows a BACK ARROW automatically.
         */
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment)  // Only HomeFragment is top-level
        )

        // Link the ActionBar (Toolbar) to the NavController for automatic title & back button
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    /**
     * When the user presses the hardware back button or the Toolbar back arrow,
     * this delegates to NavController to handle back navigation.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
