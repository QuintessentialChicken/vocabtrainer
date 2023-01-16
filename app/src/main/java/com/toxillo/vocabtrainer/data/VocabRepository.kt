package com.toxillo.vocabtrainer.data

import android.content.Context
import android.net.Uri
import com.google.firebase.firestore.util.Executors
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.toxillo.vocabtrainer.services.DatabaseServiceFirestore
import com.toxillo.vocabtrainer.services.DatabaseServiceRoom
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
        val reader = context.contentResolver.openInputStream(uri) ?: return emptyList()
        return reader.bufferedReader().lineSequence()
            .filter { it.isNotBlank() }
            .map {
                val (vocabA, vocabB) = it.split(";", limit = 2)
                Vocab(vocabA, vocabB, "Französisch")
            }
            .toList()
    }

    //Get prepacked remote csv
    fun parseCSV(onSuccess: (List<Vocab>) -> Unit) {
        val storageRef = Firebase.storage.reference
        storageRef
            .child("FR_DE.csv")
            .stream
            .addOnSuccessListener(Executors.BACKGROUND_EXECUTOR){ snapshot ->
                onSuccess(snapshot
                    .stream
                    .bufferedReader()
                    .lineSequence()
                    .filter { it.isNotBlank() }
                    .map {
                        val (vocabA, vocabB) = it.split(";", limit = 2)
                        Vocab(vocabA, vocabB, "Französisch")
                    }
                    .toList()
                )
            }
    }
}