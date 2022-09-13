package com.example.vocabtrainer.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.vocabtrainer.services.DatabaseServiceFirestore
import com.example.vocabtrainer.services.DatabaseServiceRoom
import javax.inject.Inject

class VocabRepository @Inject constructor(
    private val databaseServiceRoom: DatabaseServiceRoom,
    private val databaseServiceFirestore: DatabaseServiceFirestore,
    private val localDataSource: VocabsLocalDataSource
) {


    suspend fun uploadTest(vocabs: List<Vocab>) {
        databaseServiceFirestore.uploadTest(vocabs)
    }

    suspend fun getVocabs(onSuccess: (List<Vocab>) -> Unit) {
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

    suspend fun deleteVocabRoom(vocab: Vocab) {
        databaseServiceRoom.deleteVocab(vocab)
    }

    fun parseCSV(uri: Uri, context: Context): List<Vocab> {
        Log.d("VocabRepository", "The given uri is: $uri")
        val reader = context.contentResolver.openInputStream(uri) ?: return emptyList()
        return reader.bufferedReader().lineSequence()
            .filter { it.isNotBlank() }
            .map {
                val (langA, langB, vocabA, vocabB) = it.split(",", limit = 4)
                Vocab(vocabA, vocabB, langA)
            }
            .toList()
    }
}