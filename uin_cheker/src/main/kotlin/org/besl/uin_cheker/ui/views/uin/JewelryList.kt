package org.besl.uin_cheker.ui.views.uin

import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.splitlayout.SplitLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers
import jakarta.persistence.criteria.Predicate
import org.besl.uin_cheker.entity.JewelryItem
import org.besl.uin_cheker.service.JewelryService
import org.besl.uin_cheker.ui.MainLayout
import org.springframework.data.jpa.domain.Specification
import java.time.format.DateTimeFormatter

@Route(value = "jewelrylist", layout = MainLayout::class)
@PageTitle("Все уины")
class JewelryList(
    private val jewelryService: JewelryService
) : Div() {
    private val detailsLayout = VerticalLayout()
    private var selectedItem: JewelryItem? = null

    private lateinit var uinField: TextField
    private lateinit var articleField: TextField
    private lateinit var descriptionField: TextArea
    private lateinit var soldStatus: TextField
    private lateinit var sellerField: TextArea
    private lateinit var saleDateField: TextField
    private lateinit var shopAddress: TextArea
    private lateinit var shopOwner: TextField

    private val grid = Grid<JewelryItem>(JewelryItem::class.java).apply {
        height = "100%"
        addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES)
    }

    private val filterLayout = HorizontalLayout().apply {
        width = "100%"
        addClassName("filter-layout")
    }

    private val uinFilter = TextField("Фильтр по УИН").apply {
        setWidth("200px")
        addValueChangeListener { refreshGrid() }
    }

    private val articleFilter = TextField("Фильтр по артикулу").apply {
        setWidth("200px")
        addValueChangeListener { refreshGrid() }
    }

    private val statusFilter = ComboBox<String>("Статус продажи").apply {
        setItems("Все", "Продано", "В наличии")
        setValue("Все")
        addValueChangeListener { refreshGrid() }
    }

    // Детальная панель
    private val detailsPanel = VerticalLayout().apply {
        width = "30%"
        isSpacing = false
        isPadding = true
    }

    init {
        setupLayout()
        configureGrid()
        setupFilters()
        setupDetailsPanel()
    }

    private fun setupLayout() {
        val splitLayout = SplitLayout().apply {
            width = "100%"
            height = "calc(100vh - 120px)"
            splitterPosition = 70.0
        }

        val mainLayout = VerticalLayout().apply {
            add(filterLayout, splitLayout)
            setSizeFull()
        }

        splitLayout.addToPrimary(VerticalLayout(grid).apply { setSizeFull() })
        splitLayout.addToSecondary(detailsPanel)

        add(mainLayout)
    }

    private fun configureGrid() {
        grid.removeAllColumns()

        with(grid) {
            addColumn { it.uin }
                .setHeader("УИН")
                .setSortable(true)
                .setAutoWidth(true)

            addColumn { it.articleNumber }
                .setHeader("Артикул")
                .setAutoWidth(true)

            addColumn { if (it.isSold) "Продано" else "В наличии" }
                .setHeader("Статус")
                .setAutoWidth(true)

            addColumn { it.shop?.address ?: "" }
                .setHeader("Адрес магазина")
                .setAutoWidth(true)

            setItems { query ->
                jewelryService.list(
                    pageable = VaadinSpringDataHelpers.toSpringPageRequest(query),
                    filter = buildFilterSpec()
                ).stream()
            }

            addSelectionListener { event ->
                event.firstSelectedItem.ifPresentOrElse(
                    { updateDetails(it) },
                    { clearDetails() }
                )
            }
        }
    }

    private fun setupFilters() {
        filterLayout.add(
            uinFilter,
            articleFilter,
            statusFilter
        )
    }

    private fun setupDetailsPanel() {
        val header = H3("Детальная информация").apply {
            style["margin-top"] = "0"
        }

        val formLayout = FormLayout().apply {
            responsiveSteps = listOf(
                FormLayout.ResponsiveStep("0", 1),
                FormLayout.ResponsiveStep("500px", 2)
            )
        }

        val fields = listOf(
            TextField("УИН").apply { uinField = this  }.apply { isReadOnly = true },
            TextField("Артикул").apply { articleField = this }.apply { isReadOnly = true },
            TextField("Статус").apply { soldStatus = this }.apply { isReadOnly = true },
            TextField("Дата продажи").apply { saleDateField = this }.apply { isReadOnly = true },
            TextArea("Описание").apply { descriptionField = this }.apply { isReadOnly = true },
            TextArea("Продавец").apply { sellerField = this }.apply { isReadOnly = true },
            TextArea("Адрес магазина").apply { shopAddress = this }.apply { isReadOnly = true },
        )

        fields.forEach {
            it.width = "100%"
            formLayout.add(it)
        }

        detailsPanel.add(header, formLayout)
    }

    private fun buildFilterSpec(): Specification<JewelryItem>? {
        return Specification { root, _, criteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            uinFilter.value.takeIf { it.isNotBlank() }?.let {
                predicates.add(criteriaBuilder.like(root.get("uin"), "%${it}%"))
            }

            articleFilter.value.takeIf { it.isNotBlank() }?.let {
                predicates.add(criteriaBuilder.like(root.get("articleNumber"), "%${it}%"))
            }

            when (statusFilter.value) {
                "Продано" -> predicates.add(criteriaBuilder.isTrue(root.get("isSold")))
                "В наличии" -> predicates.add(criteriaBuilder.isFalse(root.get("isSold")))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    private fun updateDetails(item: JewelryItem) {
        uinField.value = item.uin
        articleField.value = item.articleNumber
        descriptionField.value = item.description ?: ""
        soldStatus.value = if (item.isSold) "Продано" else "В наличии"
        saleDateField.value = item.soldDate?.format(DateTimeFormatter.ISO_DATE) ?: ""
        sellerField.value = item.seller?.name ?: ""
        shopAddress.value = item.shop?.address ?: ""
        shopOwner.value = item.shop?.contractor?.name ?: ""
    }

    private fun clearDetails() {
        listOf(
            uinField, articleField, descriptionField,
            soldStatus, saleDateField, sellerField,
            shopAddress, shopOwner
        ).forEach { it.clear() }
    }

    private fun refreshGrid() {
        grid.dataProvider.refreshAll()
    }
}