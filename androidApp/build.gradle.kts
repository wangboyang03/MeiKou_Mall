import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.composeCompiler)
  kotlin("android")
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_11
  }
}
dependencies {
  implementation(project(":shared"))

  implementation(libs.androidx.activity.compose)

  implementation(libs.compose.uiToolingPreview)
  debugImplementation(libs.compose.uiTooling)
}

android {
  namespace = "cn.itcast.kotlin_multiplatform_meikou_mall"
  compileSdk = libs.versions.android.compileSdk.get().toInt()

  defaultConfig {
    applicationId = "cn.itcast.kotlin_multiplatform_meikou_mall"
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
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
  }
}