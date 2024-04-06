plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.coroutines)
            implementation(libs.kotlin.date.time)
            implementation(libs.kotlin.serialization.json)
        }

        all {
            languageSettings.optIn("kotlin.ExperimentalStdlibApi")
        }
    }
}

android {
    namespace = "com.sixbynine.transit.path.platform"

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    dependencies {
        coreLibraryDesugaring(libs.android.tools.desugar)
    }
}