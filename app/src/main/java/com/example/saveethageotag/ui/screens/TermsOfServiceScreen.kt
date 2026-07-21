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
fun TermsOfServiceScreen(onBack: () -> Unit) {
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
                Text("Agreement to Terms", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("By using Saveetha Geotag, you agree to these terms and our Privacy Policy. If you do not agree, please do not use the application.")
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Permitted Use", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("You may use Saveetha Geotag for personal or professional image verification. You agree not to use the app for any illegal purposes or to attempt to forge verification data.")

                Spacer(modifier = Modifier.height(24.dp))

                Text("Intellectual Property", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("The Saveetha Geotag application and its technology are the property of SIMATS ENGINEERING. The verified images you create remain your property.")

                Spacer(modifier = Modifier.height(24.dp))

                Text("Disclaimer of Warranties", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Saveetha Geotag is provided \"as is\" without warranty of any kind. While we strive for 100% accuracy, we are not responsible for errors in GPS data provided by the device hardware.")
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
