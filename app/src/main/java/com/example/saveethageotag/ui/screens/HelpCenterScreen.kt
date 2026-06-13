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
fun HelpCenterScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Help Center", fontWeight = FontWeight.Bold) },
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
            Text("Frequently Asked Questions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))

            FAQItem("What is GeoProof?", "GeoProof is an AI-powered image verification system that ensures the authenticity of photos by embedding secure GPS and time metadata.")
            FAQItem("How do I verify a photo?", "Go to the Scan screen and point your camera at the QR code embedded in a GeoProof photo, or upload the photo directly.")
            FAQItem("Is my data secure?", "Yes, we use advanced encryption and Firebase security rules to ensure your metadata and images are protected.")
            FAQItem("Can I use this offline?", "Basic capturing works offline, but verification and cloud synchronization require an active internet connection.")
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Contact Support", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Email: support@geoproof.simats.edu", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Phone: +91 044 2680 1911", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(question, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Text(answer, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        HorizontalDivider(modifier = Modifier.padding(top = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
    }
}
