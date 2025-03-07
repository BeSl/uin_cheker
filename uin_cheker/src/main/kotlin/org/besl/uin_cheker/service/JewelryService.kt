package org.besl.uin_cheker.service

import JewelryCheckResponse
import jakarta.transaction.Transactional
import org.besl.uin_cheker.entity.JewelryItem
import org.besl.uin_cheker.repository.ContractorRepository
import org.besl.uin_cheker.repository.JewelryMapper
import org.besl.uin_cheker.repository.JewelryRepository
import org.besl.uin_cheker.repository.ShopRepository
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer

@Service
class JewelryService(
    private val jewelryRepository: JewelryRepository,
    private val sellerRepository: ContractorRepository,
    private val mapper: JewelryMapper,
    private val shopRepository: ShopRepository,
    private val pageable: PageableHandlerMethodArgumentResolverCustomizer
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

//    fun list(pageable: Pageable, filter: Specification<JewelryItem>?): Page<JewelryItem> {
//        return filter?.let {
//            jewelryRepository.findAll(it, pageable)
//        } ?: jewelryRepository.findAll(pageable)
//    }

    fun list(filter: Specification<JewelryItem>? = null, pageable: Pageable = Pageable.unpaged()): Page<JewelryItem> {
        return filter?.let {
            jewelryRepository.findAll(it, pageable)
        } ?: jewelryRepository.findAll(pageable)
    }

    fun count(): Long {
        return jewelryRepository.count()
    }
}