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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


/**
 * Created by Dmitry Starkin on 26.02.2025 15:29.
 */
class CommonModelImpl(override val errorHandler: ErrorHandler, private val router: Router): ViewModel(), CommonModel,
    HostCreator by router{
    
    companion object{
        val testCommonViewModel: CommonModel @Composable
        get() = CommonModelImpl(ErrorHandler(LocalContext.current), RouterImpl())
    }
    
    private val mActivityLevelActionChannel = Channel<ActivityLevelAction>()
    override val activityLevelActionFlow: Flow<ActivityLevelAction> = mActivityLevelActionChannel.receiveAsFlow()
    
    private val mExternalActionChannel = Channel<ExternalEvent>()
    override val externalEventFlow: Flow<ExternalEvent> = mExternalActionChannel.receiveAsFlow()
    
    private val _backEventChannel = Channel<OnNavigateEvent>()
    override val navigationEventFlow: Flow<OnNavigateEvent> = _backEventChannel.receiveAsFlow()
    
    
    override var currentKeyboardState: KeyboardState = KeyboardState.Hidden
    
    
    init {
        router.commonModel = this
        initErrorObserver()
    }
    
    private fun initErrorObserver() {
        viewModelScope.launch {
            errorHandler.getError().collect { value ->
                performActivityLevelAction(obtainErrorMessageAction(value))
            }
        }
    }
    
    override fun performActivityLevelAction(event: ActivityLevelAction) {
        if(event is ActivityLevelAction.NavigationAction && event.rout.peekContent().destination.isExtendInterface(Router.ComposeDestination::class.java)){
            event.rout.getContentIfNotHandled()?.let{
                router.moveTo(it)
            }
        } else {
            viewModelScope.launch {
                Log.d("test","onGlobalAction send")
                mActivityLevelActionChannel.send(event)
            }
        }
    }
    
    override fun onExternalEvent(event: ExternalEvent) {
        viewModelScope.launch {
            Log.d("test","ExternalEvent send")
            mExternalActionChannel.send(event)
        }
    }
    
    override fun putNavigateEvent(event: OnNavigateEvent) {
        viewModelScope.launch {
            Log.d("test","BackDataEvent send")
            _backEventChannel.send(event)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        _backEventChannel.close()
        mExternalActionChannel.close()
        mActivityLevelActionChannel.close()
    }
}