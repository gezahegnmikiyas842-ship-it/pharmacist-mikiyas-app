package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.util.Localization
import com.example.ui.viewmodel.NavigationTab
import com.example.ui.viewmodel.PharmacyUiState

@Composable
fun BottomNavBar(
    uiState: PharmacyUiState,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("bottom_navigation_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = NavigationBarDefaults.Elevation
    ) {
        val lang = uiState.currentLanguage

        NavigationBarItem(
            selected = uiState.currentTab == NavigationTab.HOME,
            onClick = { onTabSelected(NavigationTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (uiState.currentTab == NavigationTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = Localization.get("nav_home", lang)
                )
            },
            label = { Text(Localization.get("nav_home", lang)) },
            modifier = Modifier.testTag("nav_item_home")
        )

        NavigationBarItem(
            selected = uiState.currentTab == NavigationTab.CALCULATORS,
            onClick = { onTabSelected(NavigationTab.CALCULATORS) },
            icon = {
                Icon(
                    imageVector = if (uiState.currentTab == NavigationTab.CALCULATORS) Icons.Filled.Calculate else Icons.Outlined.Calculate,
                    contentDescription = Localization.get("nav_tools", lang)
                )
            },
            label = { Text(Localization.get("nav_tools", lang)) },
            modifier = Modifier.testTag("nav_item_tools")
        )

        NavigationBarItem(
            selected = uiState.currentTab == NavigationTab.DRUG_HUB,
            onClick = { onTabSelected(NavigationTab.DRUG_HUB) },
            icon = {
                Icon(
                    imageVector = if (uiState.currentTab == NavigationTab.DRUG_HUB) Icons.Filled.Medication else Icons.Outlined.Medication,
                    contentDescription = Localization.get("nav_drugs", lang)
                )
            },
            label = { Text(Localization.get("nav_drugs", lang)) },
            modifier = Modifier.testTag("nav_item_drugs")
        )

        NavigationBarItem(
            selected = uiState.currentTab == NavigationTab.AI_ASSISTANT,
            onClick = { onTabSelected(NavigationTab.AI_ASSISTANT) },
            icon = {
                Icon(
                    imageVector = if (uiState.currentTab == NavigationTab.AI_ASSISTANT) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                    contentDescription = Localization.get("nav_ai", lang)
                )
            },
            label = { Text(Localization.get("nav_ai", lang)) },
            modifier = Modifier.testTag("nav_item_ai")
        )

        NavigationBarItem(
            selected = uiState.currentTab == NavigationTab.MORE_HUB,
            onClick = { onTabSelected(NavigationTab.MORE_HUB) },
            icon = {
                Icon(
                    imageVector = if (uiState.currentTab == NavigationTab.MORE_HUB) Icons.Filled.Hub else Icons.Outlined.Hub,
                    contentDescription = Localization.get("nav_more", lang)
                )
            },
            label = { Text(Localization.get("nav_more", lang)) },
            modifier = Modifier.testTag("nav_item_more")
        )
    }
}
