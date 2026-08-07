package cn.itcast.kotlin_multiplatform_meikou_mall.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.itcast.kotlin_multiplatform_meikou_mall.models.TabItemParams
import cn.itcast.kotlin_multiplatform_meikou_mall.views.cart.CartView
import cn.itcast.kotlin_multiplatform_meikou_mall.views.category.CategoryView
import cn.itcast.kotlin_multiplatform_meikou_mall.views.homepage.HomePageView
import cn.itcast.kotlin_multiplatform_meikou_mall.views.mine.MineView
import kotlinx.coroutines.launch
import meikou_mall.shared.generated.resources.Res
import meikou_mall.shared.generated.resources.ic_public_cart_active
import meikou_mall.shared.generated.resources.ic_public_cart_normal
import meikou_mall.shared.generated.resources.ic_public_home_active
import meikou_mall.shared.generated.resources.ic_public_home_normal
import meikou_mall.shared.generated.resources.ic_public_my_active
import meikou_mall.shared.generated.resources.ic_public_my_normal
import meikou_mall.shared.generated.resources.ic_public_pro_active
import meikou_mall.shared.generated.resources.ic_public_pro_normal
import org.jetbrains.compose.resources.painterResource
import kotlin.repeat

@Composable fun Index() {
  val tabList = listOf<TabItemParams>(
    TabItemParams("首页", Res.drawable.ic_public_home_active, Res.drawable.ic_public_home_normal),
    TabItemParams("分类", Res.drawable.ic_public_pro_active, Res.drawable.ic_public_pro_normal),
    TabItemParams("购物车", Res.drawable.ic_public_cart_active, Res.drawable.ic_public_cart_normal),
    TabItemParams("我的", Res.drawable.ic_public_my_active, Res.drawable.ic_public_my_normal)
  )

  val pageState = rememberPagerState(pageCount = { 4 })
  val scope = rememberCoroutineScope() // 局部协程作用域

  Column(Modifier.fillMaxSize().background(Color.Transparent)) {
    // 底Tab组件
    HorizontalPager(pageState, Modifier.weight(1f)) {
      when (it) {
        0 -> HomePageView()
        1 -> CategoryView()
        2 -> CartView()
        3 -> MineView()
      }
    }

    Row(Modifier.fillMaxWidth().navigationBarsPadding().height(85.dp)) {
      repeat(tabList.size) { currentIndex ->
        // 循环创建四个上图下字的item
        Column(Modifier.weight(1f).padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
          Box(Modifier.size(width = 100.dp, height = 48.dp).clip(RoundedCornerShape(24.dp)).background(if (currentIndex == pageState.currentPage) Color(0xFFE0E0F8) else Color.Transparent)
              .clickable {
                scope.launch {
                  pageState.scrollToPage(currentIndex)
                }
              },
            contentAlignment = Alignment.Center,
          ) {
            Image(painterResource(if (currentIndex == pageState.currentPage) tabList[currentIndex].activatedIcon else tabList[currentIndex].inactiveIcon), null, Modifier.size(30.dp))
          }
          Spacer(Modifier.height(4.dp)) // 上下间距
          Text(tabList[currentIndex].label, fontSize = 14.sp)
        }
      }
    }
  }
}
