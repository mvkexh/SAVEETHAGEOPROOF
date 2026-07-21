package com.example.saveethageotag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Text("Last Updated: June 2026", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(24.dp))

                PolicySection("1. Information We Collect", "GeoProof collects location data (latitude, longitude), timestamps, and camera images to provide verification services. We also collect basic device information like model and OS version.")
                PolicySection("2. How We Use Data", "Your location and images are used solely to generate a digital proof of authenticity. This data is embedded in the image and optionally stored on our secure Firebase cloud if you choose to sync.")
                PolicySection("3. Data Security", "We use industry-standard encryption and Firebase Security Rules to ensure that only you can access your private captures. Verified data shared with others is strictly read-only.")
                PolicySection("4. Your Choices", "You can use GeoProof offline for local verification. Cloud synchronization is optional and can be disabled. You can delete your historical data from the app at any time.")
            }
        }

        // Floating Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.1f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(content, fontSize = 14.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}
