package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.AppLanguage
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.PharmacistBluePrimary
import com.example.ui.util.AppImageUrls
import com.example.ui.util.Localization
import com.example.ui.viewmodel.MoreSubTab
import com.example.ui.viewmodel.NavigationTab
import com.example.ui.viewmodel.PharmacyUiState
import com.example.ui.viewmodel.PharmacyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarHeader(
    uiState: PharmacyUiState,
    viewModel: PharmacyViewModel,
    modifier: Modifier = Modifier
) {
    var showLangMenu by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier
            .fillMaxWidth()
            .testTag("top_app_bar"),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFD700))
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(PharmacistBluePrimary)
                        .clickable {
                            viewModel.setMoreSubTab(MoreSubTab.ABOUT)
                            viewModel.setTab(NavigationTab.MORE_HUB)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = AppImageUrls.AVATAR_PROFILE_PHOTO_URL,
                        contentDescription = "Mikiyas Gezahegn - Clinical Pharmacist",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = painterResource(id = R.drawable.mikiyas_clinical),
                        error = painterResource(id = R.drawable.mikiyas_clinical)
                    )
                }

                Column {
                    Text(
                        text = Localization.get("app_title", uiState.currentLanguage),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = Localization.get("tagline", uiState.currentLanguage),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = EmeraldGreen,
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1
                    )
                }
            }
        },
        actions = {
            // Language selector button
            Box {
                IconButton(
                    onClick = { showLangMenu = true },
                    modifier = Modifier.testTag("lang_menu_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Translate,
                            contentDescription = "Change Language",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = uiState.currentLanguage.name,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                DropdownMenu(
                    expanded = showLangMenu,
                    onDismissRequest = { showLangMenu = false }
                ) {
                    AppLanguage.entries.forEach { lang ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = lang.nativeName,
                                        fontWeight = if (uiState.currentLanguage == lang) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (uiState.currentLanguage == lang) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = PharmacistBluePrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                viewModel.setLanguage(lang)
                                showLangMenu = false
                            }
                        )
                    }
                }
            }

            // Dark Mode Toggle
            IconButton(
                onClick = { viewModel.toggleDarkMode() },
                modifier = Modifier.testTag("theme_toggle_button")
            ) {
                Icon(
                    imageVector = if (uiState.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Dark Mode",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Admin Mode indicator
            IconButton(
                onClick = { viewModel.toggleAdminMode() },
                modifier = Modifier.testTag("admin_toggle_button")
            ) {
                Icon(
                    imageVector = if (uiState.isAdminMode) Icons.Default.AdminPanelSettings else Icons.Outlined.Person,
                    contentDescription = "Toggle Admin Mode",
                    tint = if (uiState.isAdminMode) EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}
