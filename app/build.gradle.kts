plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.sharonsalman.fittrack"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sharonsalman.fittrack"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.fragment)
    implementation(libs.firebase.database)
    implementation(libs.recyclerview.v130)
    implementation(libs.play.services.maps)
    implementation(libs.room.common)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform(libs.compose.bom))
    implementation(libs.material3)
    implementation (libs.material.icons.extended)
    implementation (libs.lifecycle.viewmodel.ktx.v283)
    implementation (libs.play.services.location)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.firebase.auth.v2110)
    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-firestore")
    implementation (libs.google.firebase.database)
    implementation(libs.recyclerview.v130)
    implementation (libs.glide)
    annotationProcessor (libs.glide.compiler)
    implementation (libs.picasso)
    implementation(libs.mpandroidchart)
    implementation (libs.graphview)
    implementation ("com.github.prolificinteractive:material-calendarview:2.0.1")













}