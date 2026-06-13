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
fun TermsOfServiceScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Terms of Service", fontWeight = FontWeight.Bold) },
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
            Text("1. Acceptance of Terms", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("By using GeoProof, you agree to these terms and our Privacy Policy. If you do not agree, please do not use the service.")
            
            Spacer(modifier = Modifier.height(12.dp))
            Text("2. Proper Use", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Users must not attempt to spoof location data, bypass encryption, or submit tampered images to the verification system.")
            
            Spacer(modifier = Modifier.height(12.dp))
            Text("3. Intellectual Property", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("The GeoProof application and its technology are the property of SIMATS ENGINEERING. All rights reserved.")
            
            Spacer(modifier = Modifier.height(12.dp))
            Text("4. Termination", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("We reserve the right to suspend or terminate access for users who violate these terms.")
        }
    }
}
