package com.example.data.local

import androidx.room.*
import com.example.data.model.ArticleItem
import com.example.data.model.CalcHistoryEntity
import com.example.data.model.ContactMessageEntity
import com.example.data.model.DrugItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PharmacyDao {
    // Drugs
    @Query("SELECT * FROM drugs ORDER BY genericName ASC")
    fun getAllDrugs(): Flow<List<DrugItem>>

    @Query("SELECT * FROM drugs WHERE genericName LIKE '%' || :query || '%' OR brandNames LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchDrugs(query: String): Flow<List<DrugItem>>

    @Query("SELECT * FROM drugs WHERE isSaved = 1")
    fun getSavedDrugs(): Flow<List<DrugItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrugs(drugs: List<DrugItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrug(drug: DrugItem)

    @Update
    suspend fun updateDrug(drug: DrugItem)

    @Delete
    suspend fun deleteDrug(drug: DrugItem)

    // Calculators History
    @Query("SELECT * FROM calc_history ORDER BY timestamp DESC")
    fun getAllCalcHistory(): Flow<List<CalcHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalcHistory(entry: CalcHistoryEntity)

    @Query("DELETE FROM calc_history WHERE id = :id")
    suspend fun deleteCalcHistoryById(id: Int)

    @Query("DELETE FROM calc_history")
    suspend fun clearCalcHistory()

    // Articles
    @Query("SELECT * FROM articles")
    fun getAllArticles(): Flow<List<ArticleItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleItem)

    @Update
    suspend fun updateArticle(article: ArticleItem)

    @Delete
    suspend fun deleteArticle(article: ArticleItem)

    // Contact Messages
    @Query("SELECT * FROM contact_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<ContactMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ContactMessageEntity)

    @Query("DELETE FROM contact_messages WHERE id = :id")
    suspend fun deleteMessage(id: Int)
}
