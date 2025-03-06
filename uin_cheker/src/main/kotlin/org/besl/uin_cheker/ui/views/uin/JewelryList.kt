package org.besl.uin_cheker.ui.views.uin

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import org.besl.uin_cheker.entity.JewelryItem
import org.besl.uin_cheker.repository.JewelryRepository
import org.besl.uin_cheker.ui.MainLayout
import org.springframework.data.domain.PageRequest
import java.time.format.DateTimeFormatter

@Route(value = "jewelrylist", layout = MainLayout::class)
@PageTitle("Все уины")
class JewelryList(
    private val jewelryRepository: JewelryRepository
) : VerticalLayout(){

    private val grid = Grid<JewelryItem>()
    private val detailsLayout = VerticalLayout()
    private var selectedItem: JewelryItem? = null

    private lateinit var uinField: TextField
    private lateinit var articleField: TextField
    private lateinit var descriptionField: TextArea
    private lateinit var soldStatus: TextField
    private lateinit var sellerField: TextField
    private lateinit var saleDateField: TextField
    private lateinit var shopAddress: TextField
    private lateinit var shopOwner: TextField
    private lateinit var noSelection: Div
    private lateinit var detailsContent: VerticalLayout

    init {
        configureGrid()
        configureDetailsLayout()

        val content = HorizontalLayout().apply {
            setSizeFull()
            add(grid, detailsLayout)
            setFlexGrow(7.0, grid)  // 70% для grid
            setFlexGrow(3.0, detailsLayout)  // 30% для detailsLayout
        }

        add(content)
        expand(content)
        refreshGrid()
    }

    private fun configureGrid() {
        height = "100%"
        width = "75%"
        grid.height = "100%"
        grid.width = "70%"
        grid.addColumn { it.uin }.setHeader("УИН").setSortable(true)
        grid.addColumn { it.articleNumber }.setHeader("Артикул")
        grid.addColumn { it.isSold }.setHeader("Продано").setSortable(true)
        grid.addColumn { it.shop.address }.setHeader("Магазин")

        grid.setSizeFull()
        grid.selectionMode = Grid.SelectionMode.SINGLE

        grid.addSelectionListener { event ->
            selectedItem = event.firstSelectedItem.orElse(null)
            updateDetails()
        }

//        grid.addItemDoubleClickListener { item -> }
    }

    private fun configureDetailsLayout() {
//        height = "100%"
//        width = "75%"
//        detailsLayout.height = "100%"
//        detailsLayout.width = "30%"
//        detailsLayout.setSizeFull()
//        detailsContent.alignItems = FlexComponent.Alignment.STRETCH
        detailsLayout.isPadding = true
        detailsLayout.isSpacing = true
        detailsLayout.alignItems = FlexComponent.Alignment.STRETCH

        val header = H3("Описание УИНа")
        noSelection = Div(Text("Выберите элемент из списка"))

        // Создание полей
        uinField = createDetailField("УИН") as TextField
        articleField = createDetailField("Артикул") as TextField
        descriptionField = createDetailField("Описание", true) as TextArea
        soldStatus = createDetailField("Статус продажи") as TextField
        sellerField = createDetailField("Продавец") as TextField
        saleDateField = createDetailField("Дата продажи") as TextField
        shopAddress = createDetailField("Адрес магазина") as TextField
        shopOwner = createDetailField("Владелец") as TextField

        // Секции
        val basicInfo = createSection("Основная информация")
        val saleInfo = createSection("Информация о продаже")
        val shopInfo = createSection("Информация о магазине")

        detailsContent = VerticalLayout(
            basicInfo, uinField, articleField, descriptionField,
            saleInfo, soldStatus, sellerField, saleDateField,
            shopInfo, shopAddress, shopOwner
        ).apply {
            isSpacing = true
            isPadding = false
            isVisible = false
        }

        detailsLayout.add(header, noSelection, detailsContent)
        detailsLayout.isVisible = false
    }

    private fun createSection(title: String): Component {
        return Div(Text(title)).apply {
            style.set("font-weight", "bold")
            style.set("margin-top", "1em")
        }
    }

    private fun createDetailField(label: String, multiline: Boolean = false): Component {
        val field = if (multiline) {
            TextArea(label).apply {
                isReadOnly = true
                width = "100%"
            }
        } else {
            TextField(label).apply {
                isReadOnly = true
                width = "100%"
            }
        }
        return field
    }

    private fun updateDetails() {
        val item = selectedItem
        if (item != null) {
            // Основная информация
            uinField.value = item.uin
            articleField.value = item.articleNumber
            descriptionField.value = item.description ?: ""

            // Информация о продаже
            soldStatus.value = if (item.isSold) "Продано" else "В наличии"
            sellerField.value = item.seller?.name ?: "Не указан"
            saleDateField.value = item.soldDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: ""

            // Информация о магазине
            shopAddress.value = item.shop.address
            shopOwner.value = item.shop.contractor!!.name

            // Переключение видимости
            noSelection.isVisible = false
            detailsContent.isVisible = true
            detailsLayout.isVisible = true
        } else {
            detailsLayout.isVisible = false
            noSelection.isVisible = true
            detailsContent.isVisible = false
        }
    }

    private fun refreshGrid() {
        grid.setItems(
            DataProvider.fromCallbacks(
            { query -> jewelryRepository.findAll(PageRequest.of(query.page, query.pageSize)).stream() },
            { query -> jewelryRepository.count().toInt() }
        ))
    }
}