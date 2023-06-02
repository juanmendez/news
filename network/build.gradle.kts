plugins {
    id("com.android.library")
    id("kotlin-android")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.news.network"
    compileSdk = Config.Sdk.target

    defaultConfig {
        minSdk = Config.Sdk.min

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(project(":data"))
    implementation(AndroidX.androidXCore)
    implementation(AndroidX.appCompat)
    implementation(Compose.material)

    // Stetho
    implementation(Stetho.core)
    implementation(Stetho.okHttp)

    // Work
    implementation(Work.ktx)
    androidTestImplementation(Work.testing)


    testImplementation(Junit.test)
    // For mocking objects for tests
    testImplementation(Mockito.core)
    // For InstantTaskExecutorRule, used to test LiveData
    testImplementation(AndroidArchTest.core)
    // For testing coroutines
    testImplementation(AndroidArchTest.coroutinesTest)
    testImplementation(OkHttp.testing)
    // optional - Test helpers
    testImplementation(Room.testing)

    androidTestImplementation(Junit.uiTest)
    androidTestImplementation(Espresso.core)
    androidTestImplementation(Junit.uiTestExt)

    // Retrofit
    implementation(Retrofit.core)
    implementation(Retrofit.gson)

    // Okhttp
    implementation(OkHttp.core)
}