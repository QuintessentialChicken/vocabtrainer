package com.example.vocabtrainer.ui.review

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.rememberRipple
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
    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
//        Crossfade(targetState = viewModel.currentState) { state ->
        when (viewModel.currentState) {
            State.START -> {
                Button(
                    onClick = {
                        viewModel.startReview()
                    }) {
                    Text(text = "Click to start Reviewing")
                }
                LabeledCheckbox(
                    checked = viewModel.learnMode,
                    onCheckedChange = { viewModel.learnMode = !viewModel.learnMode },
                    label = "Learn mode"
                )
            }
            State.LOADING -> {}//TODO Make some sort of spinner
            State.ERROR -> {
                Button(
                    onClick = {
                        filePickerLauncher.launch("text/comma-separated-values")
                    }) {
                    Text(text = viewModel.errorMessage, modifier = modifier)
                }
            }
            State.LEARNING -> {
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
                        translation = viewModel.vocabs[targetState].foreignWord,
                        input = input,
                        color = color,
                        wrongAnswer = viewModel.wrongAnswer,
                        learnMode = viewModel.learnMode,
                        showHint = viewModel.showHint,
                        onValueChange = {
                            input = it
                            viewModel.wrongAnswer = false
                        },
                        onGo = {
                            if (viewModel.learnMode) {
                                input = ""
                                viewModel.incrementIndex()
                            } else {
                                if (!viewModel.checkInput(input)) {
                                    input = ""
                                    viewModel.wrongAnswer = false
                                    viewModel.showHint = false
                                    viewModel.incrementIndex()
                                } else {
                                    if (viewModel.wrongAnswer) {
                                        viewModel.showHint = true
                                    } else {
                                        viewModel.wrongAnswer = true
                                    }
                                }
                            }
                        },
                        modifier = modifier
                    )
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
    translation: String,
    input: String,
    color: Color,
    learnMode: Boolean,
    wrongAnswer: Boolean,
    showHint: Boolean,
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
            translation = translation,
            learnMode = learnMode,
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
            ),
            placeholder = {
                if (showHint) {
                    Text(translation)
                }
            }
        )
        if (wrongAnswer) {
            Text(
                text = "Press Enter again to reveal",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ReviewCard(
    word: String,
    translation: String,
    learnMode: Boolean,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4F),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DefaultText(
                text = word,
                modifier = modifier
            )
            if (learnMode) {
                Text(
                    text = translation
                )
            }
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


@Composable
fun LabeledCheckbox(
    checked: Boolean,
    onCheckedChange: (() -> Unit),
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
//            .clip(MaterialTheme.shapes.small)
            .clickable(
                indication = rememberRipple(color = MaterialTheme.colorScheme.primary),
                interactionSource = remember { MutableInteractionSource() },
                onClick = { onCheckedChange() }
            )
            .requiredHeight(ButtonDefaults.MinHeight)
            .padding(4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null
        )

        Spacer(Modifier.size(6.dp))

        Text(
            text = label,
        )
    }
}