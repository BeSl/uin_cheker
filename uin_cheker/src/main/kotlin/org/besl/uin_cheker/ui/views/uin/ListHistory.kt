package org.besl.uin_cheker.ui.views.uin

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import org.besl.uin_cheker.dto.response.HistoryDto
import org.besl.uin_cheker.service.HistoryService
import org.besl.uin_cheker.ui.MainLayout
import org.springframework.boot.availability.ApplicationAvailabilityBean
import java.time.format.DateTimeFormatter

@Route(value = "history", layout = MainLayout::class)
@PageTitle("История проверок")
class ListHistory(
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
            grid.addItemClickListener { event ->
                event.item.let {
//                    dialog.openDetails(it)
//                    dialog.open()
                }
            }
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
        grid.addColumn(HistoryDto::timestamp)
            .setHeader("Дата запроса")
            .setSortable(true)
        grid.addColumn(HistoryDto::uin)
            .setHeader("UIN")
            .setSortable(true)
        grid.addColumn(HistoryDto::typeClient)
            .setHeader("ТипКлиента")
            .setSortable(true)
        grid.addColumn(HistoryDto::capthaText)
            .setHeader("Распознанная капча")
            .setSortable(true)
        grid.addColumn(HistoryDto::responseData)
            .setHeader("Ответ")
            .setSortable(true)
        grid.addColumn(HistoryDto::statusRequest)
            .setHeader("Статус запроса")
            .setSortable(true)

//            .setSortable(true)
//        grid.addColumn { applicationAvailability.timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
//            .setHeader("Timestamp")
//            .setComparator(Comparator.naturalOrder<LocalDateTime>())

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