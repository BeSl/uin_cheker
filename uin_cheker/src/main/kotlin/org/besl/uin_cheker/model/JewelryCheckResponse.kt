package org.besl.uin_cheker.model

class JewelryCheckStatus (
    val mainUin: String,
    val name: String,
    val description: String="",
//    val composition: String="",
    var status: String="NONE",
//    val totalWeight: Double=0.0,
//    val weightUnit: String="",

    // Участники цепочки
//    val manufacturer: Manufacturer?,
//    val seller: Seller?,
//
//    // Комплект изделий
//    val items: List<JewelryItem>?,
//
//    // Характеристики
//    val metalInfo: MetalInfo?,
//    val inserts: List<InsertInfo> = emptyList()
    )

data class Manufacturer(
    val name: String,
    val inn: String
)

data class Seller(
    val name: String,
    val inn: String,
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