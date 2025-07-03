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


package com.starsoft.skeleton.compose.navigation

import androidx.compose.runtime.Composable

/**
 * Created by Dmitry Starkin on 28.02.2025 14:40.
 */


class ListWrapper<T>(val content: List<T>): List<T> by content

fun <T> listOf(vararg elements: T): ListWrapper<T> = if (elements.isNotEmpty()) ListWrapper(elements.asList()) else ListWrapper(emptyList())

fun <T> ListWrapper<T>.toList(): List<T> = content

interface HostCreator {
    
    @Composable
    fun CreateNavHostHere(
            targets: List<Router.TargetProperties>
    ) = CreateNavHostHere( targets, null)
    
    @Composable
    fun CreateNavHostHere(
            targets: List<Router.TargetProperties>,
            startTarget: Router.Target?
    )
    
    @Composable
    fun CreateNavHostHere(
            targets: ListWrapper<Class<out Router.ComposeDestination>>
    ) = CreateNavHostHere(targets, null)
    
    @Composable
    fun CreateNavHostHere(
            targets: ListWrapper<Class<out Router.ComposeDestination>>,
            startTarget: Class<out Router.ComposeDestination>?
    )
}