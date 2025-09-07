# Iran Cargo Management - Android App

A complete Android logistics application for cargo management in Iran with Persian RTL interface.

## Features

- **Persian RTL Interface** with navy blue theme (#0B2545)
- **Login System** with access codes (DR001 for drivers, SP001 for supervisors)
- **Trip Creation and Management** with full CRUD operations
- **Room Database** for local storage of trips, users, and location data
- **Firebase Integration** ready for cloud synchronization
- **Location Tracking Service** for real-time GPS monitoring
- **QR Code Scanner** for cargo identification
- **Material Design Components** with custom Persian styling

## Project Structure

```
app/
├── src/main/
│   ├── java/com/irancargocompany/logistics/
│   │   ├── data/
│   │   │   ├── database/         # Room database, DAOs
│   │   │   └── repository/       # Repository classes
│   │   ├── model/               # Data models (Trip, User, LocationTracking)
│   │   ├── service/             # Background services
│   │   ├── ui/                  # Activities and adapters
│   │   │   ├── auth/           # Login activity
│   │   │   ├── main/           # Dashboard and trip list
│   │   │   ├── scanner/        # QR code scanner
│   │   │   └── trip/           # Trip management
│   │   └── utils/              # Helper classes
│   ├── res/
│   │   ├── layout/             # XML layouts
│   │   ├── values/             # Colors, strings, themes
│   │   ├── values-fa/          # Persian translations
│   │   ├── drawable/           # Icons and graphics
│   │   └── menu/               # Menu resources
│   └── AndroidManifest.xml
├── build.gradle                # App-level dependencies
└── google-services.json       # Firebase configuration
```

## Setup Instructions

### 1. Prerequisites
- Android Studio Arctic Fox or later
- JDK 8 or higher
- Android SDK API level 21+

### 2. Firebase Setup
1. Replace `app/google-services.json` with your Firebase project configuration
2. Update the following in `strings.xml`:
   - `google_maps_api_key` with your Google Maps API key
   - `default_web_client_id` with your Firebase web client ID

### 3. Build and Run
```bash
./gradlew assembleDebug
```

### 4. Access Codes
- **Driver**: `DR001`
- **Supervisor**: `SP001`

## Key Components

### Database Schema
- **trips**: Trip information with origin, destination, cargo details
- **users**: User accounts with access codes and types
- **location_tracking**: GPS coordinates for trip monitoring

### Main Activities
- **SplashActivity**: App initialization and session check
- **LoginActivity**: Authentication with access codes
- **MainActivity**: Dashboard with trip overview and quick actions
- **TripActivity**: Trip creation/editing form
- **QRScannerActivity**: QR code scanning for cargo identification

### Services
- **LocationTrackingService**: Background GPS tracking
- **FirebaseMessagingService**: Push notifications

## UI/UX Features

- **RTL Support**: Full right-to-left layout for Persian text
- **Navy Blue Theme**: Professional color scheme with accessibility
- **Material Design**: Modern Android UI components
- **Responsive Layout**: Works on phones and tablets
- **Dark/Light Theme**: Automatic theme support

## Permissions

The app requires the following permissions:
- Camera (QR scanning)
- Location (GPS tracking)
- Internet (Firebase sync)
- Storage (data caching)

## Development Notes

- Built with Kotlin and Android Jetpack
- Uses MVVM architecture pattern
- Room database for offline-first approach
- Coroutines for async operations
- Material Design Components
- Firebase integration ready

## Localization

The app supports:
- Persian (Farsi) - Primary language
- English - Fallback language

All UI elements are properly localized with RTL support.

## Future Enhancements

- Real-time location sharing
- Push notifications for trip updates
- Offline map caching
- Advanced analytics dashboard
- Multi-language support expansion

---

For technical support or questions, please refer to the project documentation or create an issue in the repository.