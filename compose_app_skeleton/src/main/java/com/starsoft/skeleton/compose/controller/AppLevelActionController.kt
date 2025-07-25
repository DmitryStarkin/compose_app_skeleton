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


package com.starsoft.skeleton.compose.controller

import android.os.Bundle
import com.starsoft.skeleton.compose.navigation.HostCreator
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.transport.ErrorHandler
import com.starsoft.skeleton.compose.transport.Event
import com.starsoft.skeleton.compose.util.KeyboardState
import com.starsoft.skeleton.compose.util.KeyedData
import kotlinx.coroutines.flow.SharedFlow


/**
 * Created by Dmitry Starkin on 26.02.2025 15:17.
 *
 */
sealed interface ActivityLevelAction{
    data class MessageAction(val message: Event<String>): ActivityLevelAction {
        companion object{
            fun obtainMessageAction(text: String): MessageAction =
                MessageAction(Event(text))
        }
    }
    data class ErrorMessageAction(val message: Event<String>): ActivityLevelAction {
        companion object{
            fun obtainErrorMessageAction(text: String): ErrorMessageAction =
                ErrorMessageAction(Event(text))
        }
    }
    data class NavigationAction(val navigationTarget: Event<Router.NavigationTarget>): ActivityLevelAction{
        companion object{
            fun obtainNavigationAction(navigationTarget: Router.NavigationTarget): NavigationAction =
                NavigationAction(Event(navigationTarget))
        }
    }
    
    data class KeyboardAction(val keyboardState: KeyboardState, val marker: Long): ActivityLevelAction{
        companion object{
            fun obtainKeyboardAction(keyboardState: KeyboardState): KeyboardAction =
                KeyboardAction(keyboardState, System.currentTimeMillis() )
        }
    }
}

sealed interface ExternalEvent{
    data class KeyedDataEvent(val data: KeyedData): ExternalEvent {
        companion object{
            fun obtainKeyedDataAction(key: String, data: Bundle): KeyedDataEvent =
                KeyedDataEvent(KeyedData(key, data))
        }
    }
}

sealed interface NavigationEvent{
    data class BackDataEvent(val data: KeyedData): NavigationEvent {
        companion object{
            fun obtainBackKeyedDataEvent(key: String, data: Bundle): BackDataEvent =
                BackDataEvent(KeyedData(key, data))
        }
    }
    data class NavigateSusses(val reachedTarget: String): NavigationEvent
    data class BackPressed(val currentTarget: String): NavigationEvent
}

interface AppLevelActionController: HostCreator {
    val errorHandler: ErrorHandler
    val activityLevelActionFlow: SharedFlow<ActivityLevelAction>
    val externalEventFlow: SharedFlow<ExternalEvent>
    val navigationEventFlow: SharedFlow<NavigationEvent>
    
    var currentKeyboardState: KeyboardState
    
    fun performActivityLevelAction(event: ActivityLevelAction)
    
    fun onExternalEvent(event: ExternalEvent)
    
    fun onBackPressed(target: String)
    
    fun putNavigateEvent(event: NavigationEvent)
    
    fun clean()
}

interface CommonModelOwner{
    var appLevelActionController: AppLevelActionController
}
