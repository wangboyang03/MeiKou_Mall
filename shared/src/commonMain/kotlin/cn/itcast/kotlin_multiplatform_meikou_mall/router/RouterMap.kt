package cn.itcast.kotlin_multiplatform_meikou_mall.router

sealed class RouterMap(val route: String) {
  object Index: RouterMap("index")
  object LoginScreen: RouterMap("login_screen")
}