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