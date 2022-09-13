package com.example.vocabtrainer.ui.review

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

private const val CONTENT_ANIMATION_DURATION = 500


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ReviewScreen(
    viewModel: ReviewViewModel = viewModel(),
) {
    val context = LocalContext.current
    val modifier = Modifier
    var input by remember { mutableStateOf("") }
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.parseCSV(uri, context)
        }
    }

    BackHandler(enabled = viewModel.currentState != State.START) {
        viewModel.currentState = State.START
    }
    Box(
        modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
//        Crossfade(targetState = viewModel.currentState) { state ->
        when (viewModel.currentState) {
            State.START -> Button(onClick = { viewModel.startReview() }) { Text(text = "Click to start Reviewing") }
            State.LOADING -> {}//TODO Make some sort of spinner
            State.ERROR -> {
                Button(
                    onClick = {
                        filePickerLauncher.launch("text/comma-separated-values")
                    }) {
                    DefaultText(text = viewModel.errorMessage, modifier = modifier)
                }
            }
            State.LEARNING -> {
                Column {
                    ReviewTopAppBar(
                        vocabIndex = viewModel.vocabIndex - 1,
                        totalVocabCount = viewModel.vocabs.size
                    )

                    AnimatedContent(
                        targetState = viewModel.vocabIndex,
                        transitionSpec = {
                            val animationSpec: TweenSpec<IntOffset> =
                                tween(CONTENT_ANIMATION_DURATION)
                            val direction = AnimatedContentScope.SlideDirection.Left
                            slideIntoContainer(
                                towards = direction,
                                animationSpec = animationSpec
                            ) with
                                    slideOutOfContainer(
                                        towards = direction,
                                        animationSpec = animationSpec
                                    )
                        }
                    ) { targetState ->
                        val color: Color by animateColorAsState(if (viewModel.wrongAnswer) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant)
                        ReviewVocab(
                            word = viewModel.vocabs[targetState].domesticWord,
                            input = input,
                            color = color,
                            text = targetState.toString(),
                            onValueChange = {
                                input = it
                                viewModel.wrongAnswer = false
                            },
                            onGo = {
//                        if (viewModel.checkInput(input)) {
                                input = ""
                                viewModel.incrementIndex()
//                        } else {
//                            viewModel.wrongAnswer = true
//                            Log.d("Check if correct", "NOT CORRECT, TRY AGAIN")
//                        }
                            },
                            modifier = modifier
                        )
                    }
                }
            }
            State.FINISHED -> {
                Button(onClick = { viewModel.startReview() }) { Text(text = "Click to review more") }
            }
        }
//        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewVocab(
    word: String,
    input: String,
    text: String,
    color: Color,
    onValueChange: (String) -> Unit,
    onGo: () -> Unit,
    modifier: Modifier
) {

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ReviewCard(
            word = word,
            modifier = modifier
        )
        TextField(  //Input
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = input,
            onValueChange = { onValueChange(it) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(
                onGo = { onGo() }
            ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = color
            )
        )
    }
}

@Composable
fun ReviewCard(
    word: String,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3F),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            DefaultText(
                text = word,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ReviewTopAppBar(
    vocabIndex: Int,
    totalVocabCount: Int,
//    onBackPressed: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val animatedProgress by animateFloatAsState(
            targetValue = (vocabIndex + 1) / totalVocabCount.toFloat(),
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
        )
        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        )
    }

}

@Composable
fun DefaultText(
    text: String,
    modifier: Modifier
) {
    Text(
        modifier = modifier
            .fillMaxWidth(),
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineLarge,
    )
}
