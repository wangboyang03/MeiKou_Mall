package cn.itcast.kotlin_multiplatform_meikou_mall.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.itcast.kotlin_multiplatform_meikou_mall.apis.HomePageApi
import cn.itcast.kotlin_multiplatform_meikou_mall.models.homepage.Banner
import cn.itcast.kotlin_multiplatform_meikou_mall.models.homepage.CategoryItem
import cn.itcast.kotlin_multiplatform_meikou_mall.models.homepage.GoodsItem
import cn.itcast.kotlin_multiplatform_meikou_mall.models.homepage.HotResult
import cn.itcast.kotlin_multiplatform_meikou_mall.views.components.PromptAction
import cn.itcast.kotlin_multiplatform_meikou_mall.views.components.ToastOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
  val recommendPages: Int = 1,
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
      try {
        when (_homepageState.value.loading) {
          true -> return@launch
          else -> {
            _homepageState.update {
              it.copy(loading = true)
            }
            val response = HomePageApi.getBanners()
            _homepageState.update {
              it.copy(banners = response)
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
      }
    }
  }
}
