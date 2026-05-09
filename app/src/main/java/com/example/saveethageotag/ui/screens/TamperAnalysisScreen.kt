package com.example.saveethageotag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.saveethageotag.ui.theme.PrimaryGreen
import com.example.saveethageotag.ui.theme.SaveethaGeotagTheme
import com.example.saveethageotag.ui.theme.TextSecondary

@Composable
fun TamperAnalysisScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Black)
            }
            Text(
                "AI Analysis Report", 
                color = Color.Black, 
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
                .padding(16.dp)
        ) {
            Text("Tampering Check", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))

            TamperCheckItem("Image Metadata", "No issues found")
            TamperCheckItem("Pixel Analysis", "No manipulation detected")
            TamperCheckItem("Noise Analysis", "Consistent with original")
            TamperCheckItem("Edge Analysis", "No abnormal edits")
            TamperCheckItem("GPS Consistency", "Location is valid")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Confidence Score Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text("Confidence Score", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Mock Circular Chart
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(8.dp, PrimaryGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("98.7%", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(24.dp))
                Text("Authentic", color = PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun TamperCheckItem(label: String, status: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(label, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(status, color = Color.Gray, fontSize = 12.sp)
            }
            Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = PrimaryGreen)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TamperAnalysisScreenPreview() {
    SaveethaGeotagTheme {
        TamperAnalysisScreen()
    }
}
