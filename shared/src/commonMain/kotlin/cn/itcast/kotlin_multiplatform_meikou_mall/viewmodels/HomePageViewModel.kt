package cn.itcast.kotlin_multiplatform_meikou_mall.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.itcast.kotlin_multiplatform_meikou_mall.apis.HomePageApi
import cn.itcast.kotlin_multiplatform_meikou_mall.models.homepage.Banner
import cn.itcast.kotlin_multiplatform_meikou_mall.models.homepage.CategoryItem
import cn.itcast.kotlin_multiplatform_meikou_mall.models.homepage.GoodsItem
import cn.itcast.kotlin_multiplatform_meikou_mall.models.homepage.HotResult
import cn.itcast.kotlin_multiplatform_meikou_mall.views.components.LoadingOptions
import cn.itcast.kotlin_multiplatform_meikou_mall.views.components.PromptAction
import cn.itcast.kotlin_multiplatform_meikou_mall.views.components.ToastOptions
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.getOrDefault

data class HomeViewState(
  val banners: List<Banner> = emptyList(),
  val categories: List<CategoryItem> = emptyList(),
  val hotResult: HotResult? = null,
  val inVogue: HotResult? = null,
  val oneStop: HotResult? = null,
  val newGoods: List<GoodsItem> = emptyList(),
  val recommendGoods: List<GoodsItem> = emptyList(),
  val loading: Boolean = false,
  val isRefreshing: Boolean = false,
  val error: String? = null,
  val recommendPage: Int = 1,
  val recommendPages: Int = 8,
  val recommendFinished: Boolean = false,
  val recommendLoading: Boolean = false,
)

class HomePageViewModel: ViewModel() {
  private val _homepageState = MutableStateFlow<HomeViewState>(HomeViewState())
  val homeViewState = _homepageState.asStateFlow()

  init {
    PromptAction.showToast(ToastOptions("测试 每次进入会执行"))
    getHomeDataFormApi()
  }

  fun getHomeDataFormApi() {
    viewModelScope.launch {
      val close = PromptAction.showLoading()
      try {
        when (_homepageState.value.loading) {
          true -> return@launch
          else -> {
            // 阀门控制 避免多次请求
            _homepageState.update {
              it.copy(loading = true)
            }
            val errors = mutableListOf<String>() // 存放所有的错误信息列表

            // 轮播图并行请求
            val bannersAsync = async {
              runCatching {
                HomePageApi.getBanners()
              }.onFailure {
                errors += it.message ?: "轮播图请求失败"
              }.getOrDefault(emptyList())
            }

            // 分类列表并行请求
            val categoriesAsync = async {
              runCatching {
                HomePageApi.getCategories()
              }.onFailure {
                errors += it.message ?: "分类列表请求失败"
              }.getOrDefault(emptyList())
            }

            // 特惠推荐并行请求
            val hotResultAsync = async {
              runCatching {
                HomePageApi.getHotResult()
              }.onFailure {
                errors += it.message ?: "特惠推荐请求失败"
              }.getOrNull()
            }

            // 爆款推荐并行请求
            val inVogueAsync = async {
              runCatching {
                HomePageApi.getInVogue()
              }.onFailure {
                errors += it.message ?: "爆款推荐请求失败"
              }.getOrNull()
            }

            val oneStopAsync = async {
              runCatching {
                HomePageApi.getOneStop()
              }.onFailure {
                errors += it.message ?: "一站买全请求失败"
              }.getOrNull()
            }

            val newGoodsAsync = async {
              runCatching {
                HomePageApi.getNewGoods()
              }.onFailure {
                errors += it.message ?: "新潮好物请求失败"
              }.getOrDefault(emptyList())
            }

            val recommendGoodsAsync = async {
              runCatching {
                HomePageApi.getRecommend()
              }.onFailure {
                errors += it.message ?: "猜你喜欢请求失败"
              }.getOrDefault(emptyList())
            }

            val bannersResponse = bannersAsync.await()
            val categoriesResponse = categoriesAsync.await()
            val hotResultResponse = hotResultAsync.await()
            val inVogueResponse = inVogueAsync.await()
            val oneStopResponse = oneStopAsync.await()
            val newGoodsResponse = newGoodsAsync.await()
            val recommendGoodsResponse = recommendGoodsAsync.await()

            // 统一刷新数据
            _homepageState.update {
              it.copy(bannersResponse, categoriesResponse, hotResultResponse, inVogueResponse, oneStopResponse, newGoodsResponse, recommendGoodsResponse)
            }
          }
        }
      } catch (error: Exception) {
        error.message?.let { PromptAction.showToast(ToastOptions(it)) }
        error.printStackTrace()
      } finally {
        _homepageState.update {
          it.copy(loading = false)
        }
        close()
      }
    }
  }

  fun loadRecommendMoreData() {
    viewModelScope.launch {
      if (_homepageState.value.recommendLoading || _homepageState.value.recommendFinished) return@launch
      // 没有正在加载且还有更多数据
      _homepageState.update {
        it.copy(recommendLoading = true) // 阀门控制
      }
      val close = PromptAction.showLoading(LoadingOptions("正在加载更多..."))

      val currentPage = _homepageState.value.recommendPage +1 // 页码+1 得到当前需要请求的页码
      val allDataCount = currentPage * _homepageState.value.recommendPages // 获取到的总数量

      try {
        val response = HomePageApi.getRecommend(allDataCount)
        _homepageState.update {
          it.copy(recommendGoods = response, recommendPage = currentPage, recommendFinished = response.size < allDataCount,)
        }
      } catch (error: Exception) {
        error.message?.let {
          PromptAction.showToast(ToastOptions(it))
        }
        error.printStackTrace()
      } finally {
        _homepageState.update {
          it.copy(recommendLoading = false)
        }
        close()
      }
    }
  }

  fun refreshingHomeData() {
    // 先把状态重置回去
    _homepageState.update {
      it.copy(loading = false, recommendPage = 1, recommendFinished = false)
    }
    // 然后调用首页数据获取方法
    getHomeDataFormApi()
  }
}
