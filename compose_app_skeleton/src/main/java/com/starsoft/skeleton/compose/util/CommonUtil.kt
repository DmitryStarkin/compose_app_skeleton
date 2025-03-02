package com.starsoft.skeleton.compose.util


/**
 * Created by Dmitry Starkin on 26.02.2025 14:59.
 */
const val EMPTY_STRING = ""

fun <T> Collection<T>.containsAnyItemFrom(other: Collection<T>): Boolean =
    run breaking@{
        other.forEach {
            if (it in this) return@breaking true
        }
        false
    }
    
