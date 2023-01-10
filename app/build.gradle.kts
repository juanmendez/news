import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
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

        // API key is saved in local.properties, not pushed to github
        val localProperties = Properties().apply {
            load(FileInputStream(rootProject.file("local.properties")))
        }

        // API key is saved in local.properties, not pushed to github
        buildConfigField(
            "String",
            "API_KEY",
            localProperties.getProperty("apiKey")
        )
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.5"
    }

    sourceSets {
        getByName("test") {
            resources.srcDirs("src/test/res")
        }
    }

    packagingOptions {
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

    // Room
    implementation(Room.runtime)
    annotationProcessor(Room.compiler)
    // To use Kotlin annotation processing tool (kapt)
    kapt(Room.compiler)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(Room.ktx)
    // optional - Test helpers
    testImplementation(Room.testing)

    // Work
    implementation(Work.ktx)
    androidTestImplementation(Work.testing)

    // Retrofit
    implementation(Retrofit.core)
    implementation(Retrofit.gson)

    // Okhttp
    implementation(OkHttp.core)

    // Stetho
    implementation(Stetho.core)
    implementation(Stetho.okHttp)

    // Glide
    implementation(Glide.core)
    annotationProcessor(Glide.compiler)

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

    // UI Tests
    androidTestImplementation(Junit.uiTest)
    androidTestImplementation(Junit.uiTestExt)
    androidTestImplementation(Espresso.core)
}