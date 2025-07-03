/*
 * Copyright (c) 2025. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the «License»);
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  //www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an «AS IS» BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */



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
