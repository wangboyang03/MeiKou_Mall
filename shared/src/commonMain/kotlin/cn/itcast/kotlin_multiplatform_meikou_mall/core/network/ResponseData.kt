package cn.itcast.kotlin_multiplatform_meikou_mall.core.network

import kotlinx.serialization.Serializable

@Serializable data class ResponseData<T>(
  val code: String,
  val msg: String,
  val result: T?
)
