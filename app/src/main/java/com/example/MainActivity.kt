package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BottomNavBar
import com.example.ui.components.TopAppBarHeader
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.NavigationTab
import com.example.ui.viewmodel.PharmacyViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: PharmacyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = uiState.isDarkMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBarHeader(
                            uiState = uiState,
                            viewModel = viewModel
                        )
                    },
                    bottomBar = {
                        BottomNavBar(
                            uiState = uiState,
                            onTabSelected = { tab -> viewModel.setTab(tab) }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Crossfade(targetState = uiState.currentTab, label = "tab_transition") { tab ->
                            when (tab) {
                                NavigationTab.HOME -> HomeScreen(uiState = uiState, viewModel = viewModel)
                                NavigationTab.CALCULATORS -> ClinicalCalculatorsScreen(uiState = uiState, viewModel = viewModel)
                                NavigationTab.DRUG_HUB -> DrugHubScreen(uiState = uiState, viewModel = viewModel)
                                NavigationTab.AI_ASSISTANT -> AiAssistantScreen(uiState = uiState, viewModel = viewModel)
                                NavigationTab.MORE_HUB -> MoreHubScreen(uiState = uiState, viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
