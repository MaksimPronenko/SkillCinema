package edu.skillbox.skillcinema.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.databinding.ActivityNavigationBinding

@AndroidEntryPoint
class NavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNavigationBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_navigation) as NavHostFragment
        navController = navHostFragment.navController
        setupWithNavController(binding.bottomNavigation, navController)
    }
}

//    private lateinit var appBarConfiguration: AppBarConfiguration

//        setSupportActionBar(binding.toolbar)
//        supportActionBar?.hide()
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_navigation)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }

//    fun BottomNavigationView.uncheckAllItems() {
//        menu.setGroupCheckable(0, true, false)
//        for (i in 0 until menu.size()) {
//            menu.getItem(i).isChecked = false
//        }
//        menu.setGroupCheckable(0, true, true)
//    }
