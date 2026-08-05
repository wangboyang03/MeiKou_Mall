package cn.itcast.kotlin_multiplatform_meikou_mall

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform