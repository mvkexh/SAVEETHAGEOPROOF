package com.example.saveethageotag.ui.screens

import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.saveethageotag.ui.theme.SaveethaGeotagTheme
import com.example.saveethageotag.ui.viewmodels.CaptureViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import android.location.Geocoder
import java.util.Locale

@Composable
fun ARScreen(captureViewModel: CaptureViewModel?, onBack: () -> Unit, onCapture: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    // Sensor state for AR movement
    var rotationX by remember { mutableStateOf(0f) }
    var rotationY by remember { mutableStateOf(0f) }
    
    // Location state for AR tags and capture
    var currentAddress by remember { mutableStateOf("Fetching location...") }
    var currentAccuracy by remember { mutableStateOf("...") }
    var lastLocation: android.location.Location? by remember { mutableStateOf(null) }

    val sensorManager = remember { context.getSystemService(android.content.Context.SENSOR_SERVICE) as android.hardware.SensorManager }
    val rotationSensor = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ROTATION_VECTOR)

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    LaunchedEffect(Unit) {
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000
        ).build()

        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                result.lastLocation?.let { location ->
                    lastLocation = location
                    currentAccuracy = "Accuracy: ${String.format("%.1f", location.accuracy)} m"
                    
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        if (android.os.Build.VERSION.SDK_INT >= 33) {
                            geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                                if (addresses.isNotEmpty()) {
                                    currentAddress = addresses[0].getAddressLine(0)
                                }
                            }
                        } else {
                            @Suppress("DEPRECATION")
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            if (!addresses.isNullOrEmpty()) {
                                currentAddress = addresses[0].getAddressLine(0)
                            }
                        }
                    } catch (e: Exception) {
                        currentAddress = "Lat: ${String.format("%.4f", location.latitude)}, Lon: ${String.format("%.4f", location.longitude)}"
                    }
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
            Log.e("ARScreen", "Location permission missing", e)
        }
    }

    DisposableEffect(Unit) {
        val listener = object : android.hardware.SensorEventListener {
            override fun onSensorChanged(event: android.hardware.SensorEvent) {
                if (event.sensor.type == android.hardware.Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    android.hardware.SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    val orientation = FloatArray(3)
                    android.hardware.SensorManager.getOrientation(rotationMatrix, orientation)
                    
                    // Convert orientation to degrees for movement
                    rotationX = Math.toDegrees(orientation[1].toDouble()).toFloat() // Pitch
                    rotationY = Math.toDegrees(orientation[0].toDouble()).toFloat() // Azimuth
                }
            }
            override fun onAccuracyChanged(sensor: android.hardware.Sensor?, accuracy: Int) {}
        }
        
        sensorManager.registerListener(listener, rotationSensor, android.hardware.SensorManager.SENSOR_DELAY_UI)
        onDispose { sensorManager.unregisterListener(listener) }
    }

    LaunchedEffect(cameraProviderFuture) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build()
        val selector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val newImageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, newImageCapture)
            preview.setSurfaceProvider(previewView.surfaceProvider)
            imageCapture = newImageCapture
        } catch (e: Exception) {
            Log.e("ARScreen", "Camera binding failed", e)
        }
    }

    fun takePhoto() {
        val capture = imageCapture ?: return
        val location = lastLocation
        val timestamp = System.currentTimeMillis()
        val photoFile = java.io.File(context.cacheDir, "ar_capture_$timestamp.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        Log.d("ARScreen", "AR Capture: Initiating photo capture...")
        capture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d("ARScreen", "AR Capture: Photo saved: ${photoFile.absolutePath}")
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
                    Log.e("ARScreen", "AR Capture: Photo capture failed: ${exception.message}", exception)
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Camera Layer
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
            modifier = Modifier.fillMaxSize()
        )

        // AR HUD Overlay (Reacts to sensor)
        ARHUDOverlay(rotationX, rotationY)

        // Floating AR Data Tags (React to sensor)
        Box(modifier = Modifier.fillMaxSize()) {
            ARDataTag(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = ((-80) + (rotationY * 2)).dp, 
                        y = ((-120) + (rotationX * 2)).dp
                    ),
                title = "Verified Location",
                subtitle = currentAddress.take(20) + "...",
                distance = "0m"
            )
            ARDataTag(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = (100 + (rotationY * 2)).dp, 
                        y = (40 + (rotationX * 2)).dp
                    ),
                title = "GPS Node",
                subtitle = currentAccuracy,
                distance = "Live"
            )
        }

        // Bottom Controls
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                onClick = { takePhoto() },
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.3f),
                border = androidx.compose.foundation.BorderStroke(4.dp, Color.White)
            ) {
                Box(modifier = Modifier.padding(8.dp).background(Color.White, CircleShape))
            }
        }

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.background(Color.Black.copy(0.4f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Text(
                "AR VIEWER",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 2.sp
            )
            IconButton(
                onClick = {},
                modifier = Modifier.background(Color.Black.copy(0.4f), CircleShape)
            ) {
                Icon(Icons.Default.GpsFixed, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
            }
        }
    }
}

@Composable
fun ARHUDOverlay(pitch: Float = 0f, azimuth: Float = 0f) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(x = (azimuth / 2).dp, y = (pitch / 2).dp), 
        contentAlignment = Alignment.Center
    ) {
        // Center Reticle
        Canvas(modifier = Modifier.size(280.dp).rotate(rotation)) {
            drawCircle(
                color = Color.Cyan.copy(alpha = 0.3f),
                radius = size.minDimension / 2,
                style = Stroke(width = 5f)
            )
            // Compass-like ticks
            for (i in 0 until 4) {
                rotate(i * 90f) {
                    drawLine(
                        color = Color.Cyan,
                        start = center.copy(y = 0f),
                        end = center.copy(y = 40f),
                        strokeWidth = 10f
                    )
                }
            }
        }
        
        // Static inner crosshair
        Box(
            modifier = Modifier
                .size(40.dp)
                .border(1.dp, Color.White.copy(0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.size(4.dp).background(Color.White, CircleShape))
        }
    }
}

@Composable
fun ARDataTag(modifier: Modifier, title: String, subtitle: String, distance: String) {
    Card(
        modifier = modifier.width(160.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Cyan.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ViewInAr, contentDescription = null, tint = Color.Cyan, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(distance, color = Color.Cyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.White.copy(0.7f), fontSize = 10.sp)
        }
    }
}
