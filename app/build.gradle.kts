plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    kotlin("kapt")
}

android {
    compileSdk = Config.Sdk.compiled
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = Config.applicationId
        minSdk = Config.Sdk.min
        targetSdk = Config.Sdk.target
        versionCode = Config.versionCode
        versionName = Config.Version.name

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        // Enables Jetpack Compose for this module
        compose = true
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            testProguardFiles(
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.6"
    }

    sourceSets {
        getByName("test") {
            resources.srcDirs("src/test/res")
        }
    }

    kapt {
        correctErrorTypes = true
        useBuildCache = false
    }

    packaging {
        // Prevents errors when running Room tests
        resources.excludes.apply {
            add("META-INF/AL2.0")
            add("META-INF/LGPL2.1")
        }
    }
    namespace = "com.example.news"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":data"))
    implementation(project(":network"))

    // AndroidX
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Version.kotlin}")
    implementation(AndroidX.androidXCore)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.activityKtx)
    implementation(AndroidX.cardView)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.recyclerView)
    implementation(AndroidX.runtimeLiveData)
    implementation(AndroidX.swiperLayout)

    // For control over item selection of both touch and mouse driven selection
    implementation(AndroidX.recyclerViewSelection)

    // Lifecycle
    implementation(Lifecycle.viewModelKtx)
    implementation(Lifecycle.liveDataKtx)
    implementation(Lifecycle.viewModelSavedState)
    implementation(Lifecycle.compiler)
    implementation(Lifecycle.java8)

    // Stetho
    implementation(Stetho.core)
    implementation(Stetho.okHttp)

    // Glide
    implementation(Glide.core)
    kapt(Glide.compiler)

    // Retrofit
    implementation(Retrofit.core)

    // Jetpack
    // Integration with activities
    implementation(Compose.compose)
    // Compose Material Design
    implementation(Compose.material)
    // Animations
    implementation(Compose.animation)
    // Tool support (Previous, etc.)
    implementation(Compose.uiTooling)
    // Integration with ViewModels
    implementation(Compose.vmCompose)

    testImplementation(Junit.test)
    // For mocking objects for tests
    testImplementation(Mockito.core)
    // For InstantTaskExecutorRule, used to test LiveData
    testImplementation(AndroidArchTest.core)
    // For testing coroutines
    testImplementation(AndroidArchTest.coroutinesTest)
    testImplementation(OkHttp.testing)
    testImplementation(Retrofit.core)
    testImplementation(Retrofit.gson)

    // UI Tests
    androidTestImplementation(Junit.uiTest)
    androidTestImplementation(Junit.uiTestExt)
    androidTestImplementation(Espresso.core)
}