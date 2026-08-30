package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.PharmacistBluePrimary
import com.example.ui.util.Localization
import com.example.ui.viewmodel.CalculatorType
import com.example.ui.viewmodel.NavigationTab
import com.example.ui.viewmodel.PharmacyUiState
import com.example.ui.viewmodel.PharmacyViewModel

@Composable
fun ClinicalCalculatorsScreen(
    uiState: PharmacyUiState,
    viewModel: PharmacyViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.currentLanguage
    val historyList by viewModel.calcHistory.collectAsStateWithLifecycle()
    var showHistoryDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("calculators_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // Top Header and History button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = Localization.get("calc_title", lang),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Point-of-care evidence-based medical calculators",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { showHistoryDialog = true },
                    modifier = Modifier.testTag("calc_history_btn")
                ) {
                    Badge(
                        containerColor = PharmacistBluePrimary
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = "Calculation History",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Horizontal Calculator Type Selector Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorType.entries.forEach { calcType ->
                    val isSelected = uiState.activeCalculator == calcType
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setActiveCalculator(calcType) },
                        label = {
                            Text(
                                text = when (calcType) {
                                    CalculatorType.BMI -> "BMI & IBW"
                                    CalculatorType.CRCL -> "CrCl (Cockcroft)"
                                    CalculatorType.EGFR -> "eGFR (CKD-EPI)"
                                    CalculatorType.PEDIATRIC -> "Pediatric Dose"
                                    CalculatorType.INFUSION -> "IV Infusion"
                                    CalculatorType.PREGNANCY -> "Pregnancy Wheel"
                                    CalculatorType.GCS -> "GCS"
                                    CalculatorType.DOSAGE -> "Drug Dosage"
                                },
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PharmacistBluePrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Active Calculator Input Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = uiState.activeCalculator.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PharmacistBluePrimary
                        )
                    )

                    when (uiState.activeCalculator) {
                        CalculatorType.BMI -> {
                            OutlinedTextField(
                                value = uiState.calcInput1,
                                onValueChange = { viewModel.updateCalcInputs(input1 = it) },
                                label = { Text("Weight (kg)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = uiState.calcInput2,
                                onValueChange = { viewModel.updateCalcInputs(input2 = it) },
                                label = { Text("Height (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text("Biological Sex:", style = MaterialTheme.typography.bodyMedium)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = uiState.calcOption1 == "Male",
                                        onClick = { viewModel.updateCalcInputs(option1 = "Male") }
                                    )
                                    Text("Male")
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = uiState.calcOption1 == "Female",
                                        onClick = { viewModel.updateCalcInputs(option1 = "Female") }
                                    )
                                    Text("Female")
                                }
                            }
                        }

                        CalculatorType.CRCL -> {
                            OutlinedTextField(
                                value = uiState.calcInput1,
                                onValueChange = { viewModel.updateCalcInputs(input1 = it) },
                                label = { Text("Age (years)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = uiState.calcInput2,
                                onValueChange = { viewModel.updateCalcInputs(input2 = it) },
                                label = { Text("Actual Body Weight (kg)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = uiState.calcInput3,
                                onValueChange = { viewModel.updateCalcInputs(input3 = it) },
                                label = { Text("Serum Creatinine (mg/dL)") },
                                placeholder = { Text("e.g. 1.2") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text("Sex (×0.85 if Female):", style = MaterialTheme.typography.bodyMedium)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = uiState.calcOption1 == "Male",
                                        onClick = { viewModel.updateCalcInputs(option1 = "Male") }
                                    )
                                    Text("Male")
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = uiState.calcOption1 == "Female",
                                        onClick = { viewModel.updateCalcInputs(option1 = "Female") }
                                    )
                                    Text("Female")
                                }
                            }
                        }

                        CalculatorType.EGFR -> {
                            OutlinedTextField(
                                value = uiState.calcInput1,
                                onValueChange = { viewModel.updateCalcInputs(input1 = it) },
                                label = { Text("Age (years)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = uiState.calcInput2,
                                onValueChange = { viewModel.updateCalcInputs(input2 = it) },
                                label = { Text("Serum Creatinine (mg/dL)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text("Sex:", style = MaterialTheme.typography.bodyMedium)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = uiState.calcOption1 == "Male",
                                        onClick = { viewModel.updateCalcInputs(option1 = "Male") }
                                    )
                                    Text("Male")
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = uiState.calcOption1 == "Female",
                                        onClick = { viewModel.updateCalcInputs(option1 = "Female") }
                                    )
                                    Text("Female")
                                }
                            }
                        }

                        CalculatorType.PEDIATRIC -> {
                            OutlinedTextField(
                                value = uiState.calcInput1,
                                onValueChange = { viewModel.updateCalcInputs(input1 = it) },
                                label = { Text("Child Weight (kg)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = uiState.calcInput2,
                                onValueChange = { viewModel.updateCalcInputs(input2 = it) },
                                label = { Text("Target Dose (mg/kg/day)") },
                                placeholder = { Text("e.g. 45 for Amoxicillin") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = uiState.calcInput3,
                                onValueChange = { viewModel.updateCalcInputs(input3 = it) },
                                label = { Text("Doses Per Day (Frequency, e.g. 2 for BID, 3 for TID)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = uiState.calcInput4,
                                onValueChange = { viewModel.updateCalcInputs(input4 = it) },
                                label = { Text("Liquid Suspension Concentration (mg/mL, optional)") },
                                placeholder = { Text("e.g. 50 (for 250mg/5mL)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        CalculatorType.INFUSION -> {
                            OutlinedTextField(
                                value = uiState.calcInput1,
                                onValueChange = { viewModel.updateCalcInputs(input1 = it) },
                                label = { Text("Total IV Volume (mL)") },
                                placeholder = { Text("e.g. 1000") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = uiState.calcInput2,
                                onValueChange = { viewModel.updateCalcInputs(input2 = it) },
                                label = { Text("Infusion Duration (Hours)") },
                                placeholder = { Text("e.g. 8") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = uiState.calcInput3,
                                onValueChange = { viewModel.updateCalcInputs(input3 = it) },
                                label = { Text("IV Drop Factor (gtts/mL: 10, 15, 20, or 60 microdrip)") },
                                placeholder = { Text("e.g. 20") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        CalculatorType.PREGNANCY -> {
                            OutlinedTextField(
                                value = uiState.calcInput1,
                                onValueChange = { viewModel.updateCalcInputs(input1 = it) },
                                label = { Text("Completed Weeks since LMP") },
                                placeholder = { Text("e.g. 24") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = uiState.calcInput2,
                                onValueChange = { viewModel.updateCalcInputs(input2 = it) },
                                label = { Text("Additional Days (0-6)") },
                                placeholder = { Text("e.g. 3") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        CalculatorType.GCS -> {
                            OutlinedTextField(
                                value = uiState.calcInput1,
                                onValueChange = { viewModel.updateCalcInputs(input1 = it) },
                                label = { Text("Eye Opening (1: None, 2: To Pain, 3: To Speech, 4: Spontaneous)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = uiState.calcInput2,
                                onValueChange = { viewModel.updateCalcInputs(input2 = it) },
                                label = { Text("Verbal Response (1: None, 2: Sounds, 3: Inappropriate, 4: Confused, 5: Oriented)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = uiState.calcInput3,
                                onValueChange = { viewModel.updateCalcInputs(input3 = it) },
                                label = { Text("Motor Response (1: None, 2: Extension, 3: Flexion, 4: Withdrawal, 5: Localizes, 6: Obeys)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        CalculatorType.DOSAGE -> {
                            OutlinedTextField(
                                value = uiState.calcInput1,
                                onValueChange = { viewModel.updateCalcInputs(input1 = it) },
                                label = { Text("Desired Dose (D, mg or mcg)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = uiState.calcInput2,
                                onValueChange = { viewModel.updateCalcInputs(input2 = it) },
                                label = { Text("Dose on Hand (H, mg or mcg)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = uiState.calcInput3,
                                onValueChange = { viewModel.updateCalcInputs(input3 = it) },
                                label = { Text("Quantity on Hand (Q, e.g. 1 tab or 5 mL)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.executeCalculation() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("calc_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = PharmacistBluePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(Localization.get("btn_calculate", lang), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Calculation Result Card (if calculated)
        if (uiState.calcResult.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("calc_result_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen)
                            Text(
                                text = "Clinical Calculation Result",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen
                                )
                            )
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.calcResult,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PharmacistBluePrimary
                                ),
                                modifier = Modifier.padding(14.dp)
                            )
                        }

                        Text(
                            text = uiState.calcInterpretation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.saveCurrentCalculation() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(Localization.get("btn_save_result", lang), fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    val query = "Please explain the clinical implications and pharmacotherapy dosing guidelines for a patient with: ${uiState.activeCalculator.title}, Result: ${uiState.calcResult}. Interpretation: ${uiState.calcInterpretation}"
                                    viewModel.sendAiPrompt(query)
                                    viewModel.setTab(NavigationTab.AI_ASSISTANT)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ask Gemini", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Calculation History Dialog
    if (showHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(Localization.get("calc_history", lang), fontWeight = FontWeight.Bold)
                    if (historyList.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearAllCalcHistory() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            },
            text = {
                if (historyList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(Localization.get("no_history", lang), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(historyList) { item ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = item.resultValue, color = PharmacistBluePrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                        Text(text = item.interpretation, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    IconButton(onClick = { viewModel.deleteCalcHistoryItem(item.id) }) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHistoryDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
