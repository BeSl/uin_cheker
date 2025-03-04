package org.besl.uin_cheker.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
class Shop(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var address: String,

    @ManyToOne(optional = false)
    @JoinColumn(name = "contractor_id")
    @NotNull
    var contractor: Contractor? = null,
) {
    constructor() : this(address = "") {

    }

    override fun toString(): String {
        return "Shop(id=$id, address='$address', contractor=${contractor?.inn})"
    }
}

@Entity
@Table(name = "contractor")
class Contractor(
    @Id
    val inn: String,

    @Column
    val name: String,
    @OneToMany(mappedBy = "contractor", cascade = [CascadeType.ALL], orphanRemoval = true)
    val shops: MutableList<Shop> = mutableListOf()

) {
    constructor() : this(inn = "", name= "", shops = mutableListOf()) {

    }

    fun addShop(shop: Shop) {
        shops.add(shop)
        shop.contractor = this
    }

    fun removeShop(shop: Shop) {
        shops.remove(shop)
        shop.contractor = null
    }

    override fun toString(): String {
        return "Contractor(id=$inn, name='$name', shops=${shops.size})"
    }
}
