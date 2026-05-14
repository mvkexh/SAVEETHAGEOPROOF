package com.example.saveethageotag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saveethageotag.ui.theme.SaveethaGeotagTheme
import com.example.saveethageotag.ui.viewmodels.ThemeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.saveethageotag.ui.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onAboutClick: () -> Unit = {},
    themeViewModel: ThemeViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val isDarkMode by themeViewModel.isDarkMode
    val biometricEnabled by settingsViewModel.biometricEnabled
    val encryptionEnabled by settingsViewModel.encryptionEnabled
    val notificationsEnabled by settingsViewModel.notificationsEnabled
    val locationMetadataEnabled by settingsViewModel.locationMetadataEnabled
    val timestampEnabled by settingsViewModel.timestampEnabled

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Settings", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SettingsCategory("App Theme")
            SettingsToggleItem(
                icon = Icons.Default.DarkMode,
                title = "Dark Mode",
                subtitle = "Toggle between light and dark theme",
                checked = isDarkMode,
                onCheckedChange = { themeViewModel.setDarkMode(it) }
            )
            
            SettingsCategory("Security")
            SettingsToggleItem(
                icon = Icons.Default.Lock,
                title = "Biometric Lock",
                subtitle = "Secure app with fingerprint or face",
                checked = biometricEnabled,
                onCheckedChange = { settingsViewModel.toggleBiometric(it) }
            )
            SettingsToggleItem(
                icon = Icons.Default.Security,
                title = "Data Encryption",
                subtitle = "Manage how your photos are secured",
                checked = encryptionEnabled,
                onCheckedChange = { settingsViewModel.toggleEncryption(it) }
            )
            
            SettingsCategory("Notifications")
            SettingsToggleItem(
                icon = Icons.Default.Notifications,
                title = "Verification Alerts",
                subtitle = "Get notified when verification is complete",
                checked = notificationsEnabled,
                onCheckedChange = { settingsViewModel.toggleNotifications(it) }
            )
            
            SettingsCategory("Camera Settings")
            SettingsToggleItem(
                icon = Icons.Default.GpsFixed,
                title = "Location Metadata",
                subtitle = "Capture GPS coordinates with every photo",
                checked = locationMetadataEnabled,
                onCheckedChange = { settingsViewModel.toggleLocationMetadata(it) }
            )
            SettingsToggleItem(
                icon = Icons.Default.Schedule,
                title = "Timestamp Overlay",
                subtitle = "Show date and time on captured images",
                checked = timestampEnabled,
                onCheckedChange = { settingsViewModel.toggleTimestamp(it) }
            )
            
            SettingsCategory("Support")
            SettingsClickItem(Icons.Default.Help, "Help Center", "FAQs and troubleshooting")
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Replaced Logout with About App button as requested
            Button(
                onClick = onAboutClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("About App", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SettingsCategory(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 12.dp)
    )
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.tertiary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
fun SettingsClickItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
        
        Icon(
            Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SaveethaGeotagTheme {
        SettingsScreen()
    }
}
