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

import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry


/**
 * Created by Dmitry Starkin on 11.03.2025 12:24.
 */
abstract class ComposeScreenWithLifeCycle: Router.ComposeScreen, LifecycleOwner {
    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    
    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
    }
    @CallSuper
    override fun onCreate(owner: LifecycleOwner) {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }
    @CallSuper
    override fun onStop(owner: LifecycleOwner) {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }
    @CallSuper
    override fun onStart(owner: LifecycleOwner) {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }
    @CallSuper
    override fun onResume(owner: LifecycleOwner) {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }
    @CallSuper
    override fun onPause(owner: LifecycleOwner) {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }
    @CallSuper
    override fun onDestroy(owner: LifecycleOwner) {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
}

abstract class ComposeDialogWithLifeCycle:  Router.ComposeDialog, LifecycleOwner{
    
    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    
    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
    }
    @CallSuper
    override fun onCreate(owner: LifecycleOwner) {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }
    @CallSuper
    override fun onStop(owner: LifecycleOwner) {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }
    @CallSuper
    override fun onStart(owner: LifecycleOwner) {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }
    @CallSuper
    override fun onResume(owner: LifecycleOwner) {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }
    @CallSuper
    override fun onPause(owner: LifecycleOwner) {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }
    @CallSuper
    override fun onDestroy(owner: LifecycleOwner) {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
}