package cn.itcast.kotlin_multiplatform_meikou_mall

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return sayHello(platform.name)
    }
}