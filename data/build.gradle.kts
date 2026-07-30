import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.kotlinSerializationPlugin)
    alias(libs.plugins.kotlinCocoapods)
}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "com.pbogdev.data"
        minSdk = libs.versions.android.minSdk.get().toInt()
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
    val xcfName = "dataKit"

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }
    cocoapods {
        // 1. REQUIRED: Labels your shared code so Xcode can read it
        version = "1.0.0"
        summary = "Vibe Radar Shared Module"
        homepage = "https://github.com/pbogdev/viberadar"

        // 2. REQUIRED: Tells the compiler to build for iOS 17
        ios.deploymentTarget = "17.6"
        noPodspec()
        // 3. THE REASON YOU ARE DOING THIS: Adds MediaPipe
        pod("FirebaseAuth") {
            version = "~> 12.9.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        // 4. REQUIRED: Links to the Podfile you made
    }
    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(project(":domain"))
                implementation(project(":core"))
                implementation(libs.bundles.koin)
                implementation(libs.kotlin.stdlib)
                implementation(libs.bundles.ktor)
                implementation(libs.bundles.kotlinXSerialization)
                implementation(libs.kermitLogger)
                implementation(libs.bundles.cryptography)
                implementation(libs.datastore)
                implementation(libs.okio)
                // Add KMP dependencies here
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.ktor.mock)
                implementation(project(":testCore"))
            }
        }

        androidMain {
            dependencies {
                implementation(project.dependencies.platform(libs.android.firebase.bom))
                implementation(libs.ktor.client.okhttp)
                implementation(libs.android.firebase.auth)
                implementation(libs.kotlin.coroutines)
                implementation(libs.kotlinx.coroutines.play.services)
                implementation("com.google.android.gms:play-services-location:21.2.0")
                // Add Android-specific dependencies here. Note that this source set depends on
                // commonMain by default and will correctly pull the Android artifacts of any KMP
                // dependencies declared in commonMain.
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.testExt.junit)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target  depends on as
                // part of KMP’s default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.
            }
        }
    }
}

val secretsFile = rootProject.file("secrets.properties")
val secretsProperties = Properties()

if (secretsFile.exists()) {
    secretsProperties.load(FileInputStream(secretsFile))
} else {
    // Optional: Print a warning during the build if the file is missing
    logger.warn("secrets.properties file not found at project root. BuildKonfig will fall back to environment variables or defaults.")
}

// 2. Extract the secret safely
val vibeAppSecret: String = secretsProperties.getProperty("VIBE_APP_SECRET")
    ?: System.getenv("VIBE_APP_SECRET")
    ?: "fallback_secret_do_not_use_in_prod"
val firebaseProjectId:String = secretsProperties.getProperty(("FIREBASE_PROJECT_ID"))
    ?: System.getenv("FIREBASE_PROJECT_ID")
val buildFlavor: String = project.findProperty("buildFlavor")?.toString() ?: "dev"

buildkonfig {
    packageName = "com.pbogdev.viberadar.data"

    // Required base config
    defaultConfigs {
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "FLAVOR", buildFlavor)
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "BASE_URL", "https://jsonplaceholder.typicode.com/")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "APP_SECRET", vibeAppSecret)
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "FIREBASE_PROJECT_ID", firebaseProjectId)
    }


    // Dev config override
    defaultConfigs("dev") {
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "BASE_URL", "https://jsonplaceholder.typicode.com/")
    }

    // Prod config override
    defaultConfigs("prod") {
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "BASE_URL", "https://jsonplaceholder.typicode.com/")
    }
}