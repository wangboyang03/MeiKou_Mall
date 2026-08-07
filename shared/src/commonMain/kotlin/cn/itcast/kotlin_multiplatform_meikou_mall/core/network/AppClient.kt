package cn.itcast.kotlin_multiplatform_meikou_mall.core.network

import cn.itcast.kotlin_multiplatform_meikou_mall.App
import cn.itcast.kotlin_multiplatform_meikou_mall.views.components.PromptAction
import cn.itcast.kotlin_multiplatform_meikou_mall.views.components.ToastOptions
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.cio.parseResponse
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull

class AppClient(@PublishedApi internal val client: HttpClient) {
  // 伴随对象 当返回体中业务状态码code不为1时 请求失败 停止请求 抛出异常
  companion object {
    const val CODE: String = "1"
  }

  suspend inline fun <reified T> get(url: String, params: Map<String, Any>? = emptyMap()): T {
    val response = client.get(url) {
      params?.forEach { (key, value) -> parameter(key, value) }
    }
    return parseResponse(response.bodyAsText())
  }

  suspend inline fun <reified T> post(url: String, data: Any? = null): T {
    val response = client.post(url) {
      data?.let { setBody(data) }
    }
    return parseResponse(response.bodyAsText())
  }

  suspend inline fun <reified T> put(url: String, data: Any? = null): T {
    val response = client.put(url) {
      data?.let { setBody(data) }
    }
    return parseResponse(response.bodyAsText())
  }

  suspend inline fun <reified T> delete(url: String, data: Any? = null): T {
    val response = client.delete(url) {
      data?.let { setBody(data) }
    }
    return parseResponse(response.bodyAsText())
  }

  inline fun <reified T> parseResponse(string: String): T {
    val response = HttpClientFactory.JSON.decodeFromString<ResponseData<JsonElement?>>(string)
    // T 有可能是 具体的List<BookItem> 有可能是一个Unit
    if (T::class == Unit::class) {
      return Unit as T
    }
    if (response.code != AppClient.CODE) {
      PromptAction.showToast(ToastOptions(response.msg ?: "请求异常"))
      throw Exception("请求异常")
    }
    // 说明返回的不是 Unit 有具体类型
    val data = response.result
    if (data == null || data == JsonNull) {
      // 说明要的不是Unit 但是接口返回的是空
      PromptAction.showToast(ToastOptions(response.msg ?: "请求异常"))
      throw Exception("请求异常")
    }
    return HttpClientFactory.JSON.decodeFromString<T>(data.toString())
  }
}

val appClient = AppClient(HttpClientFactory.httpClient)