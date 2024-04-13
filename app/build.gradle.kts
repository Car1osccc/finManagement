plugins {
    alias(libs.plugins.androidApplication)
}


android {
    namespace = "com.financial.management"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.financial.management"
        minSdk = 30
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation("org.projectlombok:lombok:1.18.28")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}