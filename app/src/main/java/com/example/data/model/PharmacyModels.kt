package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    EN("en", "English", "English"),
    OM("om", "Afaan Oromoo", "Afaan Oromoo"),
    AM("am", "Amharic", "አማርኛ")
}

@Entity(tableName = "drugs")
@JsonClass(generateAdapter = true)
data class DrugItem(
    @PrimaryKey val id: String,
    val genericName: String,
    val brandNames: String, // comma separated
    val category: String,
    val moa: String,
    val standardDosage: String,
    val contraindications: String,
    val sideEffects: String,
    val pregnancyCategory: String, // A, B, C, D, X
    val lactationSafety: String,
    val knownInteractions: String,
    val storage: String,
    val counselingPoints: String,
    val isSaved: Boolean = false
)

enum class InteractionSeverity(val label: String, val level: Int) {
    CRITICAL("Contraindicated / Critical", 4),
    MAJOR("Major Interaction", 3),
    MODERATE("Moderate Interaction", 2),
    MINOR("Minor / Monitor", 1)
}

data class DrugInteractionCheckResult(
    val drugA: String,
    val drugB: String,
    val severity: InteractionSeverity,
    val mechanism: String,
    val clinicalEffect: String,
    val management: String
)

@Entity(tableName = "calc_history")
data class CalcHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val calcType: String,
    val title: String,
    val inputSummary: String,
    val resultValue: String,
    val interpretation: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "articles")
data class ArticleItem(
    @PrimaryKey val id: String,
    val title: String,
    val excerpt: String,
    val content: String,
    val category: String,
    val author: String = "Mikiyas Gezahegn, RPh",
    val readTime: String,
    val tags: String,
    val isBookmarked: Boolean = false,
    val likesCount: Int = 24
)

data class ResearchItem(
    val id: String,
    val title: String,
    val authors: String,
    val publicationVenue: String,
    val year: String,
    val category: String, // Publication, Poster, Summary
    val summary: String,
    val clinicalSignificance: String
)

data class QuizQuestion(
    val id: Int,
    val category: String, // Pharmacy, Medicine, Nursing, Public Health
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val rationale: String
)

data class FlashcardItem(
    val id: Int,
    val category: String,
    val frontPrompt: String,
    val backAnswer: String,
    val clinicalPearls: String
)

@Entity(tableName = "contact_messages")
data class ContactMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val subject: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false
)
