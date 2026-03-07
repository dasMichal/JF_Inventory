package com.sznurczak.jf_inventur

import android.R.attr.port
import android.content.pm.PackageManager
import android.net.http.SslCertificate.restoreState
import android.net.http.SslCertificate.saveState
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sznurczak.jf_inventur.data.ScannerManager
import com.sznurczak.jf_inventur.data.SettingsViewModel
import com.sznurczak.jf_inventur.data.TagData
import com.sznurczak.jf_inventur.ui.MainScreen
import com.sznurczak.jf_inventur.ui.SettingsScreen
import com.sznurczak.jf_inventur.ui.theme.JF_InventurTheme

class MainActivity : ComponentActivity()
{
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var scannerManager: ScannerManager

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        scannerManager = ScannerManager(nfcAdapter)

        setContent {
            JF_InventurTheme {
                val navController = rememberNavController()

                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModelFactory(this)
                )

                val scannedData by scannerManager.scannedData.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigationBar(navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "main",
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        composable("main") {
                            MainScreen(viewModel = settingsViewModel, scannedData = scannedData)
                        }
                        composable("settings") {
                           SettingsScreen(viewModel = settingsViewModel)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        scannerManager.startScanning(this)
    }

    override fun onPause() {
        super.onPause()
        scannerManager.stopScanning(this)
    }
}


@Composable
fun BottomNavigationBar(navController: NavHostController)
{
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val navigationItems = listOf(
        NavigationItem(route = "main", label = "Main", icon = Icons.Default.Home),
        NavigationItem(route = "settings", label = "Settings", icon = Icons.Default.Settings),
    )

    NavigationBar {
        navigationItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            )
        }
    }
}

data class NavigationItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier)
{
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview()
{
    JF_InventurTheme {
        Greeting("Android")
    }
}



class SettingsViewModelFactory(val activity: com.sznurczak.jf_inventur.MainActivity) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        return SettingsViewModel(activity) as T
    }
}