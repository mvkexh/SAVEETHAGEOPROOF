package com.example.saveethageotag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.saveethageotag.ui.theme.RedPrimary
import com.example.saveethageotag.ui.theme.SaveethaGeotagTheme
import com.example.saveethageotag.ui.theme.TextSecondary
import com.example.saveethageotag.ui.theme.SuccessGreen

data class CaptureItem(
    val id: String,
    val date: String,
    val location: String,
    val isVerified: Boolean
)

@Composable
fun CapturesScreen() {
    val captures = listOf(
        CaptureItem("GP-8291-XK92", "24 May 2024, 10:45 AM", "Chennai, India", true),
        CaptureItem("GP-7102-ML41", "23 May 2024, 02:15 PM", "Chennai, India", true),
        CaptureItem("GP-9031-QN12", "22 May 2024, 09:30 AM", "Chennai, India", false),
        CaptureItem("GP-5521-BT88", "21 May 2024, 11:20 AM", "Chennai, India", true)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            "VERIFIED CAPTURES",
            color = Color.Black,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            "Historical logs of AI-authenticated images",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(captures) { capture ->
                CaptureCard(capture)
            }
        }
    }
}

@Composable
fun CaptureCard(capture: CaptureItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(capture.id, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    if (capture.isVerified) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                    }
                }
                Text(capture.date, color = Color.Gray, fontSize = 12.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Red.copy(alpha = 0.5f), modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(capture.location, color = Color.Gray, fontSize = 10.sp)
                }
            }

            IconButton(onClick = {}) {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Black)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CapturesScreenPreview() {
    SaveethaGeotagTheme {
        CapturesScreen()
    }
}
