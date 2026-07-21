package com.example.saveethageotag.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.util.Log
import android.view.ViewGroup
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.compose.ui.platform.LocalInspectionMode
import com.example.saveethageotag.ui.viewmodels.CaptureViewModel

@Composable
fun HomeScreen(
    captureViewModel: CaptureViewModel?,
    onCapture: () -> Unit,
    onARClick: () -> Unit,
    onGalleryClick: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    val isPreview = LocalInspectionMode.current
    if (isPreview) {
        CameraContent(captureViewModel, onCapture, onARClick, onGalleryClick, onMenuClick)
    } else {
        HomeScreenWithPermissions(captureViewModel, onCapture, onARClick, onGalleryClick, onMenuClick)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun HomeScreenWithPermissions(
    captureViewModel: CaptureViewModel?,
    onCapture: () -> Unit,
    onARClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }

    if (permissionsState.allPermissionsGranted) {
        CameraContent(captureViewModel, onCapture, onARClick, onGalleryClick, onMenuClick)
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Permissions required to use the camera and geotagging",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
                Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                    Text("Grant Permissions")
                }
            }
        }
    }
}

@Composable
fun CameraContent(
    captureViewModel: CaptureViewModel?,
    onCapture: () -> Unit,
    onARClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val cameraProviderFuture = remember(isPreview) { 
        if (isPreview) null else ProcessCameraProvider.getInstance(context) 
    }
    
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_OFF) }
    
    var currentAddress by remember { mutableStateOf("Fetching location...") }
    var currentAccuracy by remember { mutableStateOf("...") }
    var lastLocation: android.location.Location? by remember { mutableStateOf(null) }

    val fusedLocationClient = remember(isPreview) { 
        if (isPreview) null else LocationServices.getFusedLocationProviderClient(context) 
    }

    LaunchedEffect(Unit) {
        if (isPreview || fusedLocationClient == null) return@LaunchedEffect
        
        // 1. Fetch last location IMMEDIATELY for speed
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    lastLocation = it
                    currentAccuracy = "Accuracy: ${String.format("%.1f", it.accuracy)} m"
                    updateAddress(context, it) { addr -> currentAddress = addr }
                }
            }
        } catch (e: SecurityException) {
            Log.e("GeoProof_Home", "Location error", e)
        }

        // 2. Continuous high-accuracy updates
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000
        ).setMinUpdateIntervalMillis(500).build()

        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                result.lastLocation?.let { location ->
                    lastLocation = location
                    currentAccuracy = "Accuracy: ${String.format("%.1f", location.accuracy)} m"
                    updateAddress(context, location) { addr -> currentAddress = addr }
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                android.os.Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e("GeoProof_Home", "Location error", e)
        }
    }

    val previewView = remember { PreviewView(context) }
    LaunchedEffect(lensFacing, cameraProviderFuture, flashMode) {
        if (isPreview || cameraProviderFuture == null) return@LaunchedEffect
        
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build()
        val selector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        val newImageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(flashMode)
            .build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                selector,
                preview,
                newImageCapture
            )
            preview.setSurfaceProvider(previewView.surfaceProvider)
            imageCapture = newImageCapture
        } catch (e: Exception) {
            Log.e("GeoProof_Home", "Camera bind failed", e)
        }
    }

    fun takePhoto() {
        if (isPreview) {
            onCapture()
            return
        }
        val capture = imageCapture ?: return
        
        val location = lastLocation
        val timestamp = System.currentTimeMillis()
        val photoFile = java.io.File(context.cacheDir, "capture_$timestamp.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        capture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    captureViewModel?.updateCapture(
                        file = photoFile,
                        lat = location?.latitude ?: 0.0,
                        lon = location?.longitude ?: 0.0,
                        address = currentAddress,
                        accuracy = currentAccuracy
                    )
                    onCapture()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("GeoProof_Home", "Capture failed", exception)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        if (isPreview) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 140.dp)
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Camera Preview Placeholder", color = Color.White)
            }
        } else {
            AndroidView(
                factory = { 
                    previewView.apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 140.dp)
            )
        }

        // Top Status Badge (Moved higher or integrated)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 140.dp)
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .background(Color.Black.copy(0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isLocationReady = lastLocation != null
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(if (isLocationReady) MaterialTheme.colorScheme.tertiary else Color.Red, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (isLocationReady) "Live GPS: $currentAccuracy" else "Searching for GPS...",
                    color = Color.White,
                    fontSize = 11.sp
                )
            }
        }

        // Current Location Info Panel (Overlay on Camera)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 80.dp, start = 16.dp, end = 16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = currentAddress,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.sp,
                            maxLines = 2
                        )
                        if (lastLocation != null) {
                            Text(
                                text = "Lat: ${String.format("%.6f", lastLocation?.latitude)}, Lon: ${String.format("%.6f", lastLocation?.longitude)}",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Top Controls
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape),
                onClick = {
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                        CameraSelector.LENS_FACING_FRONT
                    } else {
                        CameraSelector.LENS_FACING_BACK
                    }
                }
            ) {
                Icon(Icons.Default.FlipCameraAndroid, contentDescription = null, tint = Color.White)
            }
            
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape),
                onClick = {
                    flashMode = when (flashMode) {
                        ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                        ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                        else -> ImageCapture.FLASH_MODE_OFF
                    }
                }
            ) {
                Icon(
                    imageVector = when (flashMode) {
                        ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                        ImageCapture.FLASH_MODE_AUTO -> Icons.Default.FlashAuto
                        else -> Icons.Default.FlashOff
                    },
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        // Bottom Panel
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = onGalleryClick) {
                        Icon(Icons.Default.History, contentDescription = "History", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(32.dp))
                    }
                    Text("History", color = MaterialTheme.colorScheme.onSurface, fontSize = 11.sp)
                }

                Surface(
                    onClick = { 
                        if (lastLocation != null) {
                            takePhoto() 
                        } else {
                            android.widget.Toast.makeText(context, "Waiting for GPS...", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = androidx.compose.foundation.BorderStroke(5.dp, if (lastLocation != null) MaterialTheme.colorScheme.onSurface else Color.LightGray)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .background(if (lastLocation != null) MaterialTheme.colorScheme.onSurface else Color.LightGray, CircleShape)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = onARClick) {
                        Icon(Icons.Default.ViewInAr, contentDescription = "AR View", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(32.dp))
                    }
                    Text("AR View", color = MaterialTheme.colorScheme.onSurface, fontSize = 11.sp)
                }
            }
        }
    }
}

private fun updateAddress(context: android.content.Context, location: android.location.Location, onResult: (String) -> Unit) {
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                if (addresses.isNotEmpty()) {
                    val addr = addresses[0]
                    val fullAddr = addr.getAddressLine(0) ?: ""
                    onResult(fullAddr)
                }
            }
        } else {
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                val fullAddr = addr.getAddressLine(0) ?: ""
                onResult(fullAddr)
            }
        }
    } catch (e: Exception) {
        onResult("Lat: ${String.format("%.6f", location.latitude)}, Lon: ${String.format("%.6f", location.longitude)}")
    }
}
