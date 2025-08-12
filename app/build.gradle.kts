plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services") //Servicios de Firebase.
    id("com.google.devtools.ksp") //KSP
    id("androidx.navigation.safeargs.kotlin") //Navigation
    id("kotlin-parcelize") //Parcelize

}

android {
    namespace = "com.javiercanales.calistenia"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.javiercanales.calistenia"
        minSdk = 26
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.core.animation)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.recyclerview) //Dependencia para poder hacer scroll.
    implementation(platform(libs.firebase.bom)) //Firebase BoM.
    implementation(libs.firebase.auth) //Firebase Authentication.
    implementation(libs.firebase.analytics) //Firebase Analytics.
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.symbol.processing.api)
    ksp(libs.androidx.room.compiler) //Para SQLite.
    implementation(libs.androidx.lifecycle.livedata.ktx) //LiveData.
    implementation(libs.androidx.navigation.fragment.ktx) //Navigation Component.
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.core.splashscreen) //Splash-Screen.
    implementation(libs.material.calendarview) //Calendario.
    implementation(libs.kotlinx.coroutines.android)



    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries
}