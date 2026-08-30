package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DrugItem
import com.example.data.model.InteractionSeverity
import com.example.ui.theme.*
import com.example.ui.util.Localization
import com.example.ui.viewmodel.PharmacyUiState
import com.example.ui.viewmodel.PharmacyViewModel

@Composable
fun DrugHubScreen(
    uiState: PharmacyUiState,
    viewModel: PharmacyViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.currentLanguage
    val allDrugsList by viewModel.allDrugs.collectAsStateWithLifecycle()
    var selectedHubTab by remember { mutableIntStateOf(0) } // 0: Database, 1: Interaction Checker

    // Filter drugs
    val filteredDrugs = remember(allDrugsList, uiState.searchQuery, uiState.selectedCategory) {
        allDrugsList.filter { drug ->
            val matchesQuery = uiState.searchQuery.isBlank() ||
                    drug.genericName.contains(uiState.searchQuery, ignoreCase = true) ||
                    drug.brandNames.contains(uiState.searchQuery, ignoreCase = true)
            val matchesCategory = uiState.selectedCategory == "All" ||
                    (uiState.selectedCategory == "Saved" && drug.isSaved) ||
                    drug.category.contains(uiState.selectedCategory, ignoreCase = true)
            matchesQuery && matchesCategory
        }
    }

    val categories = listOf("All", "Saved", "Cardiovascular", "Endocrine", "Infectious Disease", "Hematology", "Gastrointestinal")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("drug_hub_screen")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Primary Tab Switcher
        TabRow(
            selectedTabIndex = selectedHubTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .testTag("drug_hub_tab_row")
        ) {
            Tab(
                selected = selectedHubTab == 0,
                onClick = { selectedHubTab = 0 },
                text = { Text("Drug Database", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Medication, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedHubTab == 1,
                onClick = { selectedHubTab = 1 },
                text = { Text("Interaction Checker", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.CompareArrows, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedHubTab == 0) {
            // Drug Database View
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text(Localization.get("drug_search_hint", lang)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("drug_search_field"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = uiState.selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedCategory(cat) },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PharmacistBluePrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                if (filteredDrugs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No matching drugs found in monograph database.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(filteredDrugs) { drug ->
                        DrugListItemCard(
                            drug = drug,
                            onClick = { viewModel.selectDrugForDetail(drug) },
                            onToggleSave = { viewModel.toggleSaveDrug(drug) }
                        )
                    }
                }
            }
        } else {
            // Drug Interaction Checker View
            DrugInteractionCheckerContent(uiState, viewModel)
        }
    }

    // Drug Detail Bottom Sheet/Dialog
    if (uiState.selectedDrugForDetail != null) {
        val drug = uiState.selectedDrugForDetail!!
        AlertDialog(
            onDismissRequest = { viewModel.selectDrugForDetail(null) },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = drug.genericName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PharmacistBluePrimary
                            )
                        )
                        Text(
                            text = "Brands: ${drug.brandNames}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { viewModel.toggleSaveDrug(drug) }) {
                        Icon(
                            imageVector = if (drug.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Save Drug",
                            tint = if (drug.isSaved) PharmacistBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = EmeraldGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = drug.category,
                                    color = EmeraldGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                color = when (drug.pregnancyCategory) {
                                    "A", "B" -> EmeraldGreen.copy(alpha = 0.15f)
                                    "C" -> SeverityModerate.copy(alpha = 0.15f)
                                    else -> SeverityCritical.copy(alpha = 0.15f)
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Pregnancy Cat: ${drug.pregnancyCategory}",
                                    color = when (drug.pregnancyCategory) {
                                        "A", "B" -> EmeraldGreen
                                        "C" -> SeverityModerate
                                        else -> SeverityCritical
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    item {
                        DrugMonographSection(title = "Mechanism of Action (MOA)", content = drug.moa)
                    }
                    item {
                        DrugMonographSection(title = "Standard Dosing & Administration", content = drug.standardDosage)
                    }
                    item {
                        DrugMonographSection(title = "Contraindications", content = drug.contraindications, isAlert = true)
                    }
                    item {
                        DrugMonographSection(title = "Adverse Effects & Safety", content = drug.sideEffects)
                    }
                    item {
                        DrugMonographSection(title = "Lactation Safety", content = drug.lactationSafety)
                    }
                    item {
                        DrugMonographSection(title = "Key Interactions", content = drug.knownInteractions)
                    }
                    item {
                        DrugMonographSection(title = "Storage Conditions", content = drug.storage)
                    }
                    item {
                        DrugMonographSection(title = "Patient Counseling Pearls", content = drug.counselingPoints, isHighlight = true)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.selectDrugForDetail(null) },
                    colors = ButtonDefaults.buttonColors(containerColor = PharmacistBluePrimary)
                ) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun DrugListItemCard(
    drug: DrugItem,
    onClick: () -> Unit,
    onToggleSave: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("drug_item_${drug.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = drug.genericName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = drug.brandNames,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onToggleSave) {
                    Icon(
                        imageVector = if (drug.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save Drug",
                        tint = if (drug.isSaved) PharmacistBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = PharmacistBluePrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = drug.category,
                        color = PharmacistBluePrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    color = when (drug.pregnancyCategory) {
                        "A", "B" -> EmeraldGreen.copy(alpha = 0.12f)
                        "C" -> SeverityModerate.copy(alpha = 0.12f)
                        else -> SeverityCritical.copy(alpha = 0.12f)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Preg Cat: ${drug.pregnancyCategory}",
                        color = when (drug.pregnancyCategory) {
                            "A", "B" -> EmeraldGreen
                            "C" -> SeverityModerate
                            else -> SeverityCritical
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DrugMonographSection(
    title: String,
    content: String,
    isAlert: Boolean = false,
    isHighlight: Boolean = false
) {
    Surface(
        color = when {
            isAlert -> SeverityCritical.copy(alpha = 0.08f)
            isHighlight -> EmeraldGreen.copy(alpha = 0.08f)
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isAlert -> SeverityCritical
                        isHighlight -> EmeraldGreen
                        else -> PharmacistBluePrimary
                    }
                )
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun DrugInteractionCheckerContent(
    uiState: PharmacyUiState,
    viewModel: PharmacyViewModel
) {
    val commonDrugs = listOf(
        "Warfarin", "Aspirin", "Amiodarone", "Metformin", "Iodinated Contrast",
        "Lisinopril", "Spironolactone", "Atorvastatin", "Clarithromycin",
        "Omeprazole", "Clopidogrel", "Vancomycin", "Piperacillin/Tazobactam",
        "Levothyroxine", "Calcium Carbonate"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("interaction_checker_content"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Multi-Drug Interaction Screening",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PharmacistBluePrimary
                        )
                    )
                    Text(
                        text = "Select 2 or more medicines below to analyze potential pharmacokinetic & pharmacodynamic interactions:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Drug Selection Chips Flow
                    OptInMultiChipSelector(
                        availableDrugs = commonDrugs,
                        selectedDrugs = uiState.selectedDrugsForInteraction,
                        onToggle = { viewModel.toggleDrugSelectionForInteraction(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.runInteractionCheck() },
                            enabled = uiState.selectedDrugsForInteraction.size >= 2,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("run_interaction_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = PharmacistBluePrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.CompareArrows, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Check Interactions (${uiState.selectedDrugsForInteraction.size})", fontWeight = FontWeight.Bold)
                        }

                        if (uiState.selectedDrugsForInteraction.isNotEmpty()) {
                            OutlinedButton(
                                onClick = { viewModel.clearInteractionSelections() },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Clear")
                            }
                        }
                    }
                }
            }
        }

        // Interaction Results
        if (uiState.isCheckingInteractions) {
            if (uiState.interactionResults.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldGreen.copy(alpha = 0.1f)),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen)
                            Text(
                                text = "No major documented interactions detected between selected drugs.",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = EmeraldGreen
                            )
                        }
                    }
                }
            } else {
                items(uiState.interactionResults) { result ->
                    InteractionResultCard(result)
                }
            }
        }
    }
}

@Composable
fun OptInMultiChipSelector(
    availableDrugs: List<String>,
    selectedDrugs: List<String>,
    onToggle: (String) -> Unit
) {
    // Flow/Grid representation of chips
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        val chunked = availableDrugs.chunked(3)
        chunked.forEach { rowDrugs ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                rowDrugs.forEach { drug ->
                    val isSelected = selectedDrugs.contains(drug)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onToggle(drug) },
                        label = { Text(drug, fontSize = 11.sp) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PharmacistBluePrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remainder if row is not full
                for (i in 0 until (3 - rowDrugs.size)) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun InteractionResultCard(result: com.example.data.model.DrugInteractionCheckResult) {
    val severityColor = when (result.severity) {
        InteractionSeverity.CRITICAL -> SeverityCritical
        InteractionSeverity.MAJOR -> SeverityMajor
        InteractionSeverity.MODERATE -> SeverityModerate
        InteractionSeverity.MINOR -> SeverityMinor
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${result.drugA} + ${result.drugB}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Surface(
                    color = severityColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = result.severity.label,
                        color = severityColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = "Mechanism:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = PharmacistBluePrimary)
            )
            Text(
                text = result.mechanism,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Clinical Effect:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = severityColor)
            )
            Text(
                text = result.clinicalEffect,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Surface(
                color = EmeraldGreen.copy(alpha = 0.08f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Pharmacist Action & Management:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldGreen)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = result.management,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
