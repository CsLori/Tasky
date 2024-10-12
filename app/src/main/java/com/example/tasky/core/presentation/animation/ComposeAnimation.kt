package com.example.tasky.core.presentation.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

const val ENTER_TRANSITION_DURATION_500 = 500
const val EXIT_TRANSITION_DURATION_300 = 300
const val DELAY_DURATION_MILLIS_90 = 90

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> AnimateDropdownMenu(
    targetState: T,
    modifier: Modifier = Modifier,
    transitionSpec: AnimatedContentTransitionScope<T>.() -> ContentTransform = {
        fadeIn(animationSpec = tween(ENTER_TRANSITION_DURATION_500, DELAY_DURATION_MILLIS_90)) with
                fadeOut(
                    animationSpec = tween(
                        EXIT_TRANSITION_DURATION_300,
                        DELAY_DURATION_MILLIS_90
                    )
                )
    },
    contentAlignment: Alignment = Alignment.TopStart,
    label: String = "DropdownMenu",
    content: @Composable() AnimatedVisibilityScope.(targetState: T) -> Unit
) {
    val transition = updateTransition(targetState = targetState, label = label)
    transition.AnimatedContent(
        modifier,
        transitionSpec,
        contentAlignment,
        content = content
    )
}