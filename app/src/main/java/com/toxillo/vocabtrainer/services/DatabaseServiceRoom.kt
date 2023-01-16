package com.toxillo.vocabtrainer.services

import com.toxillo.vocabtrainer.data.Vocab
import com.toxillo.vocabtrainer.data.VocabDao

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