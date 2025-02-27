package org.besl.uin_cheker.controller.ui

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.json.JsonWriteFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.vaadin.flow.component.Tag.H1
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Pre
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import org.besl.uin_cheker.model.RequestUinHistory
import org.besl.uin_cheker.service.ProbPalataService

@Route("") // Корневой путь
class MainView(
    private val jewelryService: ProbPalataService
) : VerticalLayout() {

    private val uinField = TextField("УИН")
    private val resultArea = Pre().apply { style["whiteSpace"] = "pre-wrap" }
    private val checkButton = Button("Проверить")

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
                resultArea.apply {
                    width = "100%"
                    isVisible = false
                }
            )
        }

        add(formLayout)
    }

    private fun setupListeners() {
        checkButton.addClickListener {
            if (!uinField.isEmpty) {
                checkButton.isEnabled = false
                resultArea.isVisible = false

                try {
                    val history = RequestUinHistory(requestData = uinField.value,)
                    val json = jewelryService.getAsyncStatus(uinField.value, history)
                    showResult(json)
                } catch (e: Exception) {
                    Notification.show("Ошибка: ${e.message}", 5000, Notification.Position.BOTTOM_CENTER)
                } finally {
                    checkButton.isEnabled = true
                }
            }
        }
    }

    private fun showResult(json: String) {
        resultArea.element.text = json
        resultArea.isVisible = true
    }

//    private fun String.toPrettyJson(): String {
//        val mapper = ObjectMapper(JsonFactory().enable(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS))
//        val jsonNode = mapper.readTree(this)
//        return jsonNode.toPrettyString()
//    }

}