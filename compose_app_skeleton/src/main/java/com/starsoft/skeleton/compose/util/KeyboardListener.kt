package com.starsoft.skeleton.compose.util

import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner


/**
 * Created by Dmitry Starkin on 28.02.2025 13:36.
 */

class KeyboardListener(
        private var activity: ComponentActivity?,
        lifecycleOwner: LifecycleOwner,
        private val dispatchStartState: Boolean = true,
        private var callback: ((isOpen: Boolean) -> Unit)?
) : DefaultLifecycleObserver {
    
    private val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
        private var lastState: Boolean = activity?.getRootView()?.isKeyboardVisible() ?: false
        
        override fun onGlobalLayout() {
            val isOpen = activity?.getRootView()?.isKeyboardVisible() ?: false
            if (isOpen == lastState) {
                return
            } else {
                dispatchKeyboardEvent(isOpen)
                lastState = isOpen
            }
        }
    }
    
    init {
        // Make the component lifecycle aware
        lifecycleOwner.lifecycle.addObserver(this)
    }
    
    override fun onResume(owner: LifecycleOwner) {
        
        if(dispatchStartState){
            // Dispatch the current state of the keyboard
            dispatchKeyboardEvent(activity?.getRootView()?.isKeyboardVisible() ?: false)
        }
        registerKeyboardListener()
    }
    
    override fun onPause(owner: LifecycleOwner) {
        unregisterKeyboardListener()
    }
    
    override fun onDestroy(owner: LifecycleOwner) {
        activity = null
        callback = null
        owner.lifecycle.removeObserver(this)
    }
    private fun registerKeyboardListener() {
        activity?.getRootView()?.viewTreeObserver?.addOnGlobalLayoutListener(listener)
    }
    
    private fun dispatchKeyboardEvent(isOpen: Boolean) {
        when {
            isOpen  -> callback?.invoke(true)
            !isOpen -> callback?.invoke(false)
        }
    }
    
    private fun unregisterKeyboardListener() {
        activity?.getRootView()?.viewTreeObserver?.removeOnGlobalLayoutListener(listener)
    }
}