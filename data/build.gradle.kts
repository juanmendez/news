plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.news"
    compileSdk =  Config.Sdk.target

    defaultConfig {
        minSdk = Config.Sdk.min
        targetSdk = Config.Sdk.target

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {

    implementation(AndroidX.androidXCore)
    implementation(AndroidX.appCompat)
    implementation(Compose.material)
    testImplementation(Junit.test)
    androidTestImplementation(Junit.uiTest)
    androidTestImplementation(Espresso.core)
}