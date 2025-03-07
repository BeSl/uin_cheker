package org.besl.uin_cheker.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import java.time.LocalDateTime

@Entity
@Table(name = "jewelry_item")
class JewelryItem(
    @Id
    @Column(unique = true, length = 16)
//                     @Pattern(regexp = "^[A-Z0-9]{10,20}$") // Пример формата УИН
    open val uin: String,

    @NotBlank
    @Column(nullable = false, length = 500)
    var description: String,

    @NotBlank
    @Column(unique = true, nullable = false, length = 50)
    var articleNumber: String = "none",

    @Column(nullable = false)
   open var isSold: Boolean = false,

    @ManyToOne(optional = false)
    @JoinColumn(name = "shop_id")
    @NotNull
    var shop: Shop,

    @ManyToOne
    @JoinColumn(name = "seller_id")
    var seller: Contractor? = null,

    @Column
    var soldDate: LocalDateTime? = null
) {
    protected constructor() : this(
        uin = "",
        description = "",
        articleNumber = "",
        isSold = false,
        shop = Shop(),
        seller = null,
        soldDate = null
    )
    // Бизнес-метод для отметки о продаже
    fun markAsSold(seller: Contractor) {
        require(!isSold) { "Товар уже продан" }
        this.isSold = true
        this.seller = seller
        this.soldDate = LocalDateTime.now()
    }

    override fun toString(): String {
        return "JewelryItem(uin='$uin', article='$articleNumber', sold=$isSold)"
    }
}