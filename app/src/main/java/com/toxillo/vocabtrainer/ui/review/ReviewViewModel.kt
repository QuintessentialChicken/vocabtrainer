package com.toxillo.vocabtrainer.ui.review

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.toxillo.vocabtrainer.data.Vocab
import com.toxillo.vocabtrainer.data.VocabDatabase
import com.toxillo.vocabtrainer.data.VocabRepository
import com.toxillo.vocabtrainer.data.VocabsLocalDataSource
import com.toxillo.vocabtrainer.services.DatabaseServiceFirestore
import com.toxillo.vocabtrainer.services.DatabaseServiceRoom
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
                vocabRepository.parseCSV {
                    viewModelScope.launch(Dispatchers.IO) {
                        _vocabsLocal = it
                        vocabRepository.insertVocabsRoom(_vocabsLocal)
                        _currentState = State.LEARNING
                    }
                }
            } else {
                _currentState = State.LEARNING
            }
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
            (!splitVocabs.contains(input))
        } else {
            val splitVocabs = vocabs[vocabIndex].foreignWord.split(',', '/')
            for (word in splitVocabs) {
                if (word.trim() == trimmedInput) wrong = false
            }
            !splitVocabs.contains((input))
        }
        if (wrong) correct[vocabIndex] = false
        return wrong
    }

    fun addVocabs() {
        viewModelScope.launch(Dispatchers.IO) {
            vocabRepository.insertVocabsRoom(vocabs)
        }
    }

}