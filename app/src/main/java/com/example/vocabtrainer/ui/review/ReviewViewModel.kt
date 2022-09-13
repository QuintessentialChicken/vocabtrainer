package com.example.vocabtrainer.ui.review

import android.app.Application
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
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

private const val NUMBER_OF_VOCABS: Int = 10

class ReviewViewModel(application: Application) : AndroidViewModel(application) {
    private val vocabRepository: VocabRepository

    private var _currentState by mutableStateOf(State.START)
    private var _vocabIndex by mutableStateOf(0)
    private var _wrongAnswer by mutableStateOf(false)
    private lateinit var _vocabsLocal: List<Vocab>
    private var foreignInput = true

    init {
        val userDao = VocabDatabase.getDatabase(application).vocabDao()
        vocabRepository = VocabRepository(
            DatabaseServiceRoom(userDao),
            DatabaseServiceFirestore(),
            VocabsLocalDataSource()
        )
    }


//    var isFetched: Boolean by mutableStateOf(false)

    var currentState: State
        get() = _currentState
        set(value) {
            _currentState = value
        }
    val vocabIndex: Int
        get() = _vocabIndex
    var wrongAnswer: Boolean
        get() = _wrongAnswer
        set(value) {
            _wrongAnswer = value
        }
    val vocabs: List<Vocab>
        get() = _vocabsLocal.toList()


    fun startReview() {
        currentState = State.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            _vocabsLocal = vocabRepository.getVocabsDB(NUMBER_OF_VOCABS)
            currentState = State.LEARNING
            Log.d("ReviewViewModel", "Vocabs fetched: $_vocabsLocal")
        }
    }

    fun incrementIndex() {
        if (vocabIndex + 1 < NUMBER_OF_VOCABS) {
            _vocabIndex++
        } else {
            currentState = State.FINISHED
        }
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

    fun checkInput(input: String) {
        val trimmedInput = input.trim()
        wrongAnswer = if (foreignInput) {
            (trimmedInput != vocabs[vocabIndex].foreignWord)
        } else {
            (trimmedInput != vocabs[vocabIndex].domesticWord)
        }
    }

    fun addVocabs() {
        viewModelScope.launch(Dispatchers.IO) {
            vocabRepository.insertVocabsRoom(vocabs)
            Log.d("ReviewViewModel", "Insert done $vocabs")
        }
    }

}