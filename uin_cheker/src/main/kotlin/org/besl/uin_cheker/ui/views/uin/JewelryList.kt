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

@Route(value = "jewelrylist", layout = MainLayout::class)
@PageTitle("Все уины")
class JewelryList(
    private val jewelryRepository: JewelryRepository
) : VerticalLayout(){

    private val grid = Grid<JewelryItem>()
    private val detailsLayout = VerticalLayout()
    private var selectedItem: JewelryItem? = null

    init {
        configureGrid()
        configureDetailsLayout()

        val content = HorizontalLayout(grid, detailsLayout)
        content.setSizeFull()
        content.expand(
            grid,

        )

        content.expand(
            detailsLayout,
//            3.0f
        )

        add(content)
        refreshGrid()
    }

    private fun configureGrid() {
        grid.addColumn { it.uin }.setHeader("УИН").setSortable(true)
        grid.addColumn { it.articleNumber }.setHeader("Артикул")
        grid.addColumn { it.isSold }.setHeader("Продано").setSortable(true)
        grid.addColumn { it.shop.address }.setHeader("Магазин")

        grid.setSizeFull()
        grid.selectionMode = Grid.SelectionMode.SINGLE

        grid.addSelectionListener { event ->
            selectedItem = event.firstSelectedItem.orElse(null)
//            updateDetails()
        }
    }

    private fun configureDetailsLayout() {
        detailsLayout.isPadding = true
        detailsLayout.isSpacing = true
        detailsLayout.alignItems = FlexComponent.Alignment.STRETCH

        val header = H3("Детали изделия")
        val noSelection = Div(Text("Выберите элемент из списка"))

        val basicInfo = createSection("Основная информация")
        val uinField = createDetailField("УИН")
        val articleField = createDetailField("Артикул")
        val descriptionField = createDetailField("Описание", true)

        val saleInfo = createSection("Информация о продаже")
        val soldStatus = createDetailField("Статус продажи")
        val sellerField = createDetailField("Продавец")
        val saleDateField = createDetailField("Дата продажи")

        val shopInfo = createSection("Информация о магазине")
        val shopAddress = createDetailField("Адрес магазина")
        val shopOwner = createDetailField("Владелец")

        detailsLayout.add(
            header, noSelection,
            basicInfo, uinField, articleField, descriptionField,
            saleInfo, soldStatus, sellerField, saleDateField,
            shopInfo, shopAddress, shopOwner
        )

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

//    private fun updateDetails() {
//        val item = selectedItem
//        if (item != null) {
//            // Основная информация
//            getComponentAt(1, uinField.index).value = item.uin
//            getComponentAt(1, articleField.index).value = item.articleNumber
//            getComponentAt(1, descriptionField.index).value = item.description
//
//            // Информация о продаже
//            getComponentAt(1, soldStatus.index).value = if (item.isSold) "Продано" else "В наличии"
//            getComponentAt(1, sellerField.index).value = item.seller?.name ?: "Не указан"
//            getComponentAt(1, saleDateField.index).value =
//                item.soldDate?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) ?: ""
//
//            // Информация о магазине
//            getComponentAt(1, shopAddress.index).value = item.shop.address
//            getComponentAt(1, shopOwner.index).value = item.shop.contractor.name
//
//            detailsLayout.isVisible = true
//        } else {
//            detailsLayout.isVisible = false
//        }
//    }

    private fun refreshGrid() {
        grid.setItems(
            DataProvider.fromCallbacks(
            { query -> jewelryRepository.findAll(PageRequest.of(query.page, query.pageSize)).stream() },
            { query -> jewelryRepository.count().toInt() }
        ))
    }
}