import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.googleServices)
}

kotlin {
    androidLibrary {
        namespace = "com.pbogdev.viberadar"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }

        androidResources {
            enable = true
            noCompress += "tflite"
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            export(project(":aimatchmakingengine"))
        }
    }

    cocoapods {
        // 1. REQUIRED: Labels your shared code so Xcode can read it
        version = "1.0.0"
        summary = "Vibe Radar Shared Module"
        homepage = "https://github.com/pbogdev/viberadar"

        // 2. REQUIRED: Tells the compiler to build for iOS 17
        ios.deploymentTarget = "17.6"

        // 3. THE REASON YOU ARE DOING THIS: Adds MediaPipe
        pod("MediaPipeTasksText") {
            version = "0.10.21"
            // These flags fix the complex C++ linking issues automatically
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("FirebaseAnalytics") {
            version = "~> 12.9.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("FirebaseAuth") {
            version = "~> 12.9.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("FirebaseFirestore") {
            version = "~> 12.9.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        // 4. REQUIRED: Links to the Podfile you made
        podfile = project.file("../iosApp/Podfile")

        framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {

        }

        commonMain.dependencies {
            implementation(libs.material3Adaptive)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.bundles.koin)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.kermitLogger)
            implementation(project(":domain"))
            implementation(project(":data"))
            implementation(project(":core"))
            implementation(project(":homescreen"))
            implementation(project(":aimatchmakingengine"))
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

//android {
//    namespace = "com.pbogdev.viberadar"
//    compileSdk = libs.versions.android.compileSdk.get().toInt()
//    androidResources {
//        noCompress += "tflite"
//    }
//    defaultConfig {
//        minSdk = libs.versions.android.minSdk.get().toInt()
//    }
//    packaging {
//        resources {
//            excludes += "/META-INF/{AL2.0,LGPL2.1}"
//        }
//    }
//    buildTypes {
//        getByName("release") {
//            isMinifyEnabled = false
//        }
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }
//}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
val buildFlavor: String = project.findProperty("buildFlavor")?.toString() ?: "dev"

buildkonfig {
    packageName = "com.pbogdev.viberadar"

    // Required base config
    defaultConfigs {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "FLAVOR",
            buildFlavor
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "BASE_URL",
            "https://jsonplaceholder.typicode.com/"
        )
    }


    // Dev config override
    defaultConfigs("dev") {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "BASE_URL",
            "https://jsonplaceholder.typicode.com/"
        )
    }

    // Prod config override
    defaultConfigs("prod") {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "BASE_URL",
            "https://jsonplaceholder.typicode.com/"
        )
    }
}

