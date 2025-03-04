package org.besl.uin_cheker.service

import JewelryCheckResponse
import jakarta.transaction.Transactional
import org.besl.uin_cheker.repository.ContractorRepository
import org.besl.uin_cheker.repository.JewelryMapper
import org.besl.uin_cheker.repository.JewelryRepository
import org.besl.uin_cheker.repository.ShopRepository
import org.springframework.stereotype.Service

@Service
class JewelryService(
    private val jewelryRepository: JewelryRepository,
    private val sellerRepository: ContractorRepository,
    private val mapper: JewelryMapper,
    private val shopRepository: ShopRepository
) {
    @Transactional
    fun saveJewelryCheckResponse(response: JewelryCheckResponse) {
        // Check if already exists
        jewelryRepository.findById(response.mainUin).ifPresent {
            throw IllegalArgumentException("Jewelry with UIN ${response.mainUin} already exists")
        }

        // Convert DTO to Entity
        val entity = mapper.toEntity(response)

        // Save related entities first
        sellerRepository.save(entity.seller!!)
        shopRepository.save(entity.shop!!)
        // Save main entity
        jewelryRepository.save(entity)
    }
}