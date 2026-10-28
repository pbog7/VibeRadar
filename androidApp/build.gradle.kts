import com.android.build.api.dsl.ApplicationExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.googleServices)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
    dependencies {
        implementation(libs.bundles.koin)
        implementation(libs.compose.uiToolingPreview)
        implementation(libs.androidx.activity.compose)
        implementation(libs.ktor.client.okhttp)
        implementation(project.dependencies.platform(libs.android.firebase.bom))
        implementation(libs.android.firebase.auth)
        implementation(libs.android.firebase.analytics)
        implementation(libs.android.firebase.firestore)
        implementation(project(":composeApp"))
//            implementation(libs.android.firebase.crashlytics)
    }
}


configure<ApplicationExtension> {
    namespace = "com.pbogdev.viberadar"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "com.pbogdev.viberadar"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}


