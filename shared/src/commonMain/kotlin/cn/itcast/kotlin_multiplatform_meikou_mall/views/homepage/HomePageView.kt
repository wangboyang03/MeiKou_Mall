package cn.itcast.kotlin_multiplatform_meikou_mall.views.homepage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.itcast.kotlin_multiplatform_meikou_mall.models.homepage.Banner
import cn.itcast.kotlin_multiplatform_meikou_mall.models.homepage.CategoryItem
import cn.itcast.kotlin_multiplatform_meikou_mall.models.homepage.GoodsItem
import cn.itcast.kotlin_multiplatform_meikou_mall.models.homepage.GoodsItems
import cn.itcast.kotlin_multiplatform_meikou_mall.models.homepage.HotResult
import cn.itcast.kotlin_multiplatform_meikou_mall.models.homepage.SubType
import cn.itcast.kotlin_multiplatform_meikou_mall.viewmodels.HomePageViewModel
import cn.itcast.kotlin_multiplatform_meikou_mall.viewmodels.HomeViewState
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlin.collections.chunked

@Composable fun HomePageView() {
  val vm: HomePageViewModel = viewModel()
  val homeViewState by vm.homeViewState.collectAsStateWithLifecycle()

  HomeScreen(state = homeViewState)
}

@Composable fun HomeScreen(state: HomeViewState, onSearchClick: () -> Unit = {}, onGoodsClick: (String) -> Unit = {}, ) {

  /*val preferenceGoods = state.hotResult.goods().take(4)
  val inVogueGoods = state.inVogue.goods().take(4)
  val oneStopGoods = state.oneStop.goods().take(4)*/
  val preferenceGoods = emptyList<GoodsItem>()
  val inVogueGoods = emptyList<GoodsItem>()
  val oneStopGoods = emptyList<GoodsItem>()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF2F3F5)),
    contentPadding = PaddingValues(bottom = 84.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    item {
      HomeTopSection(
        banners = state.banners,
        categories = state.categories,
        onSearchClick = onSearchClick,
      )
    }

    /*item {
      PreferenceSection(
        title = state.hotResult?.title.orEmpty().ifBlank { "特惠推荐" },
        goodsList = preferenceGoods,
        onGoodsClick = onGoodsClick,
      )
    }

    item {
      DualSection(
        leftTitle = state.inVogue?.title.orEmpty().ifBlank { "爆款推荐" },
        leftSubtitle = "24小时热榜",
        leftGoods = inVogueGoods,
        rightTitle = state.oneStop?.title.orEmpty().ifBlank { "一站全买" },
        rightSubtitle = "搞定熊孩子",
        rightGoods = oneStopGoods,
        onGoodsClick = onGoodsClick,
      )
    }

    item {
      NewGoodsSection(
        goodsList = state.newGoods,
        onGoodsClick = onGoodsClick,
      )
    }

    item {
      RecommendSection(
        goodsList = state.recommendGoods,
        onGoodsClick = onGoodsClick,
      )
    }*/
  }
}

private fun HotResult?.goods(): List<GoodsItem> {
  return this?.subTypes?.flatMap { it.goodsItems.items }.orEmpty()
}

@Composable private fun HomeTopSection(banners: List<Banner>, categories: List<CategoryItem>, onSearchClick: () -> Unit, ) {
  Column(
    modifier = Modifier.fillMaxWidth().background(Color.White),
  ) {
    HomeBanner(banners = banners, onSearchClick = onSearchClick)
    CategorySection(categories = categories)
  }
}

@Composable private fun HomeBanner(banners: List<Banner>, onSearchClick: () -> Unit, ) {
  val displayBanners = banners.filter { it.imgUrl.isNotBlank() }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(300.dp)
      .background(Color(0xFFF1CFB7)),
  ) {
    if (displayBanners.isNotEmpty()) {
      val pagerState = rememberPagerState(
        pageCount = { displayBanners.size }
      )

      /*
       * 自动轮播：
       * 每隔3秒切换到下一页，最后一页后回到第一页。
       */
      LaunchedEffect(displayBanners.size) {
        if (displayBanners.size <= 1) {
          return@LaunchedEffect
        }

        while (true) {
          delay(3_000)

          val nextPage = (pagerState.currentPage + 1) % displayBanners.size

          pagerState.animateScrollToPage(nextPage)
        }
      }

      HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
      ) { page ->
        val banner = displayBanners[page]

        AsyncImage(
          model = banner.imgUrl,
          contentDescription = "首页轮播图",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop,
        )
      }

      PageIndicator(
        count = displayBanners.size,
        current = pagerState.currentPage,
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(bottom = 12.dp),
      )
    }

    SearchOverlay(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.TopStart)
        .statusBarsPadding()
        .padding(
          start = 22.dp,
          end = 22.dp,
          top = 10.dp,
        ),
      onClick = onSearchClick,
    )
  }
}

@Composable
private fun SearchOverlay(
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Row(
    modifier = modifier
      .height(40.dp)
      .clip(RoundedCornerShape(21.dp))
      .background(Color(0x8CAEA39F))
      .border(
        width = 1.dp,
        color = Color.White.copy(alpha = 0.22f),
        shape = RoundedCornerShape(21.dp),
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(9.dp),
  ) {
    SearchIcon()

    Text(
      text = "搜索商品",
      color = Color.White.copy(alpha = 0.9f),
      fontSize = 14.sp,
    )
  }
}

@Composable
private fun SearchIcon() {
  Box(
    modifier = Modifier
      .size(15.dp)
      .drawBehind {
        val strokeWidth = 2f
        val center = Offset(
          x = size.width * 0.42f,
          y = size.height * 0.42f,
        )

        drawCircle(
          color = Color.White,
          radius = size.minDimension * 0.27f,
          center = center,
          style = Stroke(strokeWidth),
        )

        drawLine(
          color = Color.White,
          start = Offset(
            x = size.width * 0.62f,
            y = size.height * 0.62f,
          ),
          end = Offset(
            x = size.width * 0.9f,
            y = size.height * 0.9f,
          ),
          strokeWidth = strokeWidth,
        )
      },
  )
}

@Composable
private fun PageIndicator(
  count: Int,
  current: Int,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    repeat(count) { index ->
      Box(
        modifier = Modifier
          .width(if (index == current) 22.dp else 8.dp)
          .height(4.dp)
          .clip(RoundedCornerShape(100.dp))
          .background(
            if (index == current) {
              Color(0xFF1F1F1F)
            } else {
              Color.White.copy(alpha = 0.7f)
            }
          ),
      )
    }
  }
}

@Composable
private fun CategorySection(categories: List<CategoryItem>) {
  LazyRow(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 10.dp),
    contentPadding = PaddingValues(horizontal = 10.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    items(categories, key = { it.id }) { category ->
      Column(
        modifier = Modifier.width(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        AsyncImage(
          model = category.picture,
          contentDescription = category.name,
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color(0xFFF2F3F5)),
          contentScale = ContentScale.Crop,
        )

        Text(
          text = category.name,
          color = Color(0xFF3A3A3C),
          fontSize = 11.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
  }
}

@Composable
private fun PreferenceSection(
  title: String,
  goodsList: List<GoodsItem>,
  onGoodsClick: (String) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFFF7DDC8))
      .padding(10.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    SectionTitle(
      title = title,
      subtitle = "精选全攻略",
    )

    /*
     * 父 Row 先确定统一高度。
     * 左右两侧再使用 fillMaxHeight()，高度就会完全一致。
     */
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      PromotionPoster(
        goods = goodsList.first(),
        modifier = Modifier
          .weight(0.72f)
          .fillMaxHeight(),
        onClick = {
          onGoodsClick(goodsList.first().id)
        },
      )

      PromotionGoodsPanel(
        goodsList = goodsList.drop(1).take(3),
        modifier = Modifier
          .weight(1.28f)
          .fillMaxHeight(),
        onGoodsClick = onGoodsClick,
      )
    }
  }
}

@Composable
private fun PromotionPoster(
  goods: GoodsItem,
  modifier: Modifier,
  onClick: () -> Unit,
) {
  Column(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(Color(0xFFFF6B5F))
      .clickable(onClick = onClick)
      .padding(10.dp),
    verticalArrangement = Arrangement.spacedBy(7.dp),
  ) {
    Text(
      text = "年终大促",
      color = Color.White,
      fontSize = 18.sp,
      fontWeight = FontWeight.Black,
    )

    Text(
      text = "限时买就返",
      color = Color.White.copy(alpha = 0.92f),
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
    )

    AsyncImage(
      model = goods.picture,
      contentDescription = goods.name,
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .clip(RoundedCornerShape(10.dp))
        .background(Color.White.copy(alpha = 0.18f)),
      contentScale = ContentScale.Crop,
    )
  }
}

@Composable
private fun PromotionGoodsPanel(
  goodsList: List<GoodsItem>,
  modifier: Modifier,
  onGoodsClick: (String) -> Unit,
) {
  Row(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(Color.White)
      .padding(
        horizontal = 10.dp,
        vertical = 12.dp,
      ),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    goodsList.forEach { goods ->
      PromotionGoodsItem(
        goods = goods,
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
        onClick = {
          onGoodsClick(goods.id)
        },
      )
    }

    repeat((3 - goodsList.size).coerceAtLeast(0)) {
      Spacer(
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(),
      )
    }
  }
}

@Composable
private fun PromotionGoodsItem(
  goods: GoodsItem,
  modifier: Modifier,
  onClick: () -> Unit,
) {
  Column(
    modifier = modifier.clickable(onClick = onClick),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    AsyncImage(
      model = goods.picture,
      contentDescription = goods.name,
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .clip(RoundedCornerShape(10.dp))
        .background(Color(0xFFF3F4F6)),
      contentScale = ContentScale.Crop,
    )

    Spacer(modifier = Modifier.height(7.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(36.dp)
        .clip(RoundedCornerShape(9.dp))
        .background(Color(0xFFFF665C)),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = "¥${goods.price}",
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        maxLines = 1,
      )
    }
  }
}

@Composable private fun DualSection(leftTitle: String, leftSubtitle: String, leftGoods: List<GoodsItem>, rightTitle: String, rightSubtitle: String, rightGoods: List<GoodsItem>, onGoodsClick: (String) -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(166.dp),
    horizontalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    CompactSection(
      title = leftTitle,
      subtitle = leftSubtitle,
      goodsList = leftGoods,
      backgroundColor = Color(0xFFE9EEF8),
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight(),
      onGoodsClick = onGoodsClick,
    )

    CompactSection(
      title = rightTitle,
      subtitle = rightSubtitle,
      goodsList = rightGoods,
      backgroundColor = Color(0xFFF3ECE1),
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight(),
      onGoodsClick = onGoodsClick,
    )
  }
}

@Composable
private fun CompactSection(
  title: String,
  subtitle: String,
  goodsList: List<GoodsItem>,
  backgroundColor: Color,
  modifier: Modifier,
  onGoodsClick: (String) -> Unit,
) {
  Column(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(backgroundColor)
      .padding(10.dp),
    verticalArrangement = Arrangement.spacedBy(5.dp),
  ) {
    Text(
      text = title,
      color = Color(0xFF202124),
      fontSize = 16.sp,
      fontWeight = FontWeight.Black,
    )

    Text(
      text = subtitle,
      color = Color(0xFF8E8E93),
      fontSize = 11.sp,
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      goodsList.take(2).forEach { goods ->
        Column(
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable {
              onGoodsClick(goods.id)
            },
          verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
          AsyncImage(
            model = goods.picture,
            contentDescription = goods.name,
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(Color.White),
            contentScale = ContentScale.Crop,
          )

          Text(
            text = "¥${goods.price}",
            color = Color(0xFFFF5A5F),
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
          )
        }
      }

      repeat((2 - goodsList.take(2).size).coerceAtLeast(0)) {
        Spacer(modifier = Modifier.weight(1f))
      }
    }
  }
}

@Composable
private fun NewGoodsSection(
  goodsList: List<GoodsItem>,
  onGoodsClick: (String) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFFF8EEF3))
      .padding(vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    SectionTitle(
      title = "新潮好物",
      subtitle = "新鲜出炉 品质靠谱",
      modifier = Modifier.padding(horizontal = 12.dp),
    )

    LazyRow(
      contentPadding = PaddingValues(horizontal = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      items(
        items = goodsList,
        key = { it.id },
      ) { goods ->
        Column(
          modifier = Modifier
            .width(88.dp)
            .clickable {
              onGoodsClick(goods.id)
            },
          verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          AsyncImage(
            model = goods.picture,
            contentDescription = goods.name,
            modifier = Modifier
              .fillMaxWidth()
              .height(82.dp)
              .clip(RoundedCornerShape(9.dp))
              .background(Color.White),
            contentScale = ContentScale.Crop,
          )

          Text(
            text = goods.name,
            color = Color(0xFF333333),
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )

          Text(
            text = "¥${goods.price}",
            color = Color(0xFFFF5A5F),
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
          )
        }
      }
    }
  }
}

@Composable
private fun RecommendSection(
  goodsList: List<GoodsItem>,
  onGoodsClick: (String) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    SectionTitle(
      title = "猜你喜欢",
      subtitle = "越看越懂你",
      modifier = Modifier.padding(horizontal = 4.dp),
    )

    goodsList.chunked(2).forEach { rowGoods ->
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        rowGoods.forEach { goods ->
          RecommendGoodsCard(
            goods = goods,
            modifier = Modifier.weight(1f),
            onClick = {
              onGoodsClick(goods.id)
            },
          )
        }

        if (rowGoods.size == 1) {
          Spacer(modifier = Modifier.weight(1f))
        }
      }
    }
  }
}

@Composable
private fun RecommendGoodsCard(
  goods: GoodsItem,
  modifier: Modifier,
  onClick: () -> Unit,
) {
  Column(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(Color.White)
      .clickable(onClick = onClick)
      .padding(9.dp),
    verticalArrangement = Arrangement.spacedBy(7.dp),
  ) {
    AsyncImage(
      model = goods.picture,
      contentDescription = goods.name,
      modifier = Modifier
        .fillMaxWidth()
        .height(140.dp)
        .clip(RoundedCornerShape(9.dp))
        .background(Color(0xFFF3F4F6)),
      contentScale = ContentScale.Crop,
    )

    Text(
      text = goods.name,
      color = Color(0xFF252525),
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
    )

    Text(
      text = "¥${goods.price}",
      color = Color(0xFFFF5A5F),
      fontSize = 16.sp,
      fontWeight = FontWeight.Black,
    )
  }
}

@Composable
private fun SectionTitle(
  title: String,
  subtitle: String,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.Bottom,
  ) {
    Text(
      text = title,
      color = Color(0xFF202124),
      fontSize = 18.sp,
      fontWeight = FontWeight.Black,
    )

    Text(
      text = subtitle,
      color = Color(0xFF817870),
      fontSize = 12.sp,
    )
  }
}

/* -------------------- 以下全部为纯静态只读数据 -------------------- */

private fun mockImage(
  seed: String,
  width: Int = 600,
  height: Int = 600,
): String {
  return "https://picsum.photos/seed/$seed/$width/$height"
}

private fun mockGoods(
  id: String,
  name: String,
  price: String,
  seed: String,
): GoodsItem {
  return GoodsItem(
    id = id,
    name = name,
    desc = "品质好物，限时推荐",
    price = price,
    picture = mockImage(seed),
    orderNum = 100,
    payCount = 200,
  )
}

private fun mockHotResult(
  id: String,
  title: String,
  goodsList: List<GoodsItem>,
): HotResult {
  return HotResult(
    id = id,
    title = title,
    subTypes = listOf(
      SubType(
        id = "${id}_type",
        title = title,
        goodsItems = GoodsItems(
          counts = goodsList.size,
          items = goodsList,
          page = 1,
          pages = 1,
          pageSize = goodsList.size,
        ),
      )
    ),
  )
}

private val promotionGoods = listOf(
  mockGoods(
    id = "1001",
    name = "女士修身运动连体衣",
    price = "699.00",
    seed = "promotion-dress",
  ),
  mockGoods(
    id = "1002",
    name = "复古轻便休闲鞋",
    price = "1141.0",
    seed = "promotion-shoes",
  ),
  mockGoods(
    id = "1003",
    name = "经典复古太阳镜",
    price = "1437.0",
    seed = "promotion-glasses",
  ),
  mockGoods(
    id = "1004",
    name = "清新淡香水",
    price = "199.00",
    seed = "promotion-perfume",
  ),
)

private val hotGoods = listOf(
  mockGoods("2001", "轻便双肩旅行包", "299.00", "hot-bag"),
  mockGoods("2002", "家用研磨咖啡机", "799.00", "hot-coffee"),
  mockGoods("2003", "无线蓝牙耳机", "369.00", "hot-headphone"),
  mockGoods("2004", "智能运动手表", "599.00", "hot-watch"),
)

private val oneStopGoods = listOf(
  mockGoods("3001", "儿童益智积木", "159.00", "child-toy"),
  mockGoods("3002", "儿童轻便休闲鞋", "239.00", "child-shoes"),
  mockGoods("3003", "儿童纯棉卫衣", "189.00", "child-clothes"),
  mockGoods("3004", "儿童保温水杯", "99.00", "child-cup"),
)

private val newGoods = listOf(
  mockGoods("4001", "复古休闲鞋", "329.00", "new-shoes"),
  mockGoods("4002", "轻奢太阳镜", "268.00", "new-glasses"),
  mockGoods("4003", "清新香水礼盒", "199.00", "new-perfume"),
  mockGoods("4004", "便携双肩包", "399.00", "new-bag"),
  mockGoods("4005", "手冲咖啡套装", "289.00", "new-coffee"),
  mockGoods("4006", "家居香薰礼盒", "169.00", "new-aroma"),
)

private val recommendGoods = listOf(
  mockGoods("5001", "春季女士连衣裙", "499.00", "recommend-dress"),
  mockGoods("5002", "轻便男士休闲鞋", "399.00", "recommend-shoes"),
  mockGoods("5003", "简约女士手提包", "699.00", "recommend-bag"),
  mockGoods("5004", "复古设计太阳镜", "299.00", "recommend-glasses"),
  mockGoods("5005", "智能降噪耳机", "899.00", "recommend-headphone"),
  mockGoods("5006", "小型家用咖啡机", "1099.00", "recommend-coffee"),
)

val homeMockState = HomeViewState(
  banners = listOf(
    Banner(
      id = "banner_1",
      imgUrl = mockImage("home-banner-1", 1200, 600),
      type = "1",
    ),
    Banner(
      id = "banner_2",
      imgUrl = mockImage("home-banner-2", 1200, 600),
      type = "1",
    ),
    Banner(
      id = "banner_3",
      imgUrl = mockImage("home-banner-3", 1200, 600),
      type = "1",
    ),
  ),
  categories = listOf(
    CategoryItem(
      id = "category_1",
      name = "居家",
      picture = mockImage("category-home"),
    ),
    CategoryItem(
      id = "category_2",
      name = "美食",
      picture = mockImage("category-food"),
    ),
    CategoryItem(
      id = "category_3",
      name = "服饰",
      picture = mockImage("category-clothes"),
    ),
    CategoryItem(
      id = "category_4",
      name = "母婴",
      picture = mockImage("category-baby"),
    ),
    CategoryItem(
      id = "category_5",
      name = "个护",
      picture = mockImage("category-care"),
    ),
    CategoryItem(
      id = "category_6",
      name = "严选",
      picture = mockImage("category-choice"),
    ),
    CategoryItem(
      id = "category_7",
      name = "数码",
      picture = mockImage("category-digital"),
    ),
    CategoryItem(
      id = "category_8",
      name = "运动",
      picture = mockImage("category-sport"),
    ),
  ),
  hotResult = mockHotResult(
    id = "preference",
    title = "特惠推荐",
    goodsList = promotionGoods,
  ),
  inVogue = mockHotResult(
    id = "in_vogue",
    title = "爆款推荐",
    goodsList = hotGoods,
  ),
  oneStop = mockHotResult(
    id = "one_stop",
    title = "一站全买",
    goodsList = oneStopGoods,
  ),
  newGoods = newGoods,
  recommendGoods = recommendGoods,
  loading = false,
  isRefreshing = false,
  error = null,
  recommendPage = 1,
  recommendPages = 1,
  recommendFinished = true,
  recommendLoading = false,
)
