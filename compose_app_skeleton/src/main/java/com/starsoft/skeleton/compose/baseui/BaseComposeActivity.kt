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

package com.starsoft.skeleton.compose.baseui

import android.app.Activity
import android.app.Service
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.res.colorResource
import com.starsoft.skeleton.compose.R
import com.starsoft.skeleton.compose.controller.AppLevelActionController
import com.starsoft.skeleton.compose.controller.CommonModelOwner
import com.starsoft.skeleton.compose.controller.ActivityLevelAction
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.navigation.getRFinishFlag
import com.starsoft.skeleton.compose.navigation.moveToActivity
import com.starsoft.skeleton.compose.navigation.moveToService
import com.starsoft.skeleton.compose.navigation.openWebLink
import com.starsoft.skeleton.compose.navigation.tryStopAsService
import com.starsoft.skeleton.compose.transport.Event
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.skeleton.compose.util.KeyboardListener
import com.starsoft.skeleton.compose.util.hideKeyboardByIMS
import com.starsoft.skeleton.compose.util.isExtendInterface
import com.starsoft.skeleton.compose.util.isInstanceOrExtend
import com.starsoft.skeleton.compose.util.keyboardStateByVisibility
import com.starsoft.skeleton.compose.util.showKeyboardByIMS


/**
 * Created by Dmitry Starkin on 26.02.2025 15:15.
 */
abstract class BaseComposeActivity: ComponentActivity(), CommonModelOwner {
    
    override lateinit var  appLevelActionController: AppLevelActionController
    
    abstract fun obtainAppLevelActionController(): AppLevelActionController
    
    @Composable
    abstract fun SetRootUi()
    
    private lateinit var keyboardListener: KeyboardListener
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appLevelActionController = obtainAppLevelActionController()
        Log.d("test","obtained RootFlowSharedViewModel ${(appLevelActionController).hashCode()}")
        keyboardListener = KeyboardListener(this, this) { isVisible ->
            Log.d("test","keyboardListener keyboard $isVisible")
            appLevelActionController.currentKeyboardState = keyboardStateByVisibility(isVisible)
        }
        setContent {
            val event = appLevelActionController.activityLevelActionFlow.collectAsState(ActivityLevelAction.MessageAction(Event(EMPTY_STRING)))
            SetRootUi()
            HandleGlobalActions(event)
        }
    }
    
    @Composable
    open fun HandleApplicationRout(navigationTarget: Event<Router.NavigationTarget>){
        navigationTarget.getContentIfNotHandled()?.let {
            Log.d("test","HandleApplicationRout $it")
            if(it.destination.isExtendInterface(Router.ComposeDestination::class.java)){
                throw(Exception("must handled in common model"))
            } else {
                moveToApplicationRout(it)
            }
        }
    }
    
    @Composable
    open fun HandleMessage(message: Event<String>){
        Log.d("test","HandleMessage ${message} ")
        val snackBarHostState = remember { SnackbarHostState() }
        message.getContentIfNotHandled()?.let {
            if(it.isNotEmpty()){
                LaunchedEffect(key1 = message)
                {
                    snackBarHostState.showSnackbar(
                        it,
                        duration = SnackbarDuration.Short
                    )
                }
                SnackbarHost(
                    hostState = snackBarHostState,
                ){data ->
                    Snackbar(data)
                }
            }
        }
    }
    
    @Composable
    open fun HandleErrorMessage(message: Event<String>){
        Log.d("test","HandleErrorMessage ${message} ")
        val snackBarHostState = remember { SnackbarHostState() }
        message.getContentIfNotHandled()?.let {
            if(it.isNotEmpty()){
                LaunchedEffect(key1 = message)
                {
                    snackBarHostState.showSnackbar(
                        it,
                        duration = SnackbarDuration.Short
                    )
                }
                SnackbarHost(
                    hostState = snackBarHostState,
                ){data ->
                    Snackbar(
                        data,
                        backgroundColor = colorResource(id = R.color.alert)
                    )
                }
            }
        }
    }
    
    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun HandleGlobalActions(event: State<ActivityLevelAction>){
        Log.d("test","HandleGlobalActions ${event} ")
        when(val action = event.value) {
            is ActivityLevelAction.NavigationAction -> {
                HandleApplicationRout(action.navigationTarget)
            }
            
            is ActivityLevelAction.MessageAction -> {
                HandleMessage(action.message)
            }
            
            is ActivityLevelAction.ErrorMessageAction -> {
                HandleErrorMessage(action.message)
            }
            
            is ActivityLevelAction.KeyboardAction -> {
                
                Log.d("test", "keyboard current ${appLevelActionController.currentKeyboardState} ")
                if (action.keyboardState.visible) {
                Log.d("test", "try show keyboard current ${WindowInsets.isImeVisible} ")
                //LocalSoftwareKeyboardController.current?.show()
                showKeyboardByIMS()
            } else {
                Log.d("test", "try hide keyboardcurrent ${WindowInsets.isImeVisible} ")
                //LocalSoftwareKeyboardController.current?.hide()
                hideKeyboardByIMS()
            }
        }
        }
    }
    
    
    private fun moveToApplicationRout(navigationTarget: Router.NavigationTarget, data: Bundle? = null){
        Log.d("test","moveTo ${navigationTarget.destination.name}")
        if(navigationTarget is Router.NavigationTarget.NavigationTargetStub) {
            return
        }
        
        val unionData = navigationTarget.data?.let {
            data?.apply {
                it.putAll(this)
            }
            it
        } ?: data
        
        when {
            
            navigationTarget.destination.isInstanceOrExtend(Router.Close::class.java) -> {
                this.finish()
            }
            
            navigationTarget.destination.isInstanceOrExtend(Activity::class.java) -> {
                this.moveToActivity(navigationTarget.destination, unionData)
            }
            
            navigationTarget.destination.isInstanceOrExtend(Service::class.java) -> {
                this.moveToService(navigationTarget.destination, unionData)
            }
            
            navigationTarget.destination.isInstanceOrExtend(Router.StopService::class.java) -> {
                this.tryStopAsService(navigationTarget.destination)
            }
            
            navigationTarget is Router.OpenLink -> {
                this.openWebLink(navigationTarget.link)
            }
        }
        
        if (data.getRFinishFlag()) {
            this.finish()
        }
    }
}