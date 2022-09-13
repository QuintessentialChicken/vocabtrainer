package com.example.vocabtrainer.services

import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import com.example.vocabtrainer.data.Vocab
import com.example.vocabtrainer.data.VocabDao
import com.example.vocabtrainer.data.VocabDatabase

class DatabaseServiceRoom(private val vocabDao: VocabDao) : DatabaseService {
    override suspend fun getVocabs(limit: Int): List<Vocab> {
        return vocabDao.getWithLimit(limit)
    }

    override suspend fun uploadTest(vocabs: List<Vocab>) {
        vocabDao.insert(vocabs)
    }

    override suspend fun deleteVocab(vocab: Vocab) {
        vocabDao.delete(vocab)
    }



}