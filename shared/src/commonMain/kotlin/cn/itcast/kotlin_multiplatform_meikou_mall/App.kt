package cn.itcast.kotlin_multiplatform_meikou_mall

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import cn.itcast.kotlin_multiplatform_meikou_mall.router.NavigatorHost

@Composable @Preview fun App() {
  MaterialTheme {
    val controller = rememberNavController()
    Column(Modifier.background(MaterialTheme.colorScheme.background).safeContentPadding().fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
      NavigatorHost(controller)
    }
  }
}