import com.fasterxml.jackson.annotation.JsonProperty

data class JewelryCheckResponse(
    // Основная информация
    @JsonProperty("main_uin")
    val mainUin: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("description")
    val description: String?,

    @JsonProperty("composition")
    val composition: String?,

    @JsonProperty("status")
    val status: String,

    @JsonProperty("total_weight")
    val totalWeight: Double,

    @JsonProperty("weight_unit")
    val weightUnit: String,

    @JsonProperty("manufacturer")
    val manufacturer: Manufacturer,

    @JsonProperty("seller")
    val seller: Seller,

    @JsonProperty("items")
    val items: List<JewelryItem>,

    @JsonProperty("metal_info")
    val metalInfo: MetalInfo,

    @JsonProperty("inserts")
    val inserts: List<InsertInfo> = emptyList()
)

data class Manufacturer(
    @JsonProperty("name")
    val name: String,

    @JsonProperty("inn")
    val inn: String
)

data class Seller(
    @JsonProperty("name")
    val name: String,

    @JsonProperty("inn")
    val inn: String,

    @JsonProperty("address")
    val address: String
)

data class JewelryItem(
    val uin: String,
    val name: String,
    val metal: String,
    val sample: String,
    val weight: Double,
    val weightUnit: String
)

data class MetalInfo(
    val baseMetal: String,
    val sample: String
)

data class InsertInfo(
    val type: String,
    val material: String,
    val carat: Double?,
    val quantity: Int,
    val weight: Double?
)