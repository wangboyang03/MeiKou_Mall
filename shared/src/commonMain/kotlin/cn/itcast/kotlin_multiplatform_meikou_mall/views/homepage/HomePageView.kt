package cn.itcast.kotlin_multiplatform_meikou_mall.views.homepage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.itcast.kotlin_multiplatform_meikou_mall.views.components.PromptAction
import cn.itcast.kotlin_multiplatform_meikou_mall.views.components.ToastOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable fun HomePageView() {
  val scope = rememberCoroutineScope()
  Column {
    TextButton({
      PromptAction.showToast(ToastOptions("点我干啥！！！"))
    }) {
      Text("点我试试")
    }
    Spacer(Modifier.height(10.dp))
    TextButton({
      scope.launch {
        val closeFun = PromptAction.showLoading()
        // delay(10000)
        // closeFun()
      }
    }) {
      Text("点我试试 弹窗转不停")
    }
  }
}