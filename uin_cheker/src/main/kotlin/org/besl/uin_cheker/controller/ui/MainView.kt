package org.besl.uin_cheker.controller.ui

import JewelryCheckResponse
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import org.besl.uin_cheker.service.ProbPalataService
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.Scroller
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.data.value.ValueChangeMode
import org.besl.uin_cheker.repository.RequestHistoryRepository
import com.vaadin.flow.theme.lumo.LumoUtility
import org.besl.uin_cheker.model.HistoryDto
import org.besl.uin_cheker.service.HistoryService
import org.springframework.boot.availability.ApplicationAvailabilityBean
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Route("")
class MainLayout : AppLayout() {
    init {
        val toggle = DrawerToggle()

        val title = H1("Сервис проверки статуса УИН").apply {
            style
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0")
        }

        val nav = createSideNav()
        val scroller = Scroller(nav).apply {
            addClassName(LumoUtility.Padding.SMALL)
        }

        addToDrawer(scroller)
        addToNavbar(toggle, title)
    }
    private fun createSideNav(): SideNav  {
        return SideNav().apply {
            addItem(
                SideNavItem("Поиск УИН","/cheker", VaadinIcon.CHECK.create()),
                SideNavItem("История проверок", "/history", VaadinIcon.FILE_SEARCH.create())
            )
        }
    }
}

@Route(value = "cheker", layout = MainLayout::class)
@PageTitle("Поиск УИН")
class SearchView(
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



@Route(value = "history", layout = MainLayout::class)
@PageTitle("История проверок")
class HistoryView(
    private val historyService: HistoryService,
    private val applicationAvailability: ApplicationAvailabilityBean
) : VerticalLayout() {
    private val grid = Grid<HistoryDto>()
    private val uinFilter = TextField("UIN")
    private val typeFilter = TextField("Type")

    init {
        configureLayout()
//        setupListeners()
    }
    private fun configureLayout() {
        setSizeFull()
//        justifyContentMode = FlexComponent.JustifyContentMode.START
//        alignItems = FlexComponent.Alignment.CENTER
        val formLayout = VerticalLayout().apply {
            width = "100%"
            height = "100%"
            addClassName("main-form")
            add(H1("История запросов"))
            configureFilters()
            configureGrid()
            add(createToolbar(), grid)

        }

        add(formLayout)
    }
    private fun createToolbar(): HorizontalLayout {
        val filterButton = Button("Отбор", VaadinIcon.SEARCH.create()) {
            updateList()
        }

        return HorizontalLayout(
            uinFilter,
            typeFilter,
            filterButton
        ).apply {
            setAlignItems(FlexComponent.Alignment.BASELINE)
//            spacing = true
        }
    }

    private fun configureFilters() {
        uinFilter.setPlaceholder("УИН...")
        typeFilter.setPlaceholder("Тип...")

        // Live filtering (optional)
        listOf(uinFilter, typeFilter).forEach {
            it.valueChangeMode = ValueChangeMode.LAZY
            it.addValueChangeListener { updateList() }
        }
    }

    private fun configureGrid() {
        grid.setSizeFull()
        grid.addColumn(HistoryDto::uin)
            .setHeader("UIN")
            .setSortable(true)
//        grid.addColumn(HistoryDto::requestType)
//            .setHeader("Type")
//            .setSortable(true)
//        grid.addColumn { applicationAvailability.timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
//            .setHeader("Timestamp")
//            .setComparator(Comparator.naturalOrder<LocalDateTime>())
//        grid.addColumn(HistoryDto::userId)
//            .setHeader("User ID")

        // Update data
        updateList()
    }

    private fun updateList() {
        grid.setItems(
            historyService.getHistory(
                uin = uinFilter.value.takeIf { it.isNotBlank() },
                typeClient = typeFilter.value.takeIf { it.isNotBlank() }
            )
        )
    }
}