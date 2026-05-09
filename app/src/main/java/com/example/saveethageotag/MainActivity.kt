package com.example.saveethageotag

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.saveethageotag.ui.theme.SaveethaGeotagTheme
import com.example.saveethageotag.ui.theme.PrimaryGreen
import com.example.saveethageotag.ui.theme.TextSecondary
import com.example.saveethageotag.ui.screens.*

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Start : Screen("start", Icons.Default.RocketLaunch, "Start")
    object VerifyCode : Screen("verify_code", Icons.Default.Shield, "Verify")
    object Scan : Screen("scan", Icons.Default.QrCodeScanner, "Scan")
    object Captures : Screen("captures", Icons.Default.History, "History")
    object About : Screen("about", Icons.Default.Info, "About")
    
    // Non-bottom-bar screens
    object Home : Screen("home", Icons.Default.CameraAlt, "Capture")
    object Preview : Screen("preview", Icons.Default.Preview, "Preview")
    object Verified : Screen("verified", Icons.Default.Verified, "Verified")
    object Details : Screen("details", Icons.Default.Info, "Details")
    object TamperAnalysis : Screen("tamper_analysis", Icons.Default.BugReport, "Analysis")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SaveethaGeotagTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val bottomNavItems = listOf(
        Screen.VerifyCode,
        Screen.Scan,
        Screen.Captures,
        Screen.About
    )

    Scaffold(
        bottomBar = {
            if (currentDestination?.route != Screen.Start.route) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ) {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.label, fontSize = 10.sp) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
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
                                selectedIconColor = PrimaryGreen,
                                selectedTextColor = PrimaryGreen,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color.Transparent
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
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Start.route) { 
                StartScreen { 
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Start.route) { inclusive = true }
                    } 
                } 
            }
            composable(Screen.Home.route) { HomeScreen(
                onCapture = { navController.navigate(Screen.Preview.route) }
            ) }
            composable(Screen.Preview.route) { PreviewScreen(
                onConfirm = { navController.navigate(Screen.Verified.route) }
            ) }
            composable(Screen.Verified.route) { VerifiedScreen(
                onViewDetails = { navController.navigate(Screen.Details.route) }
            ) }
            composable(Screen.Details.route) { DetailsScreen() }
            composable(Screen.Scan.route) { ScanScreen() }
            composable(Screen.VerifyCode.route) { VerifyCodeScreen(
                onVerify = { navController.navigate(Screen.Verified.route) }
            ) }
            composable(Screen.Captures.route) { CapturesScreen() }
            composable(Screen.About.route) { AboutScreen() }
            composable(Screen.TamperAnalysis.route) { TamperAnalysisScreen() }
        }
    }
}
