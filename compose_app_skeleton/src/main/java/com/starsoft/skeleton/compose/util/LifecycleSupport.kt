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

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner


/**
 * Created by Dmitry Starkin on 26.02.2025 14:52.
 */
/**
 * Provides lifecycle support, classes implementing this interface
 * must provide work completion in the finalize()[finalizeTask] method
 */

interface LifecycleSupport : DefaultLifecycleObserver {
    
    /**
     * Starts tracking of the life cycle
     * in this case task will be completed in onDestroy
     * @param owner LifecycleOwner
     * (for example [Fragment][androidx.fragment.app.Fragment]
     * or [FragmentActivity][androidx.fragment.app.FragmentActivity])
     */
    fun connectToLifecycle(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(this)
    }
    
    /**
     * Stop tracking of the life cycle
     * @param owner LifecycleOwner
     * (for example [Fragment][androidx.fragment.app.Fragment]
     * or [FragmentActivity][androidx.fragment.app.FragmentActivity])
     */
    fun disconnectFromLifecycle(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
    }
    
    /**
     * @suppress
     * */
    override fun onDestroy(owner: LifecycleOwner) {
        disconnectFromLifecycle(owner)
        finalizeTask()
    }
    
    /**
     * Finalized task
     * Classes implementing this interface
     * must provide work completion in this method
     */
    fun finalizeTask()
}