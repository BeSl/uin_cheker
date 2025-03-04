package org.besl.uin_cheker.repository

import JewelryCheckResponse
import Seller
import org.besl.uin_cheker.entity.Contractor
import org.besl.uin_cheker.entity.JewelryItem
import org.besl.uin_cheker.entity.Shop
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component

interface JewelryRepository : JpaRepository<JewelryItem, String> {
    fun findByArticleNumber(articleNumber: String): JewelryItem?
    fun findByShopAndIsSoldFalse(shop: Shop): List<JewelryItem>
    fun findBySeller(seller: Contractor): List<JewelryItem>
}

@Component
class JewelryMapper {
    fun toEntity(response: JewelryCheckResponse): JewelryItem {
        val seller = mapSeller(response.seller)
        return JewelryItem(
            uin = response.mainUin,
            description = response.description?:"none",
            articleNumber = response.name,
            isSold = mapSold(response.status),
            seller = seller,
            shop = mapShop(response.seller.address, seller)
        )
    }

    private fun mapSeller(dto: Seller): Contractor {
        return Contractor(
            inn = dto.inn,
            name = dto.name,
        )
    }

    private fun mapShop(address: String, seller: Contractor): Shop {
        return Shop(address = address, contractor = seller)
    }
    private fun mapSold(status: String): Boolean{
        return (status=="продано")
    }

}