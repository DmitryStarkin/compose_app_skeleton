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