package com.starsoft.skeleton.compose.util

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map


/**
 * Created by Dmitry Starkin on 05.03.2025 14:48.
 */

@Composable
fun <T> StateFlow<T>.extractBooleanState(default: Boolean = false, producer: T.() -> Boolean): State<Boolean> {
    return map{
        it.producer()
    } .collectAsState(default)
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun <T, R> StateFlow<T>.extractState(producer: T.() -> R): State<R> =
    map{
        it.producer()
    } .collectAsState(this.value.producer())
