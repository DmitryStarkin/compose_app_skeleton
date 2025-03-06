package com.starsoft.skeleton.compose.baseViewModel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starsoft.skeleton.compose.baseViewModel.ActivityLevelAction.ErrorMessageAction.Companion.obtainErrorMessageAction
import com.starsoft.skeleton.compose.navigation.HostCreator
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.navigation.RouterImpl
import com.starsoft.skeleton.compose.transport.ErrorHandler
import com.starsoft.skeleton.compose.util.KeyboardState
import com.starsoft.skeleton.compose.util.isExtendInterface
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * Created by Dmitry Starkin on 26.02.2025 15:29.
 */
class CommonModelImpl(override val errorHandler: ErrorHandler, private val router: Router): ViewModel(), CommonModel,
    HostCreator by router{
    
    companion object{
        val testCommonViewModel: CommonModel @Composable get() = CommonModelImpl(ErrorHandler(LocalContext.current), RouterImpl())
    }
    
    private val _activityLevelActionFlow = MutableSharedFlow<ActivityLevelAction>()
    override val activityLevelActionFlow: SharedFlow<ActivityLevelAction> = _activityLevelActionFlow
    
    private val _externalActionFlow = MutableSharedFlow<ExternalEvent>()
    override val externalEventFlow: SharedFlow<ExternalEvent> = _externalActionFlow
    
    private val _navigationEventFlow = MutableSharedFlow<NavigationEvent>()
    override val navigationEventFlow: SharedFlow<NavigationEvent> = _navigationEventFlow
    
    override var currentKeyboardState: KeyboardState = KeyboardState.Hidden
    
    init {
        router.commonModel = this
        initErrorObserver()
    }
    
    private fun initErrorObserver() {
        viewModelScope.launch {
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
            viewModelScope.launch {
                Log.d("test", "onGlobalAction send")
                _activityLevelActionFlow.emit(event)
            }
        }
    }
    
    override fun onExternalEvent(event: ExternalEvent) {
            Log.d("test","ExternalEvent send")
        viewModelScope.launch {
            _externalActionFlow.emit(event)
        }
    }
    
    override fun onBackPressed(target: String) {
        viewModelScope.launch {
            _navigationEventFlow.emit(NavigationEvent.BackPressed(target))
        }
    }
    
    override fun putNavigateEvent(event: NavigationEvent) {
            Log.d("test","NavigateEvent send")
        viewModelScope.launch {
            _navigationEventFlow.emit(event)
        }
    }
}