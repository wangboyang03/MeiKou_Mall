package cn.itcast.kotlin_multiplatform_meikou_mall.apis

import cn.itcast.kotlin_multiplatform_meikou_mall.core.network.appClient
import cn.itcast.kotlin_multiplatform_meikou_mall.models.homepage.Banner
import cn.itcast.kotlin_multiplatform_meikou_mall.models.homepage.CategoryItem
import cn.itcast.kotlin_multiplatform_meikou_mall.models.homepage.GoodsItem
import cn.itcast.kotlin_multiplatform_meikou_mall.models.homepage.HotResult

object HomePageApi {
  suspend fun getBanners(): List<Banner> = appClient.get<List<Banner>>("/home/banner")

  suspend fun getCategories(): List<CategoryItem> = appClient.get<List<CategoryItem>>("/home/category/head")

  suspend fun getHotResult(): HotResult = appClient.get<HotResult>("/hot/preference")

  suspend fun getNewGoods(): List<GoodsItem> = appClient.get<List<GoodsItem>>("/home/new")

  suspend fun getRecommend(limit: Int = 8): List<GoodsItem> = appClient.get<List<GoodsItem>>("/home/recommend", mapOf("limit" to limit.toString()))

  suspend fun getInVogue(): HotResult = appClient.get<HotResult>("/hot/inVogue")

  suspend fun getOneStop(): HotResult = appClient.get<HotResult>("/hot/oneStop")
}