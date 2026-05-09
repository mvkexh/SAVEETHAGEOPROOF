package com.example.saveethageotag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
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
fun MapScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Map Placeholder
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
        ) {
             // Mock Map Grid/Pins
             repeat(5) { index ->
                 Icon(
                     Icons.Default.LocationOn,
                     contentDescription = null,
                     tint = PrimaryGreen,
                     modifier = Modifier
                         .offset(x = (50 + index * 40).dp, y = (100 + index * 60).dp)
                         .size(32.dp)
                 )
             }
        }

        // Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Search verified locations...", color = TextSecondary, fontSize = 14.sp)
        }

        // Floating Info Card
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(PrimaryGreen.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryGreen)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Current Verification Site", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Mumbai, Maharashtra · 3 active pins", color = TextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    SaveethaGeotagTheme {
        MapScreen()
    }
}
