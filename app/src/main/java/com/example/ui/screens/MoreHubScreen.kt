package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.ArticleItem
import com.example.data.repository.PharmacyRepository
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.PharmacistBluePrimary
import com.example.ui.util.AppImageUrls
import com.example.ui.util.Localization
import com.example.ui.viewmodel.MoreSubTab
import com.example.ui.viewmodel.PharmacyUiState
import com.example.ui.viewmodel.PharmacyViewModel

// ==============================================================================
// CUSTOM PHOTO WEB URL CONFIGURATION
// You can customize or paste direct image URLs here or in AppImageUrls.kt:
// ==============================================================================
var AVATAR_PROFILE_PHOTO_URL: String
    get() = AppImageUrls.AVATAR_PROFILE_PHOTO_URL
    set(value) { AppImageUrls.AVATAR_PROFILE_PHOTO_URL = value }

var CLINICAL_PRACTICE_PHOTO_URL: String
    get() = AppImageUrls.CLINICAL_PRACTICE_PHOTO_URL
    set(value) { AppImageUrls.CLINICAL_PRACTICE_PHOTO_URL = value }

var GRADUATION_PHOTO_URL: String
    get() = AppImageUrls.GRADUATION_PHOTO_URL
    set(value) { AppImageUrls.GRADUATION_PHOTO_URL = value }

var PORTRAIT_PHOTO_URL: String
    get() = AppImageUrls.PORTRAIT_PHOTO_URL
    set(value) { AppImageUrls.PORTRAIT_PHOTO_URL = value }

var DIGITAL_HEALTH_PHOTO_URL: String
    get() = AppImageUrls.DIGITAL_HEALTH_PHOTO_URL
    set(value) { AppImageUrls.DIGITAL_HEALTH_PHOTO_URL = value }

@Composable
fun MoreHubScreen(
    uiState: PharmacyUiState,
    viewModel: PharmacyViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.currentLanguage
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("more_hub_screen")
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Sub-Tab Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MoreSubTab.entries.forEach { subTab ->
                val isSelected = uiState.moreSubTab == subTab
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setMoreSubTab(subTab) },
                    label = {
                        Text(
                            text = when (subTab) {
                                MoreSubTab.ABOUT -> "About Mikiyas"
                                MoreSubTab.LEARNING -> "Learning Center"
                                MoreSubTab.RESEARCH -> "Research"
                                MoreSubTab.BLOG -> "Clinical Blog"
                                MoreSubTab.ADMIN -> "Admin Dashboard"
                                MoreSubTab.CONTACT -> "Contact"
                            },
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PharmacistBluePrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Content Area
        Box(modifier = Modifier.weight(1f)) {
            when (uiState.moreSubTab) {
                MoreSubTab.ABOUT -> AboutSectionContent(uiState)
                MoreSubTab.LEARNING -> LearningSectionContent(uiState, viewModel)
                MoreSubTab.RESEARCH -> ResearchSectionContent()
                MoreSubTab.BLOG -> BlogSectionContent(uiState, viewModel)
                MoreSubTab.ADMIN -> AdminDashboardSectionContent(uiState, viewModel)
                MoreSubTab.CONTACT -> ContactSectionContent(uiState, viewModel)
            }
        }
    }
}

@Composable
fun AboutSectionContent(uiState: PharmacyUiState) {
    val lang = uiState.currentLanguage
    var selectedPhotoUrl by remember { mutableStateOf<String?>(null) }
    var selectedPhotoTitle by remember { mutableStateOf("") }
    var selectedPhotoDesc by remember { mutableStateOf("") }

    // Dialog for viewing high-res photo from Web URL
    if (selectedPhotoUrl != null) {
        Dialog(onDismissRequest = { selectedPhotoUrl = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = selectedPhotoUrl,
                            contentDescription = selectedPhotoTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            placeholder = painterResource(id = R.drawable.mikiyas_clinical),
                            error = painterResource(id = R.drawable.mikiyas_clinical)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = selectedPhotoTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PharmacistBluePrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedPhotoDesc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { selectedPhotoUrl = null },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = PharmacistBluePrimary)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Profile Header with Avatar loaded from AVATAR_PROFILE_PHOTO_URL
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(92.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(PharmacistBluePrimary.copy(alpha = 0.2f))
                                .padding(2.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .clickable {
                                    selectedPhotoUrl = AVATAR_PROFILE_PHOTO_URL
                                    selectedPhotoTitle = "Mikiyas Gezahegn - Clinical Pharmacist"
                                    selectedPhotoDesc = "Clinical Pharmacist and Health Tech Developer."
                                }
                        ) {
                            AsyncImage(
                                model = AVATAR_PROFILE_PHOTO_URL,
                                contentDescription = "Mikiyas Gezahegn Avatar Portrait",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                placeholder = painterResource(id = R.drawable.mikiyas_graduation),
                                error = painterResource(id = R.drawable.mikiyas_graduation)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Localization.get("dev_name", lang),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PharmacistBluePrimary
                                )
                            )
                            Surface(
                                color = EmeraldGreen,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Clinical Pharmacist",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text(
                                text = "B.Pharm Clinical Pharmacy, Clinical Pharmacist & Digital Health Innovator",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Mikiyas Gezahegn is an accomplished Clinical Pharmacist. Combining clinical expertise in pharmacotherapy, therapeutic drug monitoring, and infectious disease management with modern mobile and AI software development, he develops evidence-based digital health tools to advance patient care and clinical practice.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Photo Gallery Section loaded from web URL variables
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Professional Photo Gallery & Badges",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Tap to enlarge",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        PhotoGalleryCard(
                            imageUrl = CLINICAL_PRACTICE_PHOTO_URL,
                            title = "Medical & Clinical Practice",
                            subtitle = "Hospital Ward & Consultation",
                            onClick = {
                                selectedPhotoUrl = CLINICAL_PRACTICE_PHOTO_URL
                                selectedPhotoTitle = "Clinical Pharmacist Practice & Consultation"
                                selectedPhotoDesc = "Conducting patient care rounds, bedside pharmacotherapy consultations, therapeutic drug monitoring (TDM), and clinical medication reviews."
                            }
                        )
                    }
                    item {
                        PhotoGalleryCard(
                            imageUrl = GRADUATION_PHOTO_URL,
                            title = "University Graduation",
                            subtitle = "Academic Honors",
                            onClick = {
                                selectedPhotoUrl = GRADUATION_PHOTO_URL
                                selectedPhotoTitle = "University Graduation"
                                selectedPhotoDesc = "Graduation ceremony from the Clinical Pharmacy program."
                            }
                        )
                    }
                    item {
                        PhotoGalleryCard(
                            imageUrl = PORTRAIT_PHOTO_URL,
                            title = "Professional Portrait",
                            subtitle = "Clinical Pharmacist & Developer",
                            onClick = {
                                selectedPhotoUrl = PORTRAIT_PHOTO_URL
                                selectedPhotoTitle = "Mikiyas Gezahegn, RPh"
                                selectedPhotoDesc = "Official portrait of Mikiyas Gezahegn, Clinical Pharmacist and Digital Health Software Developer."
                            }
                        )
                    }
                    item {
                        PhotoGalleryCard(
                            imageUrl = DIGITAL_HEALTH_PHOTO_URL,
                            title = "Digital Health Hub",
                            subtitle = "Clinical AI Architecture",
                            onClick = {
                                selectedPhotoUrl = DIGITAL_HEALTH_PHOTO_URL
                                selectedPhotoTitle = "Digital Health & AI Platform"
                                selectedPhotoDesc = "Architecting evidence-based clinical pharmacy solutions powered by Google Gemini AI and pharmacokinetic engines."
                            }
                        )
                    }
                }
            }
        }

        // Education Section
        item {
            Text(
                text = Localization.get("education_title", lang),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TimelineCard(
                    title = "Bachelor of Pharmacy (B.Pharm) - Clinical Pharmacy",
                    institution = "College of Health Sciences",
                    year = "Clinical Pharmacy Graduate",
                    description = "Comprehensive clinical pharmacy curriculum covering Pharmacotherapy, Pharmacokinetics (PK/PD), Hospital Pharmacy, TDM, and Clinical Chemistry with top academic standing."
                )
                TimelineCard(
                    title = "Clinical Pharmacotherapy & Digital Health Specialization",
                    institution = "Postgraduate & Continuing Clinical Education",
                    year = "Advanced Specialization",
                    description = "Specialized in Antimicrobial Stewardship (ASP), Bayesian PK Modeling for Vancomycin/Aminoglycosides, and Clinical AI CDS."
                )
            }
        }

        item {
            Text(
                text = "Honors, Awards & Licensure",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CertificationItem(
                    name = "★ Academic Excellence Award",
                    issuer = "College of Health Sciences - Clinical Pharmacy"
                )
                CertificationItem(
                    name = "Licensed Clinical Pharmacist (RPh)",
                    issuer = "Ethiopian Food and Drug Authority (EFDA) / Healthcare Licensure"
                )
                CertificationItem(
                    name = "Board Certified Pharmacotherapy Specialist (BCPS)",
                    issuer = "Board of Pharmacy Specialties (BPS)"
                )
                CertificationItem(
                    name = "Antimicrobial Stewardship Certificate (SIDP)",
                    issuer = "Society of Infectious Diseases Pharmacists"
                )
                CertificationItem(
                    name = "Medication Therapy Management (MTM) Specialist",
                    issuer = "American Pharmacists Association (APhA)"
                )
                CertificationItem(
                    name = "Full-Stack Software Engineering & Mobile App Development",
                    issuer = "Google Developer Certification"
                )
            }
        }

        item {
            Text(
                text = Localization.get("skills_title", lang),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))

            val skills = listOf(
                "Clinical Pharmacotherapy", "Pharmacokinetics (PK/PD)", "Antimicrobial Stewardship (ASP)",
                "Therapeutic Drug Monitoring (TDM)", "Renal & Hepatic Dosing", "Drug Interaction Screening",
                "Full-Stack Development (Next.js, TypeScript)", "Android (Jetpack Compose, Room)",
                "AI Engineering (Google Gemini, LLMs)", "Health Informatics & CDS"
            )

            OptInSkillsFlow(skills)
        }
    }
}

@Composable
fun OptInSkillsFlow(skills: List<String>) {
    val chunked = skills.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        chunked.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { skill ->
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(14.dp))
                            Text(text = skill, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun PhotoGalleryCard(
    imageUrl: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(id = R.drawable.mikiyas_clinical),
                    error = painterResource(id = R.drawable.mikiyas_clinical)
                )
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun TimelineCard(title: String, institution: String, year: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(text = year, style = MaterialTheme.typography.labelSmall.copy(color = EmeraldGreen, fontWeight = FontWeight.Bold))
            }
            Text(text = institution, style = MaterialTheme.typography.bodySmall.copy(color = PharmacistBluePrimary))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun CertificationItem(name: String, issuer: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Default.Verified, contentDescription = null, tint = PharmacistBluePrimary, modifier = Modifier.size(20.dp))
            Column {
                Text(text = name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                Text(text = issuer, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun LearningSectionContent(
    uiState: PharmacyUiState,
    viewModel: PharmacyViewModel
) {
    var learningMode by remember { mutableIntStateOf(0) } // 0: MCQs, 1: Flashcards
    val disciplines = listOf("Pharmacy", "Medicine", "Nursing", "Public Health")

    val allQuizzes = PharmacyRepository.quizQuestionsList
    val currentQuestion = allQuizzes.getOrNull(uiState.currentQuizIndex)

    val allFlashcards = PharmacyRepository.flashcardsList
    val currentCard = allFlashcards.getOrNull(uiState.currentFlashcardIndex)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            TabRow(
                selectedTabIndex = learningMode,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = learningMode == 0,
                    onClick = { learningMode = 0 },
                    text = { Text("Clinical MCQ Quizzes", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = learningMode == 1,
                    onClick = { learningMode = 1 },
                    text = { Text("Interactive Flashcards", fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (learningMode == 0) {
            // MCQ Quizzes Mode
            if (uiState.quizFinished) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(48.dp))
                            Text(text = "Quiz Completed!", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                            Text(
                                text = "Your Score: ${uiState.quizScore} / ${allQuizzes.size} (${(uiState.quizScore * 100) / allQuizzes.size}%)",
                                style = MaterialTheme.typography.titleMedium.copy(color = PharmacistBluePrimary, fontWeight = FontWeight.Bold)
                            )
                            Button(
                                onClick = { viewModel.restartQuiz() },
                                colors = ButtonDefaults.buttonColors(containerColor = PharmacistBluePrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Retake Quiz")
                            }
                        }
                    }
                }
            } else if (currentQuestion != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = PharmacistBluePrimary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = currentQuestion.category,
                                        color = PharmacistBluePrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "Question ${uiState.currentQuizIndex + 1} of ${allQuizzes.size}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = currentQuestion.question,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                currentQuestion.options.forEachIndexed { index, option ->
                                    val isSelected = uiState.selectedQuizOption == index
                                    val isCorrect = index == currentQuestion.correctIndex
                                    val containerColor = when {
                                        uiState.isQuizAnswerSubmitted && isCorrect -> EmeraldGreen.copy(alpha = 0.2f)
                                        uiState.isQuizAnswerSubmitted && isSelected && !isCorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                                        isSelected -> PharmacistBluePrimary.copy(alpha = 0.15f)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.selectQuizOption(index) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = containerColor
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Text(
                                                text = "${('A' + index)}.",
                                                fontWeight = FontWeight.Bold,
                                                color = PharmacistBluePrimary
                                            )
                                            Text(text = option, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }

                            // Submit / Next Buttons
                            if (!uiState.isQuizAnswerSubmitted) {
                                Button(
                                    onClick = { viewModel.submitQuizAnswer(currentQuestion.correctIndex) },
                                    enabled = uiState.selectedQuizOption != null,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = PharmacistBluePrimary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Submit Answer", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Surface(
                                    color = EmeraldGreen.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Clinical Rationale:",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldGreen)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = currentQuestion.rationale,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }

                                Button(
                                    onClick = { viewModel.nextQuizQuestion(allQuizzes.size) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = PharmacistBluePrimary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(if (uiState.currentQuizIndex + 1 < allQuizzes.size) "Next Question" else "View Final Score")
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Interactive Flashcards Mode
            if (currentCard != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clickable { viewModel.flipFlashcard() },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (uiState.isFlashcardFlipped) EmeraldGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = currentCard.category,
                                    color = PharmacistBluePrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Card ${uiState.currentFlashcardIndex + 1} of ${allFlashcards.size}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (!uiState.isFlashcardFlipped) currentCard.frontPrompt else currentCard.backAnswer,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )

                                if (uiState.isFlashcardFlipped) {
                                    Text(
                                        text = "Clinical Pearl: ${currentCard.clinicalPearls}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = EmeraldGreen, fontWeight = FontWeight.SemiBold),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }

                            Text(
                                text = if (!uiState.isFlashcardFlipped) "Tap to Flip Answer" else "Tap to Flip Question",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.prevFlashcard(allFlashcards.size) },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Previous")
                        }

                        Button(
                            onClick = { viewModel.nextFlashcard(allFlashcards.size) },
                            colors = ButtonDefaults.buttonColors(containerColor = PharmacistBluePrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Next Card")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResearchSectionContent() {
    val researchItems = PharmacyRepository.initialResearchList

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(researchItems) { item ->
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
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = PharmacistBluePrimary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = item.category,
                                color = PharmacistBluePrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Text(text = item.year, fontWeight = FontWeight.Bold, color = EmeraldGreen, fontSize = 12.sp)
                    }

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Authors: ${item.authors} | ${item.publicationVenue}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "Summary:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text(text = item.summary, style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Clinical Significance:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = EmeraldGreen)
                            Text(text = item.clinicalSignificance, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BlogSectionContent(
    uiState: PharmacyUiState,
    viewModel: PharmacyViewModel
) {
    val articles by viewModel.allArticles.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(articles) { article ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectArticleForDetail(article) },
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
                        Surface(
                            color = EmeraldGreen.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = article.category,
                                color = EmeraldGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Text(text = article.readTime, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = article.excerpt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "By ${article.author}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, color = PharmacistBluePrimary)
                        )
                        IconButton(onClick = { viewModel.toggleBookmarkArticle(article) }) {
                            Icon(
                                imageVector = if (article.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (article.isBookmarked) PharmacistBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Article Reader Dialog
    if (uiState.selectedArticleForDetail != null) {
        val art = uiState.selectedArticleForDetail!!
        AlertDialog(
            onDismissRequest = { viewModel.selectArticleForDetail(null) },
            title = {
                Text(
                    text = art.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "By ${art.author} | ${art.readTime}",
                            style = MaterialTheme.typography.labelSmall.copy(color = EmeraldGreen, fontWeight = FontWeight.Bold)
                        )
                    }
                    item {
                        Text(
                            text = art.content,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.selectArticleForDetail(null) },
                    colors = ButtonDefaults.buttonColors(containerColor = PharmacistBluePrimary)
                ) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun AdminDashboardSectionContent(
    uiState: PharmacyUiState,
    viewModel: PharmacyViewModel
) {
    val messages by viewModel.allMessages.collectAsStateWithLifecycle()
    val drugs by viewModel.allDrugs.collectAsStateWithLifecycle()
    val history by viewModel.calcHistory.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Clinical Administrator Dashboard",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PharmacistBluePrimary)
                    )
                    Text(
                        text = "System health, database logs, and client contact messages.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Stats Matrix
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminStatCard(title = "${drugs.size}", subtitle = "Active Drugs", modifier = Modifier.weight(1f))
                AdminStatCard(title = "${history.size}", subtitle = "Saved Calcs", modifier = Modifier.weight(1f))
                AdminStatCard(title = "${messages.size}", subtitle = "Messages", modifier = Modifier.weight(1f))
            }
        }

        // Messages Inbox
        item {
            Text(
                text = "Contact Form Inquiries (${messages.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))

            if (messages.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("No consultation messages received yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        items(messages) { msg ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = msg.name, fontWeight = FontWeight.Bold)
                        Text(text = msg.email, style = MaterialTheme.typography.labelSmall, color = PharmacistBluePrimary)
                    }
                    Text(text = "Subject: ${msg.subject}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = msg.message, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PharmacistBluePrimary))
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ContactSectionContent(
    uiState: PharmacyUiState,
    viewModel: PharmacyViewModel
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Get in Touch with Pharmacist Mikiyas",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PharmacistBluePrimary)
                    )
                    Text(
                        text = "For clinical consultations, hospital pharmacotherapy reviews, or software engineering collaborations:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                        Text(text = "gezahegnmikiyas842@gmail.com", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                    }
                }
            }
        }

        // Contact Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "Send Direct Message", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                    OutlinedTextField(
                        value = uiState.contactName,
                        onValueChange = { viewModel.updateContactForm(name = it) },
                        label = { Text("Your Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.contactEmail,
                        onValueChange = { viewModel.updateContactForm(email = it) },
                        label = { Text("Your Email Address") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.contactSubject,
                        onValueChange = { viewModel.updateContactForm(subject = it) },
                        label = { Text("Subject (e.g. Clinical Consultation / Project)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.contactMessage,
                        onValueChange = { viewModel.updateContactForm(message = it) },
                        label = { Text("Your Message") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Button(
                        onClick = { viewModel.submitContactMessage() },
                        enabled = uiState.contactName.isNotBlank() && uiState.contactEmail.isNotBlank() && uiState.contactMessage.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PharmacistBluePrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Message", fontWeight = FontWeight.Bold)
                    }

                    if (uiState.contactSubmitted) {
                        Surface(
                            color = EmeraldGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen)
                                Text(
                                    text = "Thank you! Your message has been sent successfully. Pharmacist Mikiyas will reply shortly.",
                                    color = EmeraldGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
