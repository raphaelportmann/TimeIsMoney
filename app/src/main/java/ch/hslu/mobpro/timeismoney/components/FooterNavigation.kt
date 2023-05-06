package ch.hslu.mobpro.timeismoney.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun FooterNavigation(
    selectedTab: Tab,
    onTabSelected: (Tab) -> Unit,
) {
    val tabs = listOf(
        Tab.Home,
        Tab.Overview,
        Tab.Settings
    )

    NavigationBar(
    ) {
        tabs.forEach { tab ->
            NavigationBarItem(
                icon = { Icon(tab.icon, contentDescription = tab.title) },
                label = { Text(tab.title) },
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) }
            )
        }
    }
}

open class Tab(
    val title: String,
    val screenName: String,
    val icon: ImageVector
) {
    object Home : Tab(
        "Home",
        "homeScreen",
        Icons.Filled.Home
    )
    object Overview : Tab(
        "Übersicht",
        "loginScreen",
        Icons.Filled.List
    )
    object Settings : Tab(
        "Einstellungen",
        "homeScreen",
        Icons.Filled.Settings
    )
}