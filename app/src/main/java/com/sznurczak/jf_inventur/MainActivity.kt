package com.sznurczak.jf_inventur

import android.content.Context
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
import com.sznurczak.jf_inventur.data.repository.DatabaseRepository
import com.sznurczak.jf_inventur.ui.viewmodels.InventoryViewModel
import com.sznurczak.jf_inventur.data.ScannerManager
import com.sznurczak.jf_inventur.ui.viewmodels.SettingsViewModel
import com.sznurczak.jf_inventur.ui.viewmodels.UserViewModel
import com.sznurczak.jf_inventur.ui.screens.MainScreen
import com.sznurczak.jf_inventur.ui.screens.SettingsScreen
import com.sznurczak.jf_inventur.ui.screens.UserListScreen
import com.sznurczak.jf_inventur.ui.theme.JF_InventurTheme
import com.sznurczak.jf_inventur.ui.viewmodels.ScanViewModel
import kotlin.jvm.java

class MainActivity : ComponentActivity()
{
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var scannerManager: ScannerManager

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        scannerManager = ScannerManager(nfcAdapter)

        setContent {
            JF_InventurTheme {
                val navController = rememberNavController()

                val app = (LocalContext.current.applicationContext as InventoryApplication)

                val inventoryViewModel: InventoryViewModel = viewModel(
                    factory = AppViewModelFactory(app.repository)
                )
                val userViewModel: UserViewModel = viewModel(
                    factory = AppViewModelFactory(app.repository)
                )

                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModelFactory(this)
                )

                val scanViewModel: ScanViewModel = viewModel(
                    factory = AppViewModelFactory(app.repository)
                )

                val scannedData by scannerManager.scannedData.collectAsState()

                // Bridge the scanner to the ViewModel
                androidx.compose.runtime.LaunchedEffect(scannedData) {
                    scannedData?.let { scanViewModel.onScan(it) }
                }



                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = {
                                Text(
                                    text = stringResource(R.string.app_name),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        )
                    },
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
                            MainScreen(
                                settingviewModel = settingsViewModel,
                                scanViewModel = scanViewModel,
                                inventoryViewModel = inventoryViewModel,
                                userViewModel = userViewModel,
                                onClear = {
                                    scanViewModel.clearScan()
                                    scannerManager.clearScan()
                                }
                            )
                        }
                        composable("userList") {
                            UserListScreen(InventoryViewModel = inventoryViewModel, UserViewModel = userViewModel)
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
        NavigationItem(route = "userList", label = stringResource(R.string.user_management_title), icon = Icons.Default.Person),
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


@Suppress("UNCHECKED_CAST")
class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        return if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            SettingsViewModel(context) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

@Suppress("UNCHECKED_CAST")
// A generic factory to handle your new ViewModels
class AppViewModelFactory(private val repository: DatabaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(InventoryViewModel::class.java) -> InventoryViewModel(repository) as T
            modelClass.isAssignableFrom(UserViewModel::class.java) -> UserViewModel(repository) as T
            modelClass.isAssignableFrom(ScanViewModel::class.java) -> ScanViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}