package com.example.vocabtrainer.data

import javax.inject.Inject

class VocabsLocalDataSource @Inject constructor() {

    val vocabs = mutableListOf(
        Vocab("des cacahuètes", "Erdnüsse", 1),
    )
}