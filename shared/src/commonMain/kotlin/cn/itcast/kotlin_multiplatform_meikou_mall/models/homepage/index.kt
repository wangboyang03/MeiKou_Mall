package cn.itcast.kotlin_multiplatform_meikou_mall.models.homepage

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive

@kotlinx.serialization.Serializable
data class HotResult(
  @SerialName("id") val id: String = "",
  @SerialName("title") val title: String = "",
  @SerialName("subTypes") val subTypes: List<SubType> = emptyList(),
)

@kotlinx.serialization.Serializable
data class SubType(
  @SerialName("id") val id: String = "",
  @SerialName("title") val title: String = "",
  @SerialName("goodsItems") val goodsItems: GoodsItems = GoodsItems(),
)

@kotlinx.serialization.Serializable
data class GoodsItems(
  @SerialName("counts") val counts: Int = 0,
  @SerialName("items") val items: List<GoodsItem> = emptyList(),
  @SerialName("page") val page: Int = 0,
  @SerialName("pages") val pages: Int = 0,
  @SerialName("pageSize") val pageSize: Int = 0,
)

@kotlinx.serialization.Serializable
data class CategoryItem(
  @SerialName("id") val id: String = "",
  @SerialName("name") val name: String = "",
  @SerialName("picture") val picture: String = "",
  @SerialName("children") val children: List<CategoryItemChild> = emptyList(),
)

@kotlinx.serialization.Serializable
data class CategoryItemChild(
  @SerialName("id") val id: String = "",
  @SerialName("name") val name: String = "",
  @SerialName("picture") val picture: String = "",
)

@kotlinx.serialization.Serializable
data class GoodsItem(
  @SerialName("id") val id: String = "",
  @SerialName("name") val name: String = "",
  @SerialName("desc") val desc: String? = null,
  @kotlinx.serialization.Serializable(with = FlexibleStringSerializer::class)
  @SerialName("price") val price: String = "",
  @SerialName("picture") val picture: String = "",
  @SerialName("orderNum") val orderNum: Int = 0,
  @SerialName("payCount") val payCount: Int = 0,
)

@Serializable
data class Banner(
  @SerialName("id") val id: String = "",
  @SerialName("imgUrl") val imgUrl: String = "",
  @SerialName("type") val type: String = "",
)


object FlexibleStringSerializer : KSerializer<String> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("FlexibleString", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: String) {
    encoder.encodeString(value)
  }

  override fun deserialize(decoder: Decoder): String {
    val jsonDecoder = decoder as? JsonDecoder
      ?: throw SerializationException("Expected JsonDecoder")
    val element = jsonDecoder.decodeJsonElement()
    return when (element) {
      is JsonPrimitive -> element.content
      else -> ""
    }
  }
}