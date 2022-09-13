package com.example.vocabtrainer.ui.review

//data class ReviewState(
//    var currentState: State = State.LOADING,
//    val numberVocabs: Int = 10,
//    var vocabs: List<Vocab> = emptyList(),
//    var vocabIndex: Int = 0,
//    var wrongAnswer: Boolean = false
//)

enum class State {
    START,
    LOADING,
    ERROR,
    LEARNING,
    FINISHED
}
