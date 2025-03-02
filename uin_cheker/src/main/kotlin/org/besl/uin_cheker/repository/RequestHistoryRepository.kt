package org.besl.uin_cheker.repository

import org.besl.uin_cheker.entity.RequestUinHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
//import org.springframework.data.repository.Repository

@Repository
public interface RequestHistoryRepository : JpaRepository <RequestUinHistory?, Long?> {
    // Для поиска по UIN с пагинацией
    fun findByUin(uin: String, pageable: Pageable): Page<RequestUinHistory>

    // Для поиска всех записей с сортировкой по дате
    fun findAllByOrderByRequestDateDesc(pageable: Pageable): Page<RequestUinHistory>

    // Для поиска всех записей по UIN без пагинации
    fun findByUin(uin: String): List<RequestUinHistory>
}