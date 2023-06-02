import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    kotlin("kapt")
}

android {
    namespace = "com.example.news.data"
    compileSdk =  Config.Sdk.target

    defaultConfig {
        minSdk = Config.Sdk.min

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

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

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true"
                )
            }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    kapt {
        correctErrorTypes = true
        useBuildCache = false
    }
}

dependencies {

    implementation(AndroidX.androidXCore)
    implementation(AndroidX.appCompat)
    implementation(Compose.material)

    // Retrofit
    implementation(Retrofit.core)
    implementation(Retrofit.gson)

    // Okhttp
    implementation(OkHttp.core)

    // Stetho
    implementation(Stetho.core)
    implementation(Stetho.okHttp)

    // Work
    implementation(Work.ktx)
    androidTestImplementation(Work.testing)

    // Room
    api(Room.runtime)
    annotationProcessor(Room.compiler)
    // To use Kotlin annotation processing tool (kapt)
    kapt(Room.compiler)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(Room.ktx)

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
}