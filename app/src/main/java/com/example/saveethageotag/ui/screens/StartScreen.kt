package com.example.saveethageotag.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saveethageotag.R
import com.example.saveethageotag.ui.theme.PrimaryGreen
import com.example.saveethageotag.ui.theme.SaveethaGeotagTheme

@Composable
fun StartScreen(onGetStarted: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // SAVEETHA LOGO
        Image(
            painter = painterResource(id = R.drawable.saveetha_logo),
            contentDescription = "Saveetha Logo",

            modifier = Modifier
                .size(220.dp)
                .offset(y = 20.dp),

            contentScale = ContentScale.Fit
        )

        // REMOVE EXTRA GAP
        Spacer(modifier = Modifier.height((-20).dp))

        // APP NAME
        Text(
            text = "GeoProof",

            fontSize = 44.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(2.dp))

        // TAGLINE
        Text(
            text = "Capture. Verify. Trust.",

            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        // DESCRIPTION
        Text(
            text = "AI-Powered Geo-Tagged\nImage Verification",

            fontSize = 16.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

        // BOTTOM GLOBE ICON
        Box(
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = null,

                tint = Color(0xFFE5E7EB),

                modifier = Modifier.size(170.dp)
            )

            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,

                tint = PrimaryGreen,

                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // BUTTON
        Button(
            onClick = onGetStarted,

            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),

            shape = RoundedCornerShape(14.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen
            )
        ) {

            Text(
                text = "Get Started",

                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {

    SaveethaGeotagTheme {
        StartScreen(onGetStarted = {})
    }
}