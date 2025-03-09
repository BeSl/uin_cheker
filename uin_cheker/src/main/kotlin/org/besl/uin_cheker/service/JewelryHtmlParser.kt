package org.besl.uin_cheker.service

import InsertInfo
import JewelryCheckResponse
import JewelryItem
import Manufacturer
import MetalInfo
import Seller

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.lang.NumberFormatException

class JewelryHtmlParser {

    fun parse(html: String): JewelryCheckResponse {
        val doc = Jsoup.parse(html)

        return JewelryCheckResponse(
            mainUin = extractMainUin(doc),
            name = extractValue(doc, "Наименование"),
            description = extractValueOrNull(doc, "Описание"),
            composition = extractValue(doc, "Состав комплекта"),
            status = extractValue(doc, "Статус"),
            totalWeight = extractTotalWeight(doc),
            weightUnit = extractWeightUnit(doc),
            manufacturer = extractManufacturer(doc),
            seller = extractSeller(doc),
            items = extractItems(doc),
            metalInfo = extractMetalInfo(doc),
            inserts = extractInserts(doc)
        )
    }

    private fun extractMainUin(doc: Document): String {
        val fn = doc.selectFirst("div.check-result-row:has(p.check-result-row__label:contains(УИН комплекта))")
            ?.selectFirst("p.check-result-row__value")
            ?.text()
            ?.replace(" ", "") //? :

        if (fn != null) {
            return fn
        }else{
            return doc.selectFirst("div.check-result-row:has(p.check-result-row__label:contains(УИН))")
                        ?.selectFirst("p.check-result-row__value")
                            ?.text()
                                ?.replace(" ", "") ?:throw ParseException("Parse UIN error")
        }
    }

    private fun extractValue(doc: Document, label: String): String {
        return doc.selectFirst("div.check-result-row:has(p.check-result-row__label:contains($label))")
            ?.selectFirst("p.check-result-row__value")
            ?.text() ?: ""
    }

    private fun extractValueOrNull(doc: Document, label: String): String? {
        return doc.selectFirst("div.check-result-row:has(p.check-result-row__label:contains($label))")
            ?.selectFirst("p.check-result-row__value")
            ?.text()
    }

    private fun extractTotalWeight(doc: Document): Double {
        val weightText = extractValue(doc, "Вес")
        return weightText.replace("[^\\d.]".toRegex(), "").toDouble()
    }

    private fun extractWeightUnit(doc: Document): String {
        val weightText = extractValue(doc, "Вес")
        return weightText.replace("[\\d.\\s]".toRegex(), "")
    }

    private fun extractManufacturer(doc: Document): Manufacturer {
        val element = doc.selectFirst("div.check-result-row:has(p.check-result-row__label:contains(Производитель))")
        return Manufacturer(
            name = element?.selectFirst("p.check-result-row__value")?.ownText()?.trim() ?: "",
            inn = element?.selectFirst("br")?.nextSibling()?.toString()?.substringAfter("ИНН:")?.trim() ?: ""
        )
    }

    private fun extractSeller(doc: Document): Seller {
        val element = doc.selectFirst("div.check-result-row:has(p.check-result-row__label:contains(Продавец))")
        return Seller(
            name = element?.selectFirst("p.check-result-row__value")?.ownText()?.trim() ?: "",
            inn = element?.selectFirst("br")?.nextSibling()?.toString()?.substringAfter("ИНН:")?.trim() ?: "",
            address = element?.selectFirst("br:nth-of-type(2)")?.nextSibling()?.toString()?.substringAfter("Адрес:")?.trim() ?: ""
        )
    }

    private fun extractItems(doc: Document): List<JewelryItem> {
        return doc.select("div.check-result-row:has(p.check-result-row__label:contains(УИН изделия))").map { element ->
            JewelryItem(
                uin = element.selectFirst("p.check-result-row__value")?.text() ?: "",
                name = element.selectFirst("p.check-result-row__label br + br")?.nextSibling()?.toString()?.trim() ?: "",
                metal = element.selectFirst("p.check-result-row__label:contains(Базовый металл) + p")?.text() ?: "",
                sample = element.selectFirst("p.check-result-row__label:contains(Проба) + p")?.text() ?: "",
                weight = parseWeight(element.selectFirst("p.check-result-row__label:contains(Вес) + p")?.text()),
                weightUnit = element.selectFirst("p.check-result-row__label:contains(Единица измерения веса) + p")?.text() ?: "г"
            )
        }
    }

    private fun extractMetalInfo(doc: Document): MetalInfo {
        val metalTab = doc.selectFirst("div.tabs__content[data-tabs-content=metall]")
        return MetalInfo(
            baseMetal = metalTab?.selectFirst("div.check-result-row:has(p.check-result-row__label:contains(Базовый металл))")
                ?.selectFirst("p.check-result-row__value")
                ?.text() ?: "",
            sample = metalTab?.selectFirst("div.check-result-row:has(p.check-result-row__label:contains(Проба))")
                ?.selectFirst("p.check-result-row__value")
                ?.text() ?: ""
        )
    }

    private fun extractInserts(doc: Document): List<InsertInfo> {
        val insertTab = doc.selectFirst("div.tabs__content[data-tabs-content=inserts]")
        return insertTab?.select("div.check-result-row")?.mapNotNull { element ->
            val label = element.selectFirst("p.check-result-row__label")?.text() ?: return@mapNotNull null
            val value = element.selectFirst("p.check-result-row__value")?.text() ?: ""

            when {
                label.contains("Тип") -> InsertInfo(
                    type = value,
                    material = "",
                    carat = null,
                    quantity = 0,
                    weight = null
                )
                // Добавьте другие случаи по необходимости
                else -> null
            }
        } ?: emptyList()
    }

    private fun parseWeight(text: String?): Double {
        return try {
            text?.replace("[^\\d.]".toRegex(), "")?.toDouble() ?: 0.0
        } catch (e: NumberFormatException) {
            0.0
        }
    }
}

class ParseException(message: String) : RuntimeException(message)