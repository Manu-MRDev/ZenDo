plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "mx.com.virtualhand.zendo"
    compileSdk = 36

    defaultConfig {
        applicationId = "mx.com.virtualhand.zendo"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ✅ Compose BoM (estable y compatible con Material3)
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))

    // ✅ Material 3 (compatible con SmallTopAppBar)
    implementation("androidx.compose.material3:material3:1.1.1")

    // ✅ Material Icons extendidos
    implementation("androidx.compose.material:material-icons-extended")

    // ✅ UI Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // ✅ Firebase BoM y módulos
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics")

    // ✅ Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // ✅ Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // ✅ SplashScreen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // ✅ Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

