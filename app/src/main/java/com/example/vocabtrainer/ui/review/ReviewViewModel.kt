package com.example.vocabtrainer.ui.review

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabtrainer.data.Vocab
import com.example.vocabtrainer.data.VocabDatabase
import com.example.vocabtrainer.data.VocabRepository
import com.example.vocabtrainer.data.VocabsLocalDataSource
import com.example.vocabtrainer.services.DatabaseServiceFirestore
import com.example.vocabtrainer.services.DatabaseServiceRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val NUMBER_OF_VOCABS: Int = 5

class ReviewViewModel(application: Application) : AndroidViewModel(application) {
    private val vocabRepository: VocabRepository
    private var correct = MutableList(NUMBER_OF_VOCABS) { true }
    private var _currentState by mutableStateOf(State.START)
    private var _vocabIndex by mutableStateOf(0)
    private var _wrongAnswer by mutableStateOf(false)
    private lateinit var _vocabsLocal: List<Vocab>
    private var foreignInput = true
    var learnMode by mutableStateOf(false)
    var showHint by mutableStateOf(false)

    init {
        val userDao = VocabDatabase.getDatabase(application).vocabDao()
        vocabRepository = VocabRepository(
            DatabaseServiceRoom(userDao),
            DatabaseServiceFirestore(),
            VocabsLocalDataSource()
        )
    }


    //    var isFetched: Boolean by mutableStateOf(false)
    var errorMessage: String = ""

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

    fun parseCSV(uri: Uri, context: Context) {
        currentState = State.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            vocabRepository.insertVocabsRoom(vocabRepository.parseCSV(uri, context))
            currentState = State.START
        }
    }


    fun startReview() {
        currentState = State.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            _vocabsLocal = vocabRepository.getVocabsDB(NUMBER_OF_VOCABS)
            _vocabIndex = 0
            if (_vocabsLocal.isEmpty()) {
                _currentState = State.ERROR
                errorMessage = "No vocabs available, create or import some first"
            } else {
                _currentState = State.LEARNING
            }
            Log.d("ReviewViewModel", "Vocabs fetched: $_vocabsLocal")
        }
    }

    fun incrementIndex() {
        if (vocabIndex + 1 < vocabs.size) {
            _vocabIndex++
        } else {
            _currentState = State.LOADING
            wrapUpReview()
        }
    }

    private fun wrapUpReview() {
        viewModelScope.launch(Dispatchers.IO) {
            _vocabsLocal.forEachIndexed { index, vocab ->
                if (!learnMode && correct[index]) vocab.level++
                vocabRepository.deleteVocabRoom(vocab)
            }
            vocabRepository.insertVocabsRoom(_vocabsLocal)
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


//    fun uploadTest() {
//        viewModelScope.launch(Dispatchers.IO) {
//            vocabRepository.uploadTest(vocabRepository.getVocabsLocal())
//        }
//    }

//    private fun fetchVocabs(): MutableList<Vocab> {
//        vocabRepository.getVocabs {
//            vocabs = it.toMutableList()
//            isFetched = true
//            Log.d("Inside ReviewViewModel", "${vocabs[0]}, ${vocabs[1]}")
//        }
//        return vocabs
//    }

    fun checkInput(input: String): Boolean {
        val trimmedInput = input.trim()
        var wrong = true
        if (foreignInput) {
            val splitVocabs = vocabs[vocabIndex].foreignWord.split(',', '/')
            for (word in splitVocabs) {
                if (word.trim() == trimmedInput) wrong = false
            }
            Log.d("InputChecker", "Split vocabs are: $splitVocabs and input is $input")
            (!splitVocabs.contains(input))
        } else {
            val splitVocabs = vocabs[vocabIndex].foreignWord.split(',', '/')
            for (word in splitVocabs) {
                if (word.trim() == trimmedInput) wrong = false
            }        }
        if (wrong) correct[vocabIndex] = false
        return wrong
    }

    fun addVocabs() {
        viewModelScope.launch(Dispatchers.IO) {
            vocabRepository.insertVocabsRoom(vocabs)
            Log.d("ReviewViewModel", "Insert done $vocabs")
        }
    }

}