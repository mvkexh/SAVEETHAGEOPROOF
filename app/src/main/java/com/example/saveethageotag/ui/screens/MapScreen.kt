package com.example.saveethageotag.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.saveethageotag.ui.viewmodels.CapturesViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(viewModel: CapturesViewModel = viewModel()) {
    val context = LocalContext.current
    val captures by viewModel.captures
    val TAG = "GeoProof_Map"
    
    // Default location (Center of India)
    val defaultPos = LatLng(20.5937, 78.9629)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPos, 5f)
    }

    // Effect to update camera when captures are loaded
    LaunchedEffect(captures) {
        if (captures.isNotEmpty()) {
            val first = captures.first()
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(first.latitude, first.longitude), 12f
            )
        }
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (hasLocationPermission) {
            Log.d(TAG, "Location permission granted")
        } else {
            Log.d(TAG, "Location permission denied")
        }
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            Log.d(TAG, "Fetching current location...")
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    Log.d(TAG, "location fetched: ${location.latitude}, ${location.longitude}")
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLatLng, 15f)
                } else {
                    Log.d(TAG, "Current location is null")
                }
            }.addOnFailureListener {
                Log.e(TAG, "Failed to fetch location", it)
            }
        } else {
            launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Real Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission,
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = true
            ),
            onMapLoaded = {
                Log.d(TAG, "map loaded successfully")
            }
        ) {

            captures.forEach { capture ->
                val displayCode = capture.verificationCode.ifEmpty { capture.id.takeLast(8).uppercase() }
                Marker(
                    state = MarkerState(position = LatLng(capture.latitude, capture.longitude)),
                    title = "Verified: $displayCode",
                    snippet = "Address: ${capture.address}",
                    onClick = {
                        Log.d(TAG, "Marker clicked: $displayCode")
                        false
                    }
                )
            }
        }

        // Search Bar Overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Search verified locations...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        }

        // Info Card
        if (!hasLocationPermission) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    "Location permission is required to show your position.",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 12.sp
                )
            }
        }
    }
}
