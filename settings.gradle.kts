rootProject.name = "Meikou_Mall"

pluginManagement {
  repositories {
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/public")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
  }
}

dependencyResolutionManagement {
  repositories {
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/public")
  }
}

include(":androidApp")
include(":shared")
include(":webApp")