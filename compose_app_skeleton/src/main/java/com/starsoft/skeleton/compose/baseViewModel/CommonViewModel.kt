package com.starsoft.skeleton.compose.baseViewModel

import android.os.Bundle
import com.starsoft.skeleton.compose.navigation.HostCreator
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.transport.ErrorHandler
import com.starsoft.skeleton.compose.transport.Event
import com.starsoft.skeleton.compose.util.KeyboardState
import com.starsoft.skeleton.compose.util.KeyedData
import kotlinx.coroutines.flow.Flow


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
    data class NavigationAction(val rout: Event<Router.Rout>): ActivityLevelAction{
        companion object{
            fun obtainNavigationAction(rout: Router.Rout): NavigationAction =
                NavigationAction(Event(rout))
        }
    }
    
    data class KeyboardAction(val keyboardState: KeyboardState): ActivityLevelAction
}

sealed interface ExternalEvent{
    data class KeyedDataEvent(val data: KeyedData): ExternalEvent {
        companion object{
            fun obtainKeyedDataAction(key: String, data: Bundle): KeyedDataEvent =
                KeyedDataEvent(KeyedData(key, data))
        }
    }
}

sealed interface OnNavigateEvent{
    data class BackDataEvent(val data: KeyedData): OnNavigateEvent {
        companion object{
            fun obtainBackKeyedDataEvent(key: String, data: Bundle): BackDataEvent =
                BackDataEvent(KeyedData(key, data))
        }
    }
    data class OnNavigate(val reachedTarget: String): OnNavigateEvent
}

interface CommonModel: HostCreator {
    val errorHandler: ErrorHandler
    val activityLevelActionFlow: Flow<ActivityLevelAction>
    val externalEventFlow: Flow<ExternalEvent>
    val navigationEventFlow: Flow<OnNavigateEvent>
    
    var currentKeyboardState: KeyboardState
    
    fun performActivityLevelAction(event: ActivityLevelAction)
    
    fun onExternalEvent(event: ExternalEvent)
    
    fun putNavigateEvent(event: OnNavigateEvent)
}

interface CommonModelOwner{
    var commonModel: CommonModel
}
