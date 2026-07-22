# SAVEETHA GeoProof

## Secure Geo-Tagged Photo Verification System

SAVEETHA GeoProof is an Android application developed for Saveetha Institute of Medical and Technical Sciences (SIMATS) to provide secure and verifiable proof of participation in academic and extracurricular activities such as internships, industrial visits, workshops, hackathons, seminars, NSS activities, project reviews, and college events.

The application captures photographs along with real-time location information and generates a unique verification system using QR codes and verification IDs, enabling users to verify the authenticity of images across devices.

---

## Features

### Geo-Tagged Photo Capture

* Capture photos directly from the application.
* Automatically records:

  * Latitude and Longitude
  * Address Information
  * Date and Time
  * Device Information
  * Unique Image ID

### Image Verification

* Generates a unique Verification Code.
* Generates a QR Code for every image.
* Helps prevent misuse of edited or manipulated images.

### Cross-Device Verification

Users can:

* Scan the QR Code.
* Enter the Verification Code.
* Verify image authenticity from any device using the GeoProof application.

### Cloud Synchronization

* Secure cloud storage of verification data.
* Enables verification from multiple devices.

### Metadata Display

Displays:

* Capture Time
* GPS Coordinates
* Address Details
* Verification Status
* Device Information

---

## System Architecture

```text
Android Application
        ↓
Image Capture Module
        ↓
Location & Metadata Collection
        ↓
QR and Verification Code Generation
        ↓
Cloud Database
        ↓
Verification Module
```

---

## Technology Stack

### Frontend

* Kotlin
* Jetpack Compose
* Material Design 3

### Backend

* FastAPI (Python)
* REST API

### Database

* Firebase Realtime Database / PostgreSQL

### Libraries

* Retrofit
* Google Play Location Services
* ZXing QR Generator
* Kotlin Coroutines
* Coil

---

## Application Modules

1. Splash Screen
2. Dashboard
3. Camera Module
4. GeoTag Preview
5. Verification Module
6. QR Scanner
7. History Module

---

## Permissions Required

```xml
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES"/>
```

---

## Installation

### Clone Repository

```bash
git clone https://github.com/shalz-collab/saveetha-geoproof.git
```

### Open in Android Studio

```text
File → Open → Select Project Folder
```

### Run Backend

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

### API Base URL

**Android Emulator**

```kotlin
http://10.0.2.2:8000/
```

**Physical Device**

```kotlin
http://YOUR_PC_IP:8000/
```

---

## Project Structure

```text
app/
├── ui/
├── screens/
├── navigation/
├── viewmodel/
├── models/
├── data/
├── network/
└── utils/
```

---

## Use Cases

* Internship Verification
* Industrial Visit Documentation
* Workshop Attendance Tracking
* Hackathon Participation Verification
* NSS Activity Recording
* Project Review Documentation
* College Event Management

---

## Security Features

* QR-Based Verification
* Unique Verification IDs
* Geo-Location Validation
* Metadata Integrity Verification
* Cloud Data Synchronization
* Cross-Device Authentication

---

## Future Enhancements

* Digital Signature Validation
* AI-Based Tampering Detection
* Offline Verification Support
* Admin Dashboard and Analytics
* PDF Report Generation
* Blockchain-Based Verification Storage

---

## Developed By

Shalini M K
B.Tech Computer Science and Engineering
Saveetha Institute of Medical and Technical Sciences (SIMATS), Chennai

GitHub: https://github.com/shalz-collab

---

## License

This project is developed for educational and research purposes under Saveetha Institute of Medical and Technical Sciences.

---

**SAVEETHA GeoProof**
Capture • Verify • Trust
