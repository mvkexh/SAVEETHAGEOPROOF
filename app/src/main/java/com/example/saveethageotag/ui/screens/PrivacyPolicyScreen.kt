package com.example.saveethageotag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Last Updated: May 2026", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))

            Text("1. Information We Collect", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("We collect location data (GPS coordinates), timestamps, and image metadata to provide the core verification service. We also collect authentication details via Firebase.")
            
            Spacer(modifier = Modifier.height(12.dp))
            Text("2. How We Use Data", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Data is used solely to verify the authenticity of captures and to prevent tampering. We do not sell your personal data to third parties.")
            
            Spacer(modifier = Modifier.height(12.dp))
            Text("3. Data Security", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("We use industry-standard encryption and secure cloud storage (Firebase) to protect your information.")
            
            Spacer(modifier = Modifier.height(12.dp))
            Text("4. Your Rights", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("You have the right to access, update, or delete your account and associated verification records at any time.")
        }
    }
}
