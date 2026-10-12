package com.mocii.simuladorprestamos

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.mocii.simuladorprestamos.common.utils.MCurrent
import com.mocii.simuladorprestamos.common.utils.MUtils
import com.mocii.simuladorprestamos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    var dataManager: DataManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        MCurrent.mInitApp(this)
        MUtils.setMTheme(this,0)
        //MUThemes.applyThemeColors(this)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MCurrent.mInitMainActivity(this)
        //setSupportActionBar(binding.toolbar)

        setSupportActionBar(binding.appBarMain.materialToolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        Log.d("MOCCI","navController:"+navController)
        Log.d("MOCCI","navController.graph:"+navController.graph)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        supportActionBar?.title = "Nuevo Título del Menú"
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val headerView: View = navView.getHeaderView(0) // Obtiene la vista del encabezado
        //val nameTextView: TextView = headerView.findViewById(R.id.tv_nav_view_name)
        //val stateTextView: TextView = headerView.findViewById(R.id.tv_nav_view_state)
        /*if(Current.isRemote){
            nameTextView.text = Current.getUser()?.name
        }else{
            nameTextView.text = "UNKNOWN"
        }
        stateTextView.text = Current.descState*/
        //val navController = findNavController(R.id.nav_host_fragment_content_main)
        //appBarConfiguration = AppBarConfiguration(navController.graph)
        //setupActionBarWithNavController(navController, appBarConfiguration)

        /*binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }*/
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_top, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                val bundle = Bundle()
                bundle.putString("MODE", "INS") // Puedes agregar los datos que necesites
                navController.navigate(R.id.ConfiguracionFragment, bundle)
                true
            }else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}