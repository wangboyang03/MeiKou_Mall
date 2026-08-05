package cn.itcast.kotlin_multiplatform_meikou_mall.router

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cn.itcast.kotlin_multiplatform_meikou_mall.views.Index
import cn.itcast.kotlin_multiplatform_meikou_mall.views.login.LoginScreen

@Composable fun NavigatorHost(controller: NavHostController) {
  NavHost(controller, RouterMap.Index.route) {
    composable(RouterMap.Index.route) {
      Index()
    }
    composable(RouterMap.LoginScreen.route) {
      LoginScreen()
    }
  }
}