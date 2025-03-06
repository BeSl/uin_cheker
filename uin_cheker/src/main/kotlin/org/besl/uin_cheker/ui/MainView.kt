package org.besl.uin_cheker.ui

import JewelryCheckResponse
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import org.besl.uin_cheker.service.ProbPalataService
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.Scroller
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.dialog.Dialog;

import com.vaadin.flow.component.html.*
import com.vaadin.flow.data.value.ValueChangeMode

import com.vaadin.flow.theme.lumo.LumoUtility
import org.besl.uin_cheker.dto.response.HistoryDto
import org.besl.uin_cheker.service.HistoryService
import org.springframework.boot.availability.ApplicationAvailabilityBean
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
                SideNavItem("Поиск УИН","/checker", VaadinIcon.CHECK.create()),
                SideNavItem("Коллекция уин", "/jewelrylist", VaadinIcon.ARROWS.create()),
                SideNavItem("История проверок", "/history", VaadinIcon.FILE_SEARCH.create())
            )
        }
    }
}



private class HistoryDetailDialog : Dialog() {
    private val content = VerticalLayout()

    init {
        isModal = true
        isDraggable = true
        isResizable = true
        add(content)
    }

    fun openDetails(item: HistoryDto) {
        removeAll()
        content.add(
            H3("Details for request #${item.uin}"),
            Div().apply {
                addClassName("detail-section")
                add(
                    item.uin?.let { createDetailItem("UIN:", it) },
//                    createDetailItem("Type:", item.requestType),
//                    createDetailItem("User ID:", item.userId.toString()),
                    createDetailItem("Timestamp:",
                        item.timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                )
            },
            HorizontalLayout().apply {
                add(
                    Button("Close") { close() },
                    Button("Export PDF", VaadinIcon.FILE.create()) {
                        // Логика экспорта
                    }
                )
//                spacing = true
            }
        )
    }

    private fun createDetailItem(label: String, value: String): HorizontalLayout {
        return HorizontalLayout(
            Span(label).apply { addClassName("detail-label") },
            Span(value).apply { addClassName("detail-value") }
        ).apply {
//            spacing = true
            setAlignItems(FlexComponent.Alignment.BASELINE)
        }
    }
}