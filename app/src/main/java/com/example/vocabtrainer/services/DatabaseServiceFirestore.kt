package com.example.vocabtrainer.services

import android.util.Log
import com.example.vocabtrainer.data.Vocab
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DatabaseServiceFirestore : DatabaseService {
    private val ref: FirebaseFirestore = Firebase.firestore
    private val vocabsRef: CollectionReference = ref.collection("vocabs")

    override suspend fun getVocabs(): List<Vocab> {
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
        Log.d("DataService", "I was run!")
        vocabs.forEach {
            batch.set(vocabsRef.document(), it)
        }
        batch.commit()
    }


}