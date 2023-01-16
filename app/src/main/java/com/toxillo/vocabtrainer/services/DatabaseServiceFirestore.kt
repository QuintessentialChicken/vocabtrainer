package com.toxillo.vocabtrainer.services

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.toxillo.vocabtrainer.data.Vocab

class DatabaseServiceFirestore : DatabaseService {
    private val ref: FirebaseFirestore = Firebase.firestore
    private val vocabsRef: CollectionReference = ref.collection("vocabs")

    override suspend fun getVocabs(limit: Int): List<Vocab> {
        vocabsRef
            .get()
//            .addOnSuccessListener {
//                onSuccess(it.toObjects(Vocab::class.java))
//            }
        return listOf()
    }

    override suspend fun uploadTest(vocabs: List<Vocab>
    ) {
        val batch = ref.batch()
        vocabs.forEach {
            batch.set(vocabsRef.document(), it)
        }
        batch.commit()
    }

    override suspend fun deleteVocab(vocab: Vocab) {
        TODO("Not yet implemented")
    }


}