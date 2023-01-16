package com.toxillo.vocabtrainer.data

import javax.inject.Inject

class VocabsLocalDataSource @Inject constructor() {

    val vocabs = mutableListOf(
        Vocab("des cacahuètes", "Erdnüsse", "Französisch",1),
    )
}