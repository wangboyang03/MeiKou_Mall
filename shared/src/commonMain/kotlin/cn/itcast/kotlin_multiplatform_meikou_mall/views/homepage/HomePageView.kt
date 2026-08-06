package cn.itcast.kotlin_multiplatform_meikou_mall.views.homepage

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import cn.itcast.kotlin_multiplatform_meikou_mall.views.components.PromptAction
import cn.itcast.kotlin_multiplatform_meikou_mall.views.components.ToastOptions

@Composable fun HomePageView() {
  Column {
    TextButton({
      PromptAction.showToast(ToastOptions("点我干啥！！！"))
    }) {
      Text("点我试试")
    }
  }
}