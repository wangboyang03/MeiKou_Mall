package cn.itcast.kotlin_multiplatform_meikou_mall.views.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object PromptAction {
  // 声明发射事件流
  private val _event = MutableSharedFlow<CommonEvent>(replay = 1, extraBufferCapacity = 100)
  val event = _event.asSharedFlow()

  fun showToast(options: ToastOptions) {
    _event.tryEmit(CommonEvent.ToastEvent(options)) // 把当前提示参数发射出去
  }

  fun closeToast() {
    _event.tryEmit(CommonEvent.ToastEvent(ToastOptions("")))
  }
  fun showLoading() {}
  fun showAlertDialog() {}
}

sealed interface CommonEvent {
  class ToastEvent(val options: ToastOptions): CommonEvent
  class LoadingEvent(): CommonEvent
  class MessageEvent(): CommonEvent
}

// 声明Toast需要的参数对象
data class ToastOptions(
  val message: String,
  val color: Color = Color.White,
  val backgroundColor: Color = Color(0xB31D1B1B),
  val duration: Long = 5000L
)

// 组件类型枚举
enum class DialogType {
  TOAST,
  LOADING,
  MESSAGE,
  NONE
}

/**
 * 定义组件
 */
@Composable fun PromptActionComponent() {
  // 声明当前弹窗类型 确保重组状态不丢失的状态变量
  var currentType by remember { mutableStateOf<DialogType>(DialogType.NONE) }
  var toastOptions by remember { mutableStateOf<ToastOptions?>(null) }

  LaunchedEffect(Unit) {
    // 在副作用中收集发射出来的事件流
    PromptAction.event.collect {
      when (it) {
        is CommonEvent.ToastEvent -> {
          currentType = if (it.options.message.isBlank()) DialogType.NONE else DialogType.TOAST
          toastOptions = it.options
        }
        is CommonEvent.LoadingEvent -> {}
        is CommonEvent.MessageEvent -> {}
      }
    }
  }

  when (currentType) {
    DialogType.TOAST -> {
      val options = toastOptions ?: return
      Toast(options)
    }
    else -> {}
  }
}

@Composable fun Toast(options: ToastOptions) {
  var showMessage by remember { mutableStateOf(false) }

  LaunchedEffect(options.message) {
    if (options.message.isNotBlank()) {
      showMessage = true // 显示
      delay(options.duration) // 停留具体时间
      showMessage = false // 关闭
      PromptAction.closeToast() // 把消息的字符串设置为空
    }
  }

  Box(Modifier.fillMaxSize().padding(bottom = 40.dp), contentAlignment = Alignment.BottomCenter) {
    AnimatedVisibility(showMessage,
      enter = fadeIn(tween(180)) + scaleIn(
        initialScale = 0.82f,
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioMediumBouncy,
          stiffness = Spring.StiffnessMedium
        )
      ),
      exit = fadeOut(tween(140)) + scaleOut(
        targetScale = 0.92f,
        animationSpec = tween(140)
      )
    ) {
      Text(options.message, Modifier.background(options.backgroundColor, RoundedCornerShape(20.dp)).padding(20.dp, 10.dp), color = options.color)
    }
  }
}
