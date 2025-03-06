package com.starsoft.skeleton.compose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController


/**
 * Created by Dmitry Starkin on 28.02.2025 14:40.
 */


class ListWrapper<T>(val content: List<T>): List<T> by content

fun <T> listOf(vararg elements: T): ListWrapper<T> = if (elements.isNotEmpty()) ListWrapper(elements.asList()) else ListWrapper(emptyList())

fun <T> ListWrapper<T>.toList(): List<T> = content

interface HostCreator {
    
    @Composable
    fun CreateNavHostHere(
            navController: NavHostController,
            targets: List<Router.TargetProperties>
    ) = CreateNavHostHere(navController, targets, null)
    
    @Composable
    fun CreateNavHostHere(
            navController: NavHostController,
            targets: List<Router.TargetProperties>,
            startTarget: Router.Target?
    )
    
    @Composable
    fun CreateNavHostHere(
            navController: NavHostController,
            targets: ListWrapper<Class<out Router.ComposeDestination>>
    ) = CreateNavHostHere(navController, targets, null)
    
    @Composable
    fun CreateNavHostHere(
            navController: NavHostController,
            targets: ListWrapper<Class<out Router.ComposeDestination>>,
            startTarget: Class<out Router.ComposeDestination>?
    )
}