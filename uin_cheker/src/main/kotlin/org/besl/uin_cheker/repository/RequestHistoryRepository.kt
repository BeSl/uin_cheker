package org.besl.uin_cheker.repository

import org.besl.uin_cheker.entity.RequestUinHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

//import org.springframework.data.repository.Repository

@Repository
public interface RequestHistoryRepository : JpaRepository <RequestUinHistory?, Long?> {
    // Для поиска по UIN с пагинацией
    fun findByUin(uin: String, pageable: Pageable): Page<RequestUinHistory>

    // Для поиска всех записей с сортировкой по дате
    fun findAllByOrderByRequestDateDesc(pageable: Pageable): Page<RequestUinHistory>
    fun findByUin(uin: String): List<RequestUinHistory>

    @Query("""
        SELECT h FROM request_history h
        WHERE (:uin IS NULL OR h.uin = :uin)
        AND (:typeClient IS NULL OR h.source = :typeClient) 
    """)
    fun findFiltered(
            @Param("uin") uin: String?,
            @Param("typeClient") typeClient: String?,
    ): List<RequestUinHistory>
}