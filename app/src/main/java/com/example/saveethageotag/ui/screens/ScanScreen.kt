package com.example.saveethageotag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saveethageotag.ui.theme.PrimaryGreen
import com.example.saveethageotag.ui.theme.DarkBackground
import com.example.saveethageotag.ui.theme.SaveethaGeotagTheme

@Composable
fun ScanScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Camera Viewfinder (Placeholder)
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0A))
        ) {
            // QR Frame
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .align(Alignment.Center)
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            ) {
                // Corner Accents (Matching image green corners)
                val cornerSize = 40.dp
                val strokeWidth = 4.dp
                
                // Top Left
                Box(modifier = Modifier.size(cornerSize).align(Alignment.TopStart).border(strokeWidth, PrimaryGreen, RoundedCornerShape(topStart = 24.dp)))
                // Top Right
                Box(modifier = Modifier.size(cornerSize).align(Alignment.TopEnd).border(strokeWidth, PrimaryGreen, RoundedCornerShape(topEnd = 24.dp)))
                // Bottom Left
                Box(modifier = Modifier.size(cornerSize).align(Alignment.BottomStart).border(strokeWidth, PrimaryGreen, RoundedCornerShape(bottomStart = 24.dp)))
                // Bottom Right
                Box(modifier = Modifier.size(cornerSize).align(Alignment.BottomEnd).border(strokeWidth, PrimaryGreen, RoundedCornerShape(bottomEnd = 24.dp)))
            }
        }

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Text(
                "Scan QR", 
                color = Color.White, 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(top = 340.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Place the QR code within", color = Color.White, fontSize = 14.sp)
            Text("the frame", color = Color.White, fontSize = 14.sp)
        }

        // Bottom Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 48.dp, end = 48.dp, bottom = 64.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    onClick = {},
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Gallery", color = Color.White, fontSize = 12.sp)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    onClick = {},
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Flash", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScanScreenPreview() {
    SaveethaGeotagTheme {
        ScanScreen()
    }
}
