import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.dagger.hilt.android)
    id("org.jetbrains.kotlin.kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.gpillaca.upcomingmovies"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gpillaca.upcomingmovies"
        minSdk = 26
        targetSdk = 34
        versionCode = 3
        versionName = "1.0"

        buildConfigField( "String", "HOST", getLocalProperty("HOST"))
        buildConfigField("String", "HOST_IMAGE", getLocalProperty("HOST_IMAGE"))
        buildConfigField("String", "API_KEY", getLocalProperty("API_KEY"))

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {

    implementation(project(":domain"))
    implementation(project(":usecases"))
    implementation(project(":data"))
    implementation ("androidx.work:work-runtime-ktx:2.7.1")
    implementation ("jp.wasabeef:blurry:4.0.1")

    implementation(libs.android.material)
    implementation(libs.android.play.services.location)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.bundles.androidx.navigation)
    implementation(libs.coroutines.core)

    implementation (libs.bundles.androidx.room)
    kapt(libs.androidx.room.compiler)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.glide)
    kapt(libs.glide.compiler)

    implementation(libs.bundles.retrofit2)

    implementation(platform(libs.firebase.boom))
    implementation(libs.bundles.firebase)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.bundles.mockito)
    testImplementation(libs.cash.turbine)

    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)

    kaptAndroidTest(libs.hilt.android.testing)

}

kapt {
    correctErrorTypes = true
}

/**
 * Convenience method to obtain a property from `$projectRoot/local.properties` file
 * without passing the project param
 */
fun Project.getLocalProperty(propertyName: String): String {
    return getLocalProperty(propertyName, this)
}

/**
 * Util to obtain property declared on `$projectRoot/local.properties` file.
 *
 *  @param propertyName the name of declared property
 * @param project the project reference
 *
 * @return the value of property name, otherwise throw [Exception]
 */
fun getLocalProperty(propertyName: String, project: Project): String {
    val localProperties = Properties().apply {
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            load(localPropertiesFile.inputStream())
        }
    }
    return localProperties.getProperty(propertyName)?.let {
        it
    } ?: run {
        throw InvalidUserDataException("You should define $propertyName in local.properties.")
    }
}
