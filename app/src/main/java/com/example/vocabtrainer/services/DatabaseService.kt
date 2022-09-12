package com.example.vocabtrainer.services

import com.example.vocabtrainer.data.Vocab

interface DatabaseService {
    suspend fun getVocabs(): List<Vocab>
    suspend fun uploadTest(vocabs: List<Vocab>)
}