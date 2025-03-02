package com.starsoft.skeleton.compose.transport


/**
 * Created by Dmitry Starkin on 26.02.2025 15:22.
 */
class Event<out T>(private val content: T) {
    
    var hasBeenHandled = false
        private set // Allow external read but not write
    
    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
    
    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}