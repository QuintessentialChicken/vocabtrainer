package com.example.vocabtrainer.data

import com.example.vocabtrainer.services.DatabaseServiceFirestore
import com.example.vocabtrainer.services.DatabaseServiceRoom
import javax.inject.Inject

class VocabRepository @Inject constructor(
    private val databaseServiceRoom: DatabaseServiceRoom,
    private val databaseServiceFirestore: DatabaseServiceFirestore,
    private val localDataSource: VocabsLocalDataSource
){


    suspend fun uploadTest(vocabs: List<Vocab>) {
        databaseServiceFirestore.uploadTest(vocabs)
    }

    suspend fun getVocabs(onSuccess: (List<Vocab>) -> Unit){
        databaseServiceFirestore.getVocabs()
    }

    fun getVocabsLocal(): List<Vocab> {
        return localDataSource.vocabs
    }

    suspend fun getVocabsDB(limit: Int): List<Vocab> {
        return databaseServiceRoom.getVocabs(limit)
    }

    suspend fun insertVocabsRoom(vocabs: List<Vocab>) {
        databaseServiceRoom.uploadTest(vocabs)
    }
}