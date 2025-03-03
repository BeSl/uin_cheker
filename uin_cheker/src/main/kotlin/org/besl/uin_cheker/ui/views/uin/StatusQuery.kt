package org.besl.uin_cheker.ui.views.uin

import JewelryCheckResponse
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import org.besl.uin_cheker.service.ProbPalataService
import org.besl.uin_cheker.ui.MainLayout


@Route(value = "checker", layout = MainLayout::class)
@PageTitle("Поиск УИН")
class StatusQuery(
        private val jewelryService: ProbPalataService
) : VerticalLayout() {

        private val uinField = TextField("УИН")
        private val checkButton = Button("Проверить")

        // Компоненты для отображения результата
        private val statusField = TextField("Статус").apply { isReadOnly = true }
        private val nameField = TextField("Наименование").apply { isReadOnly = true }
        private val descriptionField = TextField("Описание").apply { isReadOnly = true }

        private val seller_name = TextField("Наименование").apply { isReadOnly = true }
        private val seller_inn = TextField("ИНН").apply { isReadOnly = true }
        private val seller_adress = TextField("Адрес").apply { isReadOnly = true }


        private val resultLayout = VerticalLayout().apply {
            isVisible = false
            width = "100%"
            isSpacing = false
        }

        init {
            configureLayout()
            setupListeners()
        }

        private fun configureLayout() {
            setSizeFull()
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
            alignItems = FlexComponent.Alignment.CENTER

            val formLayout = VerticalLayout().apply {
                width = "50%"
                addClassName("main-form")

                add(
                    H1("Проверка статуса ювелирных изделий"),
                    uinField.apply {
                        width = "100%"
                        pattern = "\\d{16}"
                        maxLength = 16
                        setRequired(true)
                    },
                    checkButton,
                    resultLayout.apply {
                        add(
                            createSectionHeader("Результат проверки"),
                            statusField,
                            nameField,
                            descriptionField
                        )
                        add(
                            createSectionHeader("Продавец"),
                            seller_name,
                            seller_inn,
                            seller_adress
                        )
                    }
                )
            }

            add(formLayout)
        }

        private fun setupListeners() {
            checkButton.addClickListener {
                if (!uinField.isEmpty) {
                    checkButton.isEnabled = false

                    try {
                        val response = jewelryService.getAsyncStatus(uinField.value, "WEB")
                        showResult(response)
                    } catch (e: Exception) {
                        Notification.show("Ошибка: ${e.message}", 5000, Notification.Position.BOTTOM_CENTER)
                    } finally {
                        checkButton.isEnabled = true
                    }
                }
            }
        }

        private fun showResult(response: JewelryCheckResponse) {
            statusField.value = response.status ?: "Не указано"
            nameField.value = response.description ?: "Не указано"
            descriptionField.value = response.composition ?: "" + response.seller?.name ?: ""
            seller_inn.value = response.seller.inn?:""
            seller_name.value = response.seller.name?:""
            seller_adress.value = response.seller.address?:""

            // Стилизация статуса
            when(response.status?.lowercase()) {
                "продано" -> {
                    statusField.addThemeNames()

                }  //.LUMO_ERROR.getVariantName())
                "в наличии" -> statusField.addThemeNames()
                else -> statusField.removeThemeNames(

                )
            }

            resultLayout.isVisible = true
        }

        private fun createSectionHeader(text: String): H4 {
            return H4(text).apply {
                style["margin-top"] = "1em"
                style["margin-bottom"] = "0.5em"
                style["color"] = "var(--lumo-primary-text-color)"
            }
        }

    }


