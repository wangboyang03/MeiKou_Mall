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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

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

  fun showLoading(options: LoadingOptions? = null): () -> Unit {
    _event.tryEmit(CommonEvent.LoadingEvent(
      when(options != null) {
        true -> options.copy(showLoading = true)
        else -> LoadingOptions(showLoading = true)
      }
    ))
    return {
      _event.tryEmit(CommonEvent.LoadingEvent(
        when(options != null) {
          true -> options.copy(showLoading = false)
          else -> LoadingOptions(showLoading = false)
        }
      ))
    }
  }

  fun showAlertDialog(options: MessageOptions?) {
    _event.tryEmit(CommonEvent.MessageEvent(
      options?.copy(showDialog = true) ?: MessageOptions(showDialog = true) // 用户可选传参
    ))
  }

  fun closeAlertDialog() {
    _event.tryEmit(CommonEvent.MessageEvent(MessageOptions(showDialog = false)))
  }
}

sealed interface CommonEvent {
  class ToastEvent(val options: ToastOptions): CommonEvent
  class LoadingEvent(val options: LoadingOptions? = LoadingOptions()): CommonEvent // options需要默认实现LoadingOptions()
  class MessageEvent(val options: MessageOptions? = MessageOptions()): CommonEvent
}

// 声明Toast需要的参数对象
data class ToastOptions(
  val message: String,
  val foregroundColor: Color = Color.White,
  val backgroundColor: Color = Color(0xB31D1B1B),
  val duration: Long = 5000L
)

// 声明Loading需要的参数对象
data class LoadingOptions(
  val message: String = "正在加载中...",
  val foregroundColor: Color = Color.White,
  val backgroundColor: Color = Color(0xB31D1B1B),
  val showLoading: Boolean = false
)

// 声明Message需要的参数对象
data class MessageOptions(
  val title: String = "提示",
  val content: @Composable () -> Unit ={},
  val showConfigButton: Boolean = true,
  val showCancelButton: Boolean = false,
  val confirmButtonText: String = "确认",
  val cancelButtonText: String = "取消",
  val confirmCallBack: () -> Unit = {},
  val cancelCallBack: () -> Unit = {},
  val closeOnMask: Boolean = true,
  val showDialog: Boolean = false
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
  var loadingOptions by remember { mutableStateOf<LoadingOptions?>(null) }
  var messageOptions by remember { mutableStateOf<MessageOptions?>(null) }

  LaunchedEffect(Unit) {
    // 在副作用中收集发射出来的事件流
    PromptAction.event.collect {
      // 分发事件流到CommonEvent的具体实现
      when (it) {
        is CommonEvent.ToastEvent -> {
          currentType = if (it.options.message.isBlank()) DialogType.NONE else DialogType.TOAST
          toastOptions = it.options
        }
        is CommonEvent.LoadingEvent -> {
          val options = it.options ?: return@collect // 取出options参数 因为前面为option声明可选签名
          // 通过猫耳操作符避免未传值到的的空指针
          it.options.showLoading.let {
            when (it) {
              true -> {
                currentType = DialogType.LOADING
                loadingOptions =options
              }
              else -> currentType = DialogType.NONE
            }
          }
        }
        is CommonEvent.MessageEvent -> {
          when (it.options?.showDialog) {
            true -> {
              currentType = DialogType.MESSAGE
              messageOptions = it.options
            }
            else -> currentType = DialogType.NONE
          }
        }
      }
    }
  }

  when (currentType) {
    DialogType.TOAST -> {
      val options = toastOptions ?: return
      Toast(options)
    }
    DialogType.LOADING -> {
      val options = loadingOptions ?: return
      Loading(options)
    }
    DialogType.MESSAGE -> {
      val options = messageOptions ?: return
      AlertDialog(options)
    }
    else -> null
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
      Text(options.message, Modifier.background(options.backgroundColor, RoundedCornerShape(20.dp)).padding(20.dp, 10.dp), color = options.foregroundColor)
    }
  }
}

@Composable fun Loading(options: LoadingOptions) {
  Box(Modifier.fillMaxSize(), Alignment.Center) {
    Column(Modifier.width(160.dp).aspectRatio(1f).background(options.backgroundColor, RoundedCornerShape(20.dp)).padding(10.dp, 0.dp), Arrangement.Center, Alignment.CenterHorizontally) {
      CircularProgressIndicator(color = options.foregroundColor)
      Spacer(Modifier.height(10.dp))
      Text(options.message, color = options.foregroundColor)
    }
  }
}

@Composable fun AlertDialog(options: MessageOptions) {
  val scope = rememberCoroutineScope()
  AlertDialog(
    title = {
      Text(options.title)
    },
    text = options.content,
    // 点击蒙层会触发onDismissRequest
    onDismissRequest = {
      if (options.closeOnMask) {
        PromptAction.closeAlertDialog()
      }
    },
    confirmButton = {
      if (options.showDialog) {
        TextButton({
          scope.launch {
            options.confirmCallBack() // 点击确认按钮执行的逻辑
            PromptAction.closeAlertDialog() // 执行关闭弹窗的逻辑
          }
        }) {
          Text(options.confirmButtonText)
        }
      }
    },
    dismissButton = {
      if (options.showCancelButton) {
        TextButton({
          scope.launch {
            options.cancelCallBack() // 点击取消按钮执行的逻辑
            PromptAction.closeAlertDialog() // 执行关闭弹窗的逻辑
          }

        }) {
          Text(options.cancelButtonText)
        }
      }
    }
  )
}