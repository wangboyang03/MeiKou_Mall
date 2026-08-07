import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
}

kotlin {
  js {
    browser()
    binaries.executable()
  }

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser()
    binaries.executable()
  }

  sourceSets {
    commonMain.dependencies {
      implementation(project(":shared"))

      implementation(libs.compose.ui)
      implementation(libs.compose.components.resources)
    }
    jsMain.dependencies {
      implementation(npm("os-browserify", "0.3.0"))
      implementation(npm("path-browserify", "1.0.1"))
    }
    wasmJsMain.dependencies {
      implementation(npm("os-browserify", "0.3.0"))
      implementation(npm("path-browserify", "1.0.1"))
    }
  }
}