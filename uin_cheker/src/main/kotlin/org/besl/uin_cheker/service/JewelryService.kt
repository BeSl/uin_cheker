package org.besl.uin_cheker.service

import JewelryCheckResponse
import jakarta.transaction.Transactional
import org.besl.uin_cheker.entity.JewelryItem
import org.besl.uin_cheker.repository.ContractorRepository
import org.besl.uin_cheker.repository.JewelryMapper
import org.besl.uin_cheker.repository.JewelryRepository
import org.besl.uin_cheker.repository.ShopRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation

@Service
class JewelryService(
    private val jewelryRepository: JewelryRepository,
    private val sellerRepository: ContractorRepository,
    private val mapper: JewelryMapper,
    private val shopRepository: ShopRepository
) {
    @Transactional
    fun saveOrUpdateJewelryCheckResponse(response: JewelryCheckResponse) {
        val existingEntity = jewelryRepository.findById(response.mainUin).orElse(null)

        if (existingEntity != null) {
            // Обновление существующей записи
            updateExistingEntity(existingEntity, response)
        } else {
            // Создание новой записи
            createNewEntity(response)
        }
    }

    private fun createNewEntity(response: JewelryCheckResponse): JewelryItem {
        val entity = mapper.toEntity(response).apply {
            seller?.let { sellerRepository.save(it) }
            shop?.let { shopRepository.save(it) }
        }

        val savedEntity = jewelryRepository.save(entity)
        return savedEntity
    }

    private fun updateExistingEntity(existing: JewelryItem, response: JewelryCheckResponse): JewelryItem {
        // Обновляем поля
        val entity = mapper.toEntity(response)
        val updatedEntity = existing.apply {
            description = response.description ?: description
            isSold = entity.isSold
        }

        // Сохраняем изменения
        val savedEntity = jewelryRepository.save(updatedEntity)
        return savedEntity
    }
//        // Convert DTO to Entity
//        val entity = mapper.toEntity(response)
//
//        // Save related entities first
//        sellerRepository.save(entity.seller!!)
//        shopRepository.save(entity.shop)
//        // Save main entity
//        jewelryRepository.saveAndFlush(entity)
//    }
}