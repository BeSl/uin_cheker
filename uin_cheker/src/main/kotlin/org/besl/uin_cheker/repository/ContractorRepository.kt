package org.besl.uin_cheker.repository

import org.besl.uin_cheker.entity.Contractor
import org.besl.uin_cheker.entity.Shop
import org.springframework.data.jpa.repository.JpaRepository

interface ShopRepository : JpaRepository<Shop, Long> {
//    fun findByContractor(contractor: Contractor): List<Shop>
}

interface ContractorRepository : JpaRepository<Contractor, String>