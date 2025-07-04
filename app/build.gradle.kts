plugins {
    alias(libs.plugins.android.application)
    id("androidx.navigation.safeargs")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.projectakhir"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.projectakhir"
        minSdk = 30
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation ("com.google.android.gms:play-services-auth:21.0.0")

    // AndroidX & Common Libraries
    implementation ("androidx.core:core:1.12.0")
    implementation(libs.flexbox)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Activity & Fragment KTX (untuk ActivityResultLauncher)
    implementation ("androidx.activity:activity:1.8.2")
    implementation ("androidx.fragment:fragment:1.6.2")

    // RecyclerView & CardView
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("androidx.cardview:cardview:1.0.0")

    // ViewModel
    implementation(libs.lifecycle.viewmodel.ktx) // Or latest version
    // LiveData
    implementation(libs.lifecycle.livedata.ktx)   // Or latest version
    // Lifecycle Annotation Processor (if using Java)
    // annotationProcessor("androidx.lifecycle:lifecycle-compiler:2.8.7") // Not needed for Kotlin usually
    // Optional: Lifecycle Runtime KTX for lifecycleScope
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Java language implementation
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation (libs.circleimageview)

    // Import the Firebase BoM (Bill of Materials)
    implementation(platform(libs.firebase.bom))

    // Add the Firebase SDKs for the products you want to use
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    // Glide
    implementation(libs.glide)
    implementation("com.google.android.material:material:1.12.0")
}