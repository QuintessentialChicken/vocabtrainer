package com.example.vocabtrainer.ui.review

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabtrainer.data.Vocab
import com.example.vocabtrainer.data.VocabDatabase
import com.example.vocabtrainer.data.VocabRepository
import com.example.vocabtrainer.data.VocabsLocalDataSource
import com.example.vocabtrainer.navigation.Review
import com.example.vocabtrainer.services.DatabaseServiceFirestore
import com.example.vocabtrainer.services.DatabaseServiceRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReviewViewModel(application: Application) : AndroidViewModel(application) {
    private val vocabRepository: VocabRepository
    var uiState by mutableStateOf(ReviewState())
        private set

    private lateinit var _vocabsLocal: MutableList<Vocab>

    init {
        val userDao = VocabDatabase.getDatabase(application).vocabDao()
        vocabRepository = VocabRepository(
            DatabaseServiceRoom(userDao),
            DatabaseServiceFirestore(),
            VocabsLocalDataSource()
        )
        viewModelScope.launch (Dispatchers.IO) {
            uiState.vocabs = vocabRepository.getVocabsDB()
            uiState.currentState = State.LEARNING
            Log.d("ReviewViewModel", "Is fetched equals: $isFetched")
        }
    }


    private var foreignInput = true
    private var vocabs: MutableList<Vocab> = mutableListOf()
    private var _isWrong by mutableStateOf(false)
    private var _count: Int by mutableStateOf(0)


    var isFetched: Boolean by mutableStateOf(false)

    var isWrong: Boolean
        get() = _isWrong
        set(value) {
            _isWrong = value
        }

    private val vocabsLocal: List<Vocab>
        get() = _vocabsLocal.toList()

    private var _isCorrect by mutableStateOf(false)

    var correct: Boolean
        get() = _isCorrect
        set(value) {
            _isCorrect = value
        }

//    var count: Int
//        get() = _count
//        set(value) {
//            _count = (_count + value) % _vocabsLocal.size
//        }


    fun incrementIndex() {
        uiState.vocabIndex++
        if (uiState.vocabIndex >= uiState.numberVocabs) uiState.currentState = State.FINISHED
    }

//    private fun getVocabInternal(): String {
//        return if (isFetched) {
//            if (foreignInput) {
//                vocabs[count].domesticWord
//            } else {
//                vocabs[count].foreignWord
//            }
//        } else {
////            fetchVocabs()
//            ""
//        }
//    }




    fun uploadTest() {
        viewModelScope.launch(Dispatchers.IO) {
            vocabRepository.uploadTest(vocabRepository.getVocabsLocal())
        }
    }

//    private fun fetchVocabs(): MutableList<Vocab> {
//        vocabRepository.getVocabs {
//            vocabs = it.toMutableList()
//            isFetched = true
//            Log.d("Inside ReviewViewModel", "${vocabs[0]}, ${vocabs[1]}")
//        }
//        return vocabs
//    }

    fun checkInput(input: String){
        val trimmedInput = input.trim()
        if (foreignInput) {
            uiState.wrongAnswer = (trimmedInput != vocabsLocal[uiState.vocabIndex].foreignWord)
        } else {
            uiState.wrongAnswer = (trimmedInput != vocabsLocal[uiState.vocabIndex].domesticWord)
        }
    }

    fun addVocabs() {
        viewModelScope.launch(Dispatchers.IO) {
            vocabRepository.insertVocabsRoom(vocabsLocal)
            Log.d("ReviewViewModel", "Insert done $vocabsLocal")
        }
    }

}