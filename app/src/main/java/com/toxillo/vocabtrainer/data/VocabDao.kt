package com.toxillo.vocabtrainer.data

import androidx.room.*

@Dao
interface VocabDao {
    @Query("SELECT * FROM vocabs")
    fun getAll(): List<Vocab>

    @Query("SELECT * FROM vocabs LIMIT :limit")
    fun getWithLimit(limit: Int) : List<Vocab>

    @Query("SELECT * FROM vocabs WHERE level=:level")
    fun getByLevel(level: Int) : List<Vocab>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vocabs: List<Vocab>)

    @Delete
    fun delete(vocab: Vocab)
}