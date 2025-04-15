plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")

    // Hilt Plugin + KAPT support
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")

}

android {
    namespace = "it.uniupo.ktt"
    compileSdk = 35

    defaultConfig {
        applicationId = "it.uniupo.ktt"
        minSdk = 29
        targetSdk = 35
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
    implementation (libs.androidx.credentials.play.services.auth.vlatestversion)
    implementation (libs.credentials)
    implementation (libs.googleid.vlatestversion)

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.google.firebase.auth)

    // Also add the dependencies for the Credential Manager libraries and specify their versions
    implementation(libs.androidx.material.icons.extended)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.material3.lint)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose.v240)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation("androidx.compose.material3:material3:1.2.0")

    //implementation("androidx.compose.ui:ui-graphics:1.3.0") //blurred background effect

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-compiler:2.51")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0") // Integrazione HiltViewModel in Composable e Navigation graph

    // REALTIME DB
    implementation("com.google.firebase:firebase-database-ktx")
}