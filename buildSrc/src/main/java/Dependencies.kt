object Config {
    const val applicationId = "com.example.news"
    const val versionCode = 1

    object Version {
        private const val major = 1
        private const val minor = 0
        private const val patch = 0

        val name: String
            get() = "$major.$minor.$patch"
    }

    object Sdk {
        const val compiled = 33
        const val min = 28
        const val target = 33
    }
}

object Version {
    object AndroidX {
        const val activityKtx = "1.4.0"
        const val activityCompose = "1.4.0"
        const val animation = "1.0.5"
        const val appCompat = "1.4.1"
        const val cardView = "1.0.0"
        const val constraintLayout = "2.1.3"
        const val coreKtx = "1.7.0"
        const val lifecycleVMCompose = "2.4.0"
        const val liveDataRuntime = "1.2.0-alpha01"
        const val material = "1.0.5"
        const val recyclerView = "1.2.1"
        const val recyclerViewSelection = "1.2.0-alpha01"
        const val swiperLayout = "1.1.0"
        const val uiTooling = "1.0.5"
    }

    const val glide = "4.12.0"
    const val lifecycle = "2.4.0"
    const val kotlin = "1.8.20"
    const val okHttp = "4.9.0"
    const val recyclerView = "1.2.1"
    const val retrofit = "2.9.0"
    const val room = "2.5.1"
    const val stetho = "1.5.1"
    const val work = "2.7.1"

    const val coreTesting = "2.1.0"
    const val junitTest = "4.13.2"
    const val junitUitTest = "1.0.5"
    const val junitUiTestExt = "1.1.3"
    const val coroutinesTest = "1.5.0"
    const val mockito = "3.5.10"
    const val espresso = "3.4.0"
}

object AndroidX {
    const val androidXCore = "androidx.core:core-ktx:${Version.AndroidX.coreKtx}"
    const val appCompat = "androidx.appcompat:appcompat:${Version.AndroidX.appCompat}"
    const val activityKtx = "androidx.activity:activity-ktx:${Version.AndroidX.activityKtx}"
    const val cardView = "androidx.cardview:cardview:${Version.AndroidX.cardView}"
    const val runtimeLiveData = "androidx.compose.runtime:runtime-livedata:${Version.AndroidX.liveDataRuntime}"
    const val swiperLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Version.AndroidX.swiperLayout}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Version.AndroidX.constraintLayout}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${Version.AndroidX.recyclerView}"
    const val recyclerViewSelection = "androidx.recyclerview:recyclerview-selection:${Version.AndroidX.recyclerViewSelection}"
}

object Lifecycle {
    const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Version.lifecycle}"
    const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${Version.lifecycle}"
    const val compiler = "androidx.lifecycle:lifecycle-compiler:${Version.lifecycle}"
    const val java8 = "androidx.lifecycle:lifecycle-common-java8:${Version.lifecycle}"
    const val viewModelSavedState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Version.lifecycle}"
}

// https://developer.android.com/jetpack/androidx/releases/room#kts
object Room {
    const val runtime = "androidx.room:room-runtime:${Version.room}"
    const val compiler = "androidx.room:room-compiler:${Version.room}"
    const val ktx = "androidx.room:room-ktx:${Version.room}"
    const val testing = "androidx.room:room-testing:${Version.room}"
}

// https://developer.android.com/jetpack/androidx/releases/work
object Work {
    const val ktx = "androidx.work:work-runtime-ktx:${Version.work}"
    const val testing = "androidx.work:work-testing:${Version.work}"
}

// https://github.com/square/retrofit
object Retrofit {
    const val core = "com.squareup.retrofit2:retrofit:${Version.retrofit}"
    const val gson = "com.squareup.retrofit2:converter-gson:${Version.retrofit}"
}

// https://square.github.io/okhttp/
object OkHttp {
    const val core = "com.squareup.okhttp3:okhttp:${Version.okHttp}"
    const val testing = "com.squareup.okhttp3:mockwebserver:${Version.okHttp}"
}

// http://facebook.github.io/stetho/
object Stetho {
    const val core = "com.facebook.stetho:stetho:${Version.stetho}"
    const val okHttp = "com.facebook.stetho:stetho-okhttp3:${Version.stetho}"
}

// https://github.com/bumptech/glide
object Glide {
    const val core = "com.github.bumptech.glide:glide:${Version.glide}"
    const val compiler = "com.github.bumptech.glide:compiler:${Version.glide}"
}

// https://developer.android.com/jetpack/compose/interop/adding
object Compose {
    const val compose = "androidx.activity:activity-compose:${Version.AndroidX.activityCompose}"
    const val material = "androidx.compose.material:material:${Version.AndroidX.material}"
    const val animation = "androidx.compose.animation:animation:${Version.AndroidX.animation}"
    const val uiTooling = "androidx.compose.ui:ui-tooling:${Version.AndroidX.uiTooling}"
    const val vmCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:${Version.AndroidX.lifecycleVMCompose}"
}

// https://junit.org/junit4/
object Junit {
    const val test = "junit:junit:${Version.junitTest}"
    const val uiTest = "androidx.compose.ui:ui-test-junit4:${Version.junitUitTest}"
    const val uiTestExt = "androidx.test.ext:junit:${Version.junitUiTestExt}"
}

object Kotlin {
    const val ktlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}"
}

// For mocking objects for tests
// https://site.mockito.org
// https://search.maven.org/search?q=g:org.mockito
object Mockito {
    const val core = "org.mockito:mockito-core:${Version.mockito}"
}

// For InstantTaskExecutorRule, used to test LiveData
// https://developer.android.com/jetpack/androidx/releases/arch-core
object AndroidArchTest {
    const val core = "androidx.arch.core:core-testing:${Version.coreTesting}"

    // For testing coroutines
    // https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Version.coroutinesTest}"
}

object Espresso {
    const val core = "androidx.test.espresso:espresso-core:${Version.espresso}"
}


