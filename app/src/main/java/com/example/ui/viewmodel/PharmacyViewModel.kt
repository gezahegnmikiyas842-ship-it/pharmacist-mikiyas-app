package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PharmacyDatabase
import com.example.data.model.*
import com.example.data.repository.PharmacyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class NavigationTab {
    HOME,
    CALCULATORS,
    DRUG_HUB,
    AI_ASSISTANT,
    MORE_HUB
}

enum class MoreSubTab {
    ABOUT,
    LEARNING,
    RESEARCH,
    BLOG,
    ADMIN,
    CONTACT
}

enum class CalculatorType(val title: String) {
    BMI("BMI & IBW / BSA"),
    CRCL("Creatinine Clearance (Cockcroft-Gault)"),
    EGFR("eGFR (CKD-EPI 2021)"),
    PEDIATRIC("Pediatric Dose"),
    INFUSION("IV Infusion & Drop Rate"),
    PREGNANCY("Pregnancy Wheel & EDD"),
    GCS("Glasgow Coma Scale (GCS)"),
    DOSAGE("Drug Dosage & Unit Converter")
}

data class PharmacyUiState(
    val currentTab: NavigationTab = NavigationTab.HOME,
    val moreSubTab: MoreSubTab = MoreSubTab.ABOUT,
    val currentLanguage: AppLanguage = AppLanguage.EN,
    val isDarkMode: Boolean = false,
    val isAdminMode: Boolean = false,
    
    // Drug Hub
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val selectedDrugForDetail: DrugItem? = null,
    val selectedDrugsForInteraction: List<String> = emptyList(),
    val interactionResults: List<DrugInteractionCheckResult> = emptyList(),
    val isCheckingInteractions: Boolean = false,

    // Calculators
    val activeCalculator: CalculatorType = CalculatorType.CRCL,
    val calcInput1: String = "",
    val calcInput2: String = "",
    val calcInput3: String = "",
    val calcInput4: String = "",
    val calcOption1: String = "Male",
    val calcOption2: String = "Adult",
    val calcResult: String = "",
    val calcInterpretation: String = "",

    // AI Assistant
    val aiMessages: List<ChatMessage> = listOf(
        ChatMessage(
            id = "ai-intro",
            isUser = false,
            message = "Hello! I am Pharmacist Mikiyas AI Clinical Assistant. You can ask me any clinical pharmacotherapy questions, renal/hepatic dosing adjustments, drug interactions, disease guidelines, or patient counseling pearls."
        )
    ),
    val aiInputText: String = "",
    val isAiLoading: Boolean = false,

    // Learning
    val activeLearningCategory: String = "Pharmacy",
    val currentQuizIndex: Int = 0,
    val selectedQuizOption: Int? = null,
    val isQuizAnswerSubmitted: Boolean = false,
    val quizScore: Int = 0,
    val quizFinished: Boolean = false,
    
    val currentFlashcardIndex: Int = 0,
    val isFlashcardFlipped: Boolean = false,

    // Blog
    val selectedArticleForDetail: ArticleItem? = null,
    val blogSearchQuery: String = "",

    // Contact
    val contactName: String = "",
    val contactEmail: String = "",
    val contactSubject: String = "",
    val contactMessage: String = "",
    val contactSubmitted: Boolean = false
)

class PharmacyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PharmacyRepository
    private val _uiState = MutableStateFlow(PharmacyUiState())
    val uiState: StateFlow<PharmacyUiState> = _uiState.asStateFlow()

    val allDrugs: StateFlow<List<DrugItem>>
    val savedDrugs: StateFlow<List<DrugItem>>
    val calcHistory: StateFlow<List<CalcHistoryEntity>>
    val allArticles: StateFlow<List<ArticleItem>>
    val allMessages: StateFlow<List<ContactMessageEntity>>

    init {
        val db = PharmacyDatabase.getDatabase(application)
        repository = PharmacyRepository(db.pharmacyDao())

        allDrugs = repository.allDrugs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        savedDrugs = repository.savedDrugs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        calcHistory = repository.calcHistory.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allArticles = repository.allArticles.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        allMessages = repository.allMessages.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun setTab(tab: NavigationTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun setMoreSubTab(subTab: MoreSubTab) {
        _uiState.update { it.copy(moreSubTab = subTab) }
    }

    fun setLanguage(lang: AppLanguage) {
        _uiState.update { it.copy(currentLanguage = lang) }
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun toggleAdminMode() {
        _uiState.update { it.copy(isAdminMode = !it.isAdminMode) }
    }

    // Drug Hub Actions
    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setSelectedCategory(cat: String) {
        _uiState.update { it.copy(selectedCategory = cat) }
    }

    fun selectDrugForDetail(drug: DrugItem?) {
        _uiState.update { it.copy(selectedDrugForDetail = drug) }
    }

    fun toggleSaveDrug(drug: DrugItem) {
        viewModelScope.launch {
            repository.toggleSaveDrug(drug)
        }
    }

    fun toggleDrugSelectionForInteraction(drugName: String) {
        val current = _uiState.value.selectedDrugsForInteraction.toMutableList()
        if (current.contains(drugName)) {
            current.remove(drugName)
        } else {
            current.add(drugName)
        }
        _uiState.update { it.copy(selectedDrugsForInteraction = current, interactionResults = emptyList()) }
    }

    fun runInteractionCheck() {
        val selected = _uiState.value.selectedDrugsForInteraction
        if (selected.size < 2) return
        val results = repository.checkInteractions(selected)
        _uiState.update { it.copy(interactionResults = results, isCheckingInteractions = true) }
    }

    fun clearInteractionSelections() {
        _uiState.update { it.copy(selectedDrugsForInteraction = emptyList(), interactionResults = emptyList(), isCheckingInteractions = false) }
    }

    // Calculators
    fun setActiveCalculator(calc: CalculatorType) {
        _uiState.update {
            it.copy(
                activeCalculator = calc,
                calcInput1 = "",
                calcInput2 = "",
                calcInput3 = "",
                calcInput4 = "",
                calcResult = "",
                calcInterpretation = ""
            )
        }
    }

    fun updateCalcInputs(input1: String? = null, input2: String? = null, input3: String? = null, input4: String? = null, option1: String? = null, option2: String? = null) {
        _uiState.update {
            it.copy(
                calcInput1 = input1 ?: it.calcInput1,
                calcInput2 = input2 ?: it.calcInput2,
                calcInput3 = input3 ?: it.calcInput3,
                calcInput4 = input4 ?: it.calcInput4,
                calcOption1 = option1 ?: it.calcOption1,
                calcOption2 = option2 ?: it.calcOption2
            )
        }
    }

    fun executeCalculation() {
        val state = _uiState.value
        when (state.activeCalculator) {
            CalculatorType.BMI -> {
                val weight = state.calcInput1.toDoubleOrNull() ?: 0.0 // kg
                val heightCm = state.calcInput2.toDoubleOrNull() ?: 0.0 // cm
                val isMale = state.calcOption1.equals("Male", ignoreCase = true)
                if (weight > 0 && heightCm > 0) {
                    val heightM = heightCm / 100.0
                    val bmi = weight / (heightM * heightM)
                    // Ideal Body Weight (Devine Formula)
                    val heightInches = heightCm / 2.54
                    val inchesOver5ft = maxOf(0.0, heightInches - 60.0)
                    val ibw = if (isMale) 50.0 + (2.3 * inchesOver5ft) else 45.5 + (2.3 * inchesOver5ft)
                    // Mosteller BSA
                    val bsa = Math.sqrt((heightCm * weight) / 3600.0)

                    val category = when {
                        bmi < 18.5 -> "Underweight (Clinical Review indicated)"
                        bmi in 18.5..24.9 -> "Normal weight"
                        bmi in 25.0..29.9 -> "Overweight"
                        bmi in 30.0..34.9 -> "Obesity Class I"
                        bmi in 35.0..39.9 -> "Obesity Class II"
                        else -> "Obesity Class III (Severe/Morbid)"
                    }
                    val resultStr = String.format("BMI: %.1f kg/m² | IBW: %.1f kg | BSA: %.2f m²", bmi, ibw, bsa)
                    val interpStr = "Classification: $category. Actual weight is ${String.format("%.1f%%", (weight / ibw) * 100)} of IBW."
                    _uiState.update { it.copy(calcResult = resultStr, calcInterpretation = interpStr) }
                }
            }
            CalculatorType.CRCL -> {
                val age = state.calcInput1.toDoubleOrNull() ?: 0.0
                val weight = state.calcInput2.toDoubleOrNull() ?: 0.0 // kg
                val scr = state.calcInput3.toDoubleOrNull() ?: 0.0 // mg/dL
                val isFemale = state.calcOption1.equals("Female", ignoreCase = true)
                if (age > 0 && weight > 0 && scr > 0) {
                    var crcl = ((140.0 - age) * weight) / (72.0 * scr)
                    if (isFemale) crcl *= 0.85

                    val interp = when {
                        crcl >= 90 -> "Normal to High Renal Function. Standard dosing appropriate."
                        crcl in 60.0..89.9 -> "Mildly Decreased Renal Function. Review renally eliminated drugs."
                        crcl in 30.0..59.9 -> "Moderate Renal Impairment (CKD Stage 3). Dose reduction indicated for Vancomycin, Enoxaparin, Ciprofloxacin, DOACs."
                        crcl in 15.0..29.9 -> "Severe Renal Impairment (CKD Stage 4). Avoid Metformin, NSAIDs; severe dose reduction required."
                        else -> "Kidney Failure / ESRD (CKD Stage 5). Dialysis dosing protocols required."
                    }
                    _uiState.update {
                        it.copy(
                            calcResult = String.format("CrCl: %.1f mL/min", crcl),
                            calcInterpretation = interp
                        )
                    }
                }
            }
            CalculatorType.EGFR -> {
                val age = state.calcInput1.toDoubleOrNull() ?: 0.0
                val scr = state.calcInput2.toDoubleOrNull() ?: 0.0 // mg/dL
                val isFemale = state.calcOption1.equals("Female", ignoreCase = true)
                if (age > 0 && scr > 0) {
                    // CKD-EPI 2021 Refit equation (race-neutral)
                    val kappa = if (isFemale) 0.7 else 0.9
                    val alpha = if (isFemale) -0.241 else -0.302
                    val minRatio = Math.min(scr / kappa, 1.0)
                    val maxRatio = Math.max(scr / kappa, 1.0)
                    val genderMultiplier = if (isFemale) 1.012 else 1.0

                    val egfr = 142.0 * Math.pow(minRatio, alpha) * Math.pow(maxRatio, -1.200) * Math.pow(0.9938, age) * genderMultiplier
                    val stage = when {
                        egfr >= 90 -> "G1: Normal or high kidney function"
                        egfr in 60.0..89.9 -> "G2: Mildly decreased"
                        egfr in 45.0..59.9 -> "G3a: Mildly to moderately decreased"
                        egfr in 30.0..44.9 -> "G3b: Moderately to severely decreased"
                        egfr in 15.0..29.9 -> "G4: Severely decreased"
                        else -> "G5: Kidney failure (ESRD)"
                    }
                    _uiState.update {
                        it.copy(
                            calcResult = String.format("eGFR: %.1f mL/min/1.73 m²", egfr),
                            calcInterpretation = "CKD Stage: $stage (2021 CKD-EPI Refit)"
                        )
                    }
                }
            }
            CalculatorType.PEDIATRIC -> {
                val weightKg = state.calcInput1.toDoubleOrNull() ?: 0.0
                val dosePerKg = state.calcInput2.toDoubleOrNull() ?: 0.0 // mg/kg/day
                val dosesPerDay = state.calcInput3.toIntOrNull() ?: 2
                val concentrationMgPerMl = state.calcInput4.toDoubleOrNull() ?: 0.0 // mg/mL
                if (weightKg > 0 && dosePerKg > 0) {
                    val totalDailyDoseMg = weightKg * dosePerKg
                    val singleDoseMg = totalDailyDoseMg / dosesPerDay
                    val mlPerDose = if (concentrationMgPerMl > 0) singleDoseMg / concentrationMgPerMl else 0.0

                    val res = String.format("Total: %.1f mg/day | Single Dose: %.1f mg", totalDailyDoseMg, singleDoseMg)
                    val interp = if (concentrationMgPerMl > 0) {
                        String.format("Give %.1f mL (%d times daily). Always verify with standardized oral syringe.", mlPerDose, dosesPerDay)
                    } else {
                        "Administer in $dosesPerDay divided doses."
                    }
                    _uiState.update { it.copy(calcResult = res, calcInterpretation = interp) }
                }
            }
            CalculatorType.INFUSION -> {
                val volumeMl = state.calcInput1.toDoubleOrNull() ?: 0.0 // mL
                val durationHours = state.calcInput2.toDoubleOrNull() ?: 0.0 // hrs
                val dropFactor = state.calcInput3.toDoubleOrNull() ?: 20.0 // gtts/mL (e.g. 10, 15, 20, 60)
                if (volumeMl > 0 && durationHours > 0) {
                    val mlPerHour = volumeMl / durationHours
                    val totalMinutes = durationHours * 60.0
                    val dropsPerMin = (volumeMl * dropFactor) / totalMinutes

                    _uiState.update {
                        it.copy(
                            calcResult = String.format("Flow Rate: %.1f mL/hr | %.0f gtts/min", mlPerHour, dropsPerMin),
                            calcInterpretation = "Using drop factor $dropFactor gtts/mL. Infuse $volumeMl mL over $durationHours hr(s)."
                        )
                    }
                }
            }
            CalculatorType.PREGNANCY -> {
                val lmpWeeks = state.calcInput1.toIntOrNull() ?: 0
                val lmpDays = state.calcInput2.toIntOrNull() ?: 0
                val totalDays = (lmpWeeks * 7) + lmpDays
                if (totalDays in 1..294) {
                    val currentWeek = totalDays / 7
                    val currentDay = totalDays % 7
                    val trimester = when {
                        currentWeek < 13 -> "First Trimester (0-12w)"
                        currentWeek < 27 -> "Second Trimester (13-26w)"
                        else -> "Third Trimester (27-40w)"
                    }
                    val daysRemaining = 280 - totalDays
                    _uiState.update {
                        it.copy(
                            calcResult = "GA: $currentWeek weeks, $currentDay days",
                            calcInterpretation = "$trimester. ~$daysRemaining days until Estimated Due Date (EDD, Naegele's 40 weeks)."
                        )
                    }
                }
            }
            CalculatorType.GCS -> {
                val eye = state.calcInput1.toIntOrNull() ?: 4 // 1-4
                val verbal = state.calcInput2.toIntOrNull() ?: 5 // 1-5
                val motor = state.calcInput3.toIntOrNull() ?: 6 // 1-6
                val totalGcs = eye + verbal + motor
                val severity = when {
                    totalGcs in 13..15 -> "Mild Head Injury / Alert"
                    totalGcs in 9..12 -> "Moderate Brain Injury"
                    else -> "Severe Brain Injury (Coma / High risk of airway compromise, GCS ≤ 8)"
                }
                _uiState.update {
                    it.copy(
                        calcResult = "Glasgow Coma Scale: $totalGcs / 15",
                        calcInterpretation = "Sub-scores: E${eye}V${verbal}M${motor}. Severity: $severity."
                    )
                }
            }
            CalculatorType.DOSAGE -> {
                val desiredDose = state.calcInput1.toDoubleOrNull() ?: 0.0 // mg
                val doseOnHand = state.calcInput2.toDoubleOrNull() ?: 0.0 // mg per tab or per mL
                val quantityOnHand = state.calcInput3.toDoubleOrNull() ?: 1.0 // tab or mL
                if (desiredDose > 0 && doseOnHand > 0) {
                    val amountToAdminister = (desiredDose / doseOnHand) * quantityOnHand
                    _uiState.update {
                        it.copy(
                            calcResult = String.format("Administer: %.2f units (tablets/mL)", amountToAdminister),
                            calcInterpretation = "Formula: (Desired / On-Hand) × Vehicle Quantity. Verify calculation independently before drug dispensing."
                        )
                    }
                }
            }
        }
    }

    fun saveCurrentCalculation() {
        val state = _uiState.value
        if (state.calcResult.isBlank()) return
        viewModelScope.launch {
            repository.saveCalcResult(
                calcType = state.activeCalculator.name,
                title = state.activeCalculator.title,
                inputSummary = "In1: ${state.calcInput1}, In2: ${state.calcInput2}",
                resultValue = state.calcResult,
                interpretation = state.calcInterpretation
            )
        }
    }

    fun deleteCalcHistoryItem(id: Int) {
        viewModelScope.launch {
            repository.deleteCalcHistory(id)
        }
    }

    fun clearAllCalcHistory() {
        viewModelScope.launch {
            repository.clearCalcHistory()
        }
    }

    // AI Assistant
    fun setAiInputText(text: String) {
        _uiState.update { it.copy(aiInputText = text) }
    }

    fun sendAiPrompt(promptText: String? = null) {
        val prompt = promptText ?: _uiState.value.aiInputText
        if (prompt.isBlank() || _uiState.value.isAiLoading) return

        val userMsg = ChatMessage(id = UUID.randomUUID().toString(), isUser = true, message = prompt)
        val placeholderAiMsg = ChatMessage(id = UUID.randomUUID().toString(), isUser = false, message = "", isLoading = true)

        val updatedMessages = _uiState.value.aiMessages + userMsg + placeholderAiMsg
        _uiState.update {
            it.copy(
                aiMessages = updatedMessages,
                aiInputText = "",
                isAiLoading = true
            )
        }

        viewModelScope.launch {
            val history = _uiState.value.aiMessages
                .filter { !it.isLoading }
                .map { Pair(it.message, it.isUser) }

            val response = repository.askGemini(prompt, history)

            _uiState.update { state ->
                val finalMessages = state.aiMessages.map { msg ->
                    if (msg.id == placeholderAiMsg.id) {
                        msg.copy(message = response, isLoading = false)
                    } else msg
                }
                state.copy(aiMessages = finalMessages, isAiLoading = false)
            }
        }
    }

    fun clearAiChat() {
        _uiState.update {
            it.copy(
                aiMessages = listOf(
                    ChatMessage(
                        id = "ai-intro",
                        isUser = false,
                        message = "Chat history cleared. How can I assist you with clinical pharmacology or pharmacotherapy calculations today?"
                    )
                )
            )
        }
    }

    // Learning Center
    fun setLearningCategory(cat: String) {
        _uiState.update {
            it.copy(
                activeLearningCategory = cat,
                currentQuizIndex = 0,
                selectedQuizOption = null,
                isQuizAnswerSubmitted = false,
                quizScore = 0,
                quizFinished = false
            )
        }
    }

    fun selectQuizOption(index: Int) {
        if (!_uiState.value.isQuizAnswerSubmitted) {
            _uiState.update { it.copy(selectedQuizOption = index) }
        }
    }

    fun submitQuizAnswer(correctIndex: Int) {
        val selected = _uiState.value.selectedQuizOption ?: return
        val isCorrect = selected == correctIndex
        _uiState.update {
            it.copy(
                isQuizAnswerSubmitted = true,
                quizScore = if (isCorrect) it.quizScore + 1 else it.quizScore
            )
        }
    }

    fun nextQuizQuestion(totalQuestions: Int) {
        val nextIdx = _uiState.value.currentQuizIndex + 1
        if (nextIdx < totalQuestions) {
            _uiState.update {
                it.copy(
                    currentQuizIndex = nextIdx,
                    selectedQuizOption = null,
                    isQuizAnswerSubmitted = false
                )
            }
        } else {
            _uiState.update { it.copy(quizFinished = true) }
        }
    }

    fun restartQuiz() {
        _uiState.update {
            it.copy(
                currentQuizIndex = 0,
                selectedQuizOption = null,
                isQuizAnswerSubmitted = false,
                quizScore = 0,
                quizFinished = false
            )
        }
    }

    fun flipFlashcard() {
        _uiState.update { it.copy(isFlashcardFlipped = !it.isFlashcardFlipped) }
    }

    fun nextFlashcard(totalCards: Int) {
        val nextIdx = (_uiState.value.currentFlashcardIndex + 1) % totalCards
        _uiState.update { it.copy(currentFlashcardIndex = nextIdx, isFlashcardFlipped = false) }
    }

    fun prevFlashcard(totalCards: Int) {
        val prevIdx = if (_uiState.value.currentFlashcardIndex - 1 < 0) totalCards - 1 else _uiState.value.currentFlashcardIndex - 1
        _uiState.update { it.copy(currentFlashcardIndex = prevIdx, isFlashcardFlipped = false) }
    }

    // Blog
    fun selectArticleForDetail(article: ArticleItem?) {
        _uiState.update { it.copy(selectedArticleForDetail = article) }
    }

    fun toggleBookmarkArticle(article: ArticleItem) {
        viewModelScope.launch {
            repository.toggleBookmarkArticle(article)
        }
    }

    // Contact Form
    fun updateContactForm(name: String? = null, email: String? = null, subject: String? = null, message: String? = null) {
        _uiState.update {
            it.copy(
                contactName = name ?: it.contactName,
                contactEmail = email ?: it.contactEmail,
                contactSubject = subject ?: it.contactSubject,
                contactMessage = message ?: it.contactMessage
            )
        }
    }

    fun submitContactMessage() {
        val state = _uiState.value
        if (state.contactName.isBlank() || state.contactEmail.isBlank() || state.contactMessage.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(
                name = state.contactName,
                email = state.contactEmail,
                subject = state.contactSubject.ifBlank { "General Inquiry" },
                message = state.contactMessage
            )
            _uiState.update {
                it.copy(
                    contactName = "",
                    contactEmail = "",
                    contactSubject = "",
                    contactMessage = "",
                    contactSubmitted = true
                )
            }
        }
    }

    fun resetContactSubmitted() {
        _uiState.update { it.copy(contactSubmitted = false) }
    }
}
