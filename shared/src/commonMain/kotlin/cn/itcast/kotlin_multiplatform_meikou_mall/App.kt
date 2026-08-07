package cn.itcast.kotlin_multiplatform_meikou_mall

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import cn.itcast.kotlin_multiplatform_meikou_mall.router.NavigationHost
import cn.itcast.kotlin_multiplatform_meikou_mall.views.components.PromptActionComponent

@Composable @Preview fun App() {
  MaterialTheme {
    val controller = rememberNavController()
    Box(Modifier.background(MaterialTheme.colorScheme.onTertiary).fillMaxSize()) {
      NavigationHost(controller)
      PromptActionComponent()
    }
  }
}
