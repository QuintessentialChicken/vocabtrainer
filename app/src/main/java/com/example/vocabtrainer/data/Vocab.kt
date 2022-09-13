package com.example.vocabtrainer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocabs")
data class Vocab(
    @PrimaryKey val domesticWord: String = "",
    val foreignWord: String = "",
    var level: Int = 1,
)