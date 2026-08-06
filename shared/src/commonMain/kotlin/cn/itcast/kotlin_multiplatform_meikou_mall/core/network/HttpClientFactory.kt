package cn.itcast.kotlin_multiplatform_meikou_mall.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {
  private const val BASE_URL: String = "https://meikou-api.itheima.net/"
  private const val TIME_OUT: Long = 10000L

  // 定义序列化配置
  val JSON = Json {
    ignoreUnknownKeys = true
  }

  fun create(): HttpClient {
    return HttpClient {
      defaultRequest() {
        url(BASE_URL) // 设置基础地址
      }
      install(HttpTimeout) {
        connectTimeoutMillis = TIME_OUT
        requestTimeoutMillis = TIME_OUT
      }
      install(ContentNegotiation) {
        json(JSON)
      }
    }
  }

  val httpClient = create()
}