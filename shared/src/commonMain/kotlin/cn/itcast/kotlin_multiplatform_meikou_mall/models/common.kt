package cn.itcast.kotlin_multiplatform_meikou_mall.models

import org.jetbrains.compose.resources.DrawableResource

data class TabItemParams(
  val label: String,
  val activatedIcon: DrawableResource,
  val inactiveIcon: DrawableResource
)