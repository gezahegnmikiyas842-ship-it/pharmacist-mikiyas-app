package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.PharmacistBluePrimary
import com.example.ui.util.AppImageUrls
import com.example.ui.util.Localization
import com.example.ui.viewmodel.MoreSubTab
import com.example.ui.viewmodel.NavigationTab
import com.example.ui.viewmodel.PharmacyUiState
import com.example.ui.viewmodel.PharmacyViewModel

@Composable
fun HomeScreen(
    uiState: PharmacyUiState,
    viewModel: PharmacyViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.currentLanguage

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("home_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // Hero Section Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color(0xFF0B1120))
                    ) {
                        AsyncImage(
                            model = AppImageUrls.HERO_BANNER_PHOTO_URL,
                            contentDescription = "Clinical Pharmacist Mikiyas Gezahegn Banner",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize(),
                            placeholder = painterResource(id = R.drawable.mikiyas_banner),
                            error = painterResource(id = R.drawable.mikiyas_banner)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color(0xDD0B1120)),
                                        startY = 80f
                                    )
                                )
                        )
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFD700))
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            ) {
                                AsyncImage(
                                    model = AppImageUrls.AVATAR_PROFILE_PHOTO_URL,
                                    contentDescription = "Mikiyas Gezahegn - Clinical Pharmacist",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                    placeholder = painterResource(id = R.drawable.mikiyas_portrait),
                                    error = painterResource(id = R.drawable.mikiyas_portrait)
                                )
                            }

                            Column {
                                Surface(
                                    color = EmeraldGreen,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Clinical Pharmacist",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = Localization.get("dev_name", lang),
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = Localization.get("hero_greeting", lang),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PharmacistBluePrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = Localization.get("hero_sub", lang),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.setTab(NavigationTab.CALCULATORS) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("hero_cta_calculators"),
                                colors = ButtonDefaults.buttonColors(containerColor = PharmacistBluePrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Calculators", fontSize = 13.sp)
                            }
                            OutlinedButton(
                                onClick = { viewModel.setTab(NavigationTab.AI_ASSISTANT) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("hero_cta_ai"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp), tint = EmeraldGreen)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ask Gemini", fontSize = 13.sp, color = EmeraldGreen)
                            }
                        }
                    }
                }
            }
        }

        // Animated Statistics Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "1+ Yr",
                    subtitle = Localization.get("stat_experience", lang),
                    icon = Icons.Default.Timeline,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "99.8%",
                    subtitle = Localization.get("stat_accuracy", lang),
                    icon = Icons.Default.Verified,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "8+",
                    subtitle = Localization.get("stat_calculators", lang),
                    icon = Icons.Default.Calculate,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "500+",
                    subtitle = Localization.get("stat_drugs", lang),
                    icon = Icons.Default.Medication,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Clinical Shortcuts
        item {
            Text(
                text = "Clinical Hub Quick Access",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    title = "Cockcroft-Gault",
                    subtitle = "CrCl Renal Dosing",
                    icon = Icons.Default.Speed,
                    badgeColor = PharmacistBluePrimary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setActiveCalculator(com.example.ui.viewmodel.CalculatorType.CRCL)
                        viewModel.setTab(NavigationTab.CALCULATORS)
                    }
                )
                QuickActionCard(
                    title = "Interactions",
                    subtitle = "Drug-Drug Matrix",
                    icon = Icons.Default.Shuffle,
                    badgeColor = EmeraldGreen,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setTab(NavigationTab.DRUG_HUB)
                    }
                )
            }
        }

        // Featured Clinical Services
        item {
            Text(
                text = Localization.get("featured_services", lang),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ServiceItemCard(
                    title = Localization.get("service_calculators", lang),
                    description = Localization.get("service_calculators_desc", lang),
                    icon = Icons.Default.Calculate,
                    color = PharmacistBluePrimary,
                    onClick = { viewModel.setTab(NavigationTab.CALCULATORS) }
                )
                ServiceItemCard(
                    title = Localization.get("service_interactions", lang),
                    description = Localization.get("service_interactions_desc", lang),
                    icon = Icons.Default.CompareArrows,
                    color = EmeraldGreen,
                    onClick = { viewModel.setTab(NavigationTab.DRUG_HUB) }
                )
                ServiceItemCard(
                    title = Localization.get("service_ai", lang),
                    description = Localization.get("service_ai_desc", lang),
                    icon = Icons.Default.AutoAwesome,
                    color = Color(0xFF8B5CF6),
                    onClick = { viewModel.setTab(NavigationTab.AI_ASSISTANT) }
                )
                ServiceItemCard(
                    title = Localization.get("service_learning", lang),
                    description = Localization.get("service_learning_desc", lang),
                    icon = Icons.Default.School,
                    color = Color(0xFFF59E0B),
                    onClick = {
                        viewModel.setMoreSubTab(MoreSubTab.LEARNING)
                        viewModel.setTab(NavigationTab.MORE_HUB)
                    }
                )
            }
        }

        // Clinical Pharmacist Profile Card with Photos
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.setMoreSubTab(MoreSubTab.ABOUT)
                        viewModel.setTab(NavigationTab.MORE_HUB)
                    },
                shape = RoundedCornerShape(16.dp),
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
                                .size(76.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(PharmacistBluePrimary.copy(alpha = 0.2f))
                                .padding(2.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                        ) {
                            AsyncImage(
                                model = AppImageUrls.GRADUATION_PHOTO_URL,
                                contentDescription = "Mikiyas Gezahegn Graduation",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                placeholder = painterResource(id = R.drawable.mikiyas_graduation),
                                error = painterResource(id = R.drawable.mikiyas_graduation)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Mikiyas Gezahegn",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = PharmacistBluePrimary
                            )
                            Surface(
                                color = EmeraldGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Clinical Pharmacist",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Madda Walabu University Graduate, Clinical Pharmacist & Digital Health Innovator.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.setMoreSubTab(MoreSubTab.ABOUT)
                                viewModel.setTab(NavigationTab.MORE_HUB)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("View Full Bio & Photos", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.setMoreSubTab(MoreSubTab.CONTACT)
                                viewModel.setTab(NavigationTab.MORE_HUB)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = PharmacistBluePrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.MailOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Consult", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Research Snapshot & Portfolio Highlights
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.setMoreSubTab(MoreSubTab.RESEARCH)
                        viewModel.setTab(NavigationTab.MORE_HUB)
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(PharmacistBluePrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = "Research",
                            tint = PharmacistBluePrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Research & Scientific Publications",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Antimicrobial Stewardship, PK/PD Vancomycin optimization, and Clinical AI CDS tools.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open Research",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Testimonial / Clinical Impact
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.FormatQuote, contentDescription = null, tint = EmeraldGreen)
                        Text(
                            text = "Clinical Endorsements",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"Pharmacist Mikiyas's clinical tools and pharmacokinetic insights have tremendously streamlined our hospital antimicrobial stewardship audits and bedside renal dosage titrations.\"",
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "— Senior Internal Medicine Attending & Hospital ICU Team",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Contact CTA
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PharmacistBluePrimary)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Professional Consultation",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Connect for pharmacotherapy consulting, clinical trials, or digital health engineering.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    Button(
                        onClick = {
                            viewModel.setMoreSubTab(MoreSubTab.CONTACT)
                            viewModel.setTab(NavigationTab.MORE_HUB)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Connect", color = PharmacistBluePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PharmacistBluePrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun ServiceItemCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
        }
    }
}
