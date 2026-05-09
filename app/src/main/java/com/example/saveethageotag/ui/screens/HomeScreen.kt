package com.example.saveethageotag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saveethageotag.ui.theme.DarkBackground
import com.example.saveethageotag.ui.theme.PrimaryGreen
import com.example.saveethageotag.ui.theme.SaveethaGeotagTheme
import com.example.saveethageotag.ui.theme.TextSecondary

@Composable
fun HomeScreen(onCapture: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Camera Viewfinder (Placeholder)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp, bottom = 140.dp)
                .background(Color(0xFF0A0A0A))
        ) {
            // Viewfinder Grid
            Box(modifier = Modifier.fillMaxSize()) {
                HorizontalDivider(modifier = Modifier.align(Alignment.Center).offset(y = (-80).dp), color = Color.White.copy(0.1f))
                HorizontalDivider(modifier = Modifier.align(Alignment.Center).offset(y = 80.dp), color = Color.White.copy(0.1f))
                VerticalDivider(modifier = Modifier.align(Alignment.Center).offset(x = (-80).dp), color = Color.White.copy(0.1f))
                VerticalDivider(modifier = Modifier.align(Alignment.Center).offset(x = 80.dp), color = Color.White.copy(0.1f))
            }

            // Top Status Badge
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .background(Color.Black.copy(0.6f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).background(PrimaryGreen, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Live + GPS Connected", color = Color.White, fontSize = 12.sp)
            }
        }

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
            }
            Text("Capture Photo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            IconButton(onClick = {}) {
                Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color.White)
            }
        }

        // Bottom Panel
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(DarkBackground)
                .padding(bottom = 16.dp)
        ) {
            // Location Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Saveetha Nagar, Thandalam, Chennai, Tamil Nadu 602105, India",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Accuracy: 6.5 m  ± 3.0 m",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Text("Gallery", color = TextSecondary, fontSize = 10.sp)
                }

                Surface(
                    onClick = onCapture,
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = androidx.compose.foundation.BorderStroke(4.dp, Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .background(Color.White, CircleShape)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.FlipCameraIos, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Text("Switch Camera", color = TextSecondary, fontSize = 10.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SaveethaGeotagTheme {
        HomeScreen {}
    }
}
