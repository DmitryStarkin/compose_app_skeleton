package com.starsoft.skeleton.compose.controller

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.starsoft.skeleton.compose.controller.ActivityLevelAction.ErrorMessageAction.Companion.obtainErrorMessageAction
import com.starsoft.skeleton.compose.navigation.HostCreator
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.navigation.RouterImpl
import com.starsoft.skeleton.compose.transport.ErrorHandler
import com.starsoft.skeleton.compose.util.KeyboardState
import com.starsoft.skeleton.compose.util.isExtendInterface
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.jetbrains.annotations.MustBeInvokedByOverriders

/**
 * Created by Dmitry Starkin on 26.02.2025 15:29.
 */
open class AppLevelActionControllerImpl(override val errorHandler: ErrorHandler, private val router: Router): AppLevelActionController,
    HostCreator by router{
    
    companion object{
        val testAppLevelActionController: AppLevelActionController @Composable get() = AppLevelActionControllerImpl(ErrorHandler(LocalContext.current), RouterImpl())
    }
    
    private val scope = MainScope()
    
    private val _activityLevelActionFlow = MutableSharedFlow<ActivityLevelAction>()
    override val activityLevelActionFlow: SharedFlow<ActivityLevelAction> = _activityLevelActionFlow
    
    private val _externalActionFlow = MutableSharedFlow<ExternalEvent>()
    override val externalEventFlow: SharedFlow<ExternalEvent> = _externalActionFlow
    
    private val _navigationEventFlow = MutableSharedFlow<NavigationEvent>()
    override val navigationEventFlow: SharedFlow<NavigationEvent> = _navigationEventFlow
    
    override var currentKeyboardState: KeyboardState = KeyboardState.Hidden
    
    init {
        router.appLevelActionController = this
        initErrorObserver()
    }
    
    private fun initErrorObserver() {
        scope.launch {
            errorHandler.errorFlow.collect { value ->
                performActivityLevelAction(obtainErrorMessageAction(value))
            }
        }
    }
    
    override fun performActivityLevelAction(event: ActivityLevelAction) {
        if(event is ActivityLevelAction.NavigationAction && event.navigationTarget.peekContent().destination.isExtendInterface(Router.ComposeDestination::class.java)){
            event.navigationTarget.getContentIfNotHandled()?.let{
                router.moveTo(it)
            }
        } else {
            scope.launch {
                Log.d("test", "onGlobalAction send")
                _activityLevelActionFlow.emit(event)
            }
        }
    }
    
    override fun onExternalEvent(event: ExternalEvent) {
            Log.d("test","ExternalEvent send")
        scope.launch {
            _externalActionFlow.emit(event)
        }
    }
    
    override fun onBackPressed(target: String) {
        scope.launch {
            _navigationEventFlow.emit(NavigationEvent.BackPressed(target))
        }
    }
    
    override fun putNavigateEvent(event: NavigationEvent) {
            Log.d("test","NavigateEvent send")
        scope.launch {
            _navigationEventFlow.emit(event)
        }
    }
    
    @MustBeInvokedByOverriders
    override fun clean() {
        scope.cancel()
    }
}