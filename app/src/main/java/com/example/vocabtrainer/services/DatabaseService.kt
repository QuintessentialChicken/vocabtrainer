package com.example.vocabtrainer.services

import com.example.vocabtrainer.data.Vocab

interface DatabaseService {
    suspend fun getVocabs(limit: Int = 0): List<Vocab>
    suspend fun uploadTest(vocabs: List<Vocab>)
    suspend fun deleteVocab(vocab: Vocab)
}