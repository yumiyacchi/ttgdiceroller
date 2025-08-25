plugins {

    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.yumiyacchi.diceroller"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.yumiyacchi.diceroller"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true // Recommended for Compose
        }
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
        viewBinding = true // You can keep this if other parts of app use it, or set to false if fully Compose.
        compose = true // Enable Jetpack Compose
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get() // Use version from TOML
    }
    packaging { // Recommended for Compose
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat) // Keep if MainActivity extends AppCompatActivity
    implementation(libs.material)          // This is Material Design Components (XML). Compose uses material3.

    // Remove or comment out NavComponent if fully replacing with Compose Navigation,
    // or keep if MainActivity still hosts a NavHostFragment for other (XML-based) parts.
    // For now, let's assume we'll use Compose directly in MainActivity.
    // implementation(libs.androidx.navigation.fragment.ktx)
    // implementation(libs.androidx.navigation.ui.ktx)


    // ViewModel, Lifecycle, and Activity KTX (already present, used by Compose as well)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Jetpack Compose Dependencies
    implementation(platform(libs.androidx.compose.bom)) // BOM
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3) // Material Design 3 for Compose
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling) // Tooling for previews (debug only)

    implementation(libs.androidx.activity.compose) // For setContent in Activity
    implementation(libs.androidx.lifecycle.viewmodel.compose) // For viewModel() Composable

    // Coroutines (already present, used by ViewModel)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Add Compose testing dependencies if needed
    // androidTestImplementation(platform(libs.androidx.compose.bom))
    // androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    // debugImplementation(libs.androidx.compose.ui.test.manifest)
}
