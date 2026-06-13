package com.example.saveethageotag

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.saveethageotag.ui.theme.SaveethaGeotagTheme
import com.example.saveethageotag.ui.theme.LogoBlue
import com.example.saveethageotag.ui.viewmodels.ThemeViewModel
import com.example.saveethageotag.ui.viewmodels.CaptureViewModel
import com.example.saveethageotag.ui.viewmodels.CapturesViewModel
import com.example.saveethageotag.data.firebase.FirebaseManager
import com.example.saveethageotag.ui.screens.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Start : Screen("start", Icons.Default.RocketLaunch, "Start")
    object VerifyCode : Screen("verify_code", Icons.Default.Shield, "Verify")
    object Scan : Screen("scan", Icons.Default.QrCodeScanner, "Scan")
    object AR : Screen("ar", Icons.Default.ViewInAr, "AR")
    object Dashboard : Screen("dashboard", Icons.Default.Dashboard, "Stats")
    object Captures : Screen("captures", Icons.Default.History, "History")
    object About : Screen("about", Icons.Default.Info, "About")
    
    // Non-bottom-bar screens
    object Home : Screen("home", Icons.Default.CameraAlt, "Capture")
    object Preview : Screen("preview", Icons.Default.Preview, "Preview")
    object Verified : Screen("verified/{verificationId}", Icons.Default.Verified, "Verified") {
        fun createRoute(verificationId: String) = "verified/$verificationId"
    }
    object Details : Screen("details/{verificationId}", Icons.Default.Info, "Details") {
        fun createRoute(verificationId: String) = "details/$verificationId"
    }
    object TamperAnalysis : Screen("tamper_analysis", Icons.Default.BugReport, "Analysis")
    object Settings : Screen("settings", Icons.Default.Settings, "Settings")
    
    // Support screens
    object HelpCenter : Screen("help_center", Icons.AutoMirrored.Filled.Help, "Help")
    object PrivacyPolicy : Screen("privacy_policy", Icons.Default.PrivacyTip, "Privacy")
    object TermsOfService : Screen("terms_of_service", Icons.Default.Gavel, "Terms")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Backend health check (FastAPI connection test)
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val response = URL("http://10.0.2.2:8000/health").readText()
                    Log.d("BACKEND_TEST", "FastAPI Connected: $response")
                }
            } catch (e: Exception) {
                Log.e("BACKEND_TEST", "FastAPI Connection Failed: ${e.message}")
            }
        }

        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val captureViewModel: CaptureViewModel = viewModel()
            val capturesViewModel: CapturesViewModel = viewModel()
            val isDarkMode by themeViewModel.isDarkMode

            val firebaseManager = remember { FirebaseManager(applicationContext) }

            LaunchedEffect(Unit) {
                firebaseManager.signInAnonymously { }
            }
            
            SaveethaGeotagTheme(darkTheme = isDarkMode) {
                MainApp(themeViewModel, captureViewModel, capturesViewModel)
            }
        }
    }
}

@Composable
fun MainApp(themeViewModel: ThemeViewModel, captureViewModel: CaptureViewModel, capturesViewModel: CapturesViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Dashboard,
        Screen.VerifyCode,
        Screen.Scan,
        Screen.Captures,
        Screen.Settings
    )

    fun navigateToVerified(id: String) {
        navController.navigate(Screen.Verified.createRoute(id))
    }

    Scaffold(
        bottomBar = {
            if (currentDestination?.route != Screen.Start.route) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentDestination?.hierarchy?.any { 
                            it.route?.split("/")?.firstOrNull() == screen.route.split("/").firstOrNull() 
                        } == true
                        
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.label, fontSize = 10.sp) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = LogoBlue,
                                selectedTextColor = LogoBlue,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = LogoBlue.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Start.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(700)) },
            exitTransition = { fadeOut(animationSpec = tween(700)) }
        ) {
            composable(Screen.Start.route) { 
                StartScreen { 
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Start.route) { inclusive = true }
                    } 
                } 
            }
            composable(Screen.Home.route) { HomeScreen(
                captureViewModel = captureViewModel,
                onCapture = { navController.navigate(Screen.Preview.route) },
                onARClick = { navController.navigate(Screen.AR.route) },
                onGalleryClick = { navController.navigate(Screen.Captures.route) },
                onMenuClick = { navController.navigate(Screen.Settings.route) }
            ) }
            composable(Screen.Preview.route) { PreviewScreen(
                captureViewModel = captureViewModel,
                onConfirm = { id -> navigateToVerified(id) }
            ) }
            composable(
                Screen.Verified.route,
                arguments = listOf(navArgument("verificationId") { type = NavType.StringType })
            ) { backStackEntry ->
                val verificationId = backStackEntry.arguments?.getString("verificationId") ?: ""
                VerifiedScreen(
                    verificationId = verificationId,
                    onBack = { navController.popBackStack() },
                    onViewDetails = { 
                        navController.navigate(Screen.Details.createRoute(verificationId))
                    }
                )
            }
            composable(Screen.Details.route, arguments = listOf(navArgument("verificationId") { type = NavType.StringType })) { backStackEntry ->
                val verificationId = backStackEntry.arguments?.getString("verificationId") ?: ""
                DetailsScreen(
                    verificationId = verificationId,
                    onBack = { navController.popBackStack() },
                    onAnalysisClick = { navController.navigate(Screen.TamperAnalysis.route) }
                )
            }
            composable(Screen.AR.route) { 
                ARScreen(
                    captureViewModel = captureViewModel,
                    onBack = { navController.popBackStack() },
                    onCapture = { navController.navigate(Screen.Preview.route) }
                ) 
            }
            composable(Screen.Captures.route) { 
                CapturesScreen(
                    viewModel = capturesViewModel,
                    onCaptureClick = { id ->
                        navController.navigate(Screen.Details.createRoute(id))
                    }
                ) 
            }
            composable(Screen.Dashboard.route) { DashboardScreen(viewModel = capturesViewModel) }
            composable(Screen.Scan.route) { 
                ScanScreen(
                    onScanSuccess = { content ->
                        // Clean the scanned content
                        val id = when {
                            content.contains("ID:") -> content.substringAfter("ID:").substringBefore("|").trim()
                            content.startsWith("GP-") -> content.substringAfter("GP-").trim()
                            else -> content.trim()
                        }
                        navController.navigate(Screen.Details.createRoute(id))
                    }
                ) 
            }
            composable(Screen.VerifyCode.route) { VerifyCodeScreen(
                onBack = { navController.popBackStack() },
                onVerify = { id -> navController.navigate(Screen.Details.createRoute(id)) }
            ) }
            composable(Screen.About.route) { AboutScreen() }
            composable(Screen.TamperAnalysis.route) { TamperAnalysisScreen() }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onAboutClick = { navController.navigate(Screen.About.route) },
                    onHelpClick = { navController.navigate(Screen.HelpCenter.route) },
                    onPrivacyClick = { navController.navigate(Screen.PrivacyPolicy.route) },
                    onTermsClick = { navController.navigate(Screen.TermsOfService.route) },
                    themeViewModel = themeViewModel
                )
            }
            composable(Screen.HelpCenter.route) { HelpCenterScreen(onBack = { navController.popBackStack() }) }
            composable(Screen.PrivacyPolicy.route) { PrivacyPolicyScreen(onBack = { navController.popBackStack() }) }
            composable(Screen.TermsOfService.route) { TermsOfServiceScreen(onBack = { navController.popBackStack() }) }
        }
    }
}
