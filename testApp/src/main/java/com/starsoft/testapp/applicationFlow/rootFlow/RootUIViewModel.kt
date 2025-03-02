package com.starsoft.testapp.applicationFlow.rootFlow

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starsoft.skeleton.compose.baseViewModel.OnNavigateEvent
import com.starsoft.skeleton.compose.baseViewModel.CommonModel
import com.starsoft.skeleton.compose.baseViewModel.ExternalEvent
import com.starsoft.skeleton.compose.baseViewModel.moveByRout
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.navigation.simpleProperties
import com.starsoft.skeleton.compose.navigation.simpleRout
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.testapp.applicationFlow.RootFlowSharedViewModel.Companion.testRootFlowSharedViewModel
import com.starsoft.testapp.applicationFlow.SharedModel
import com.starsoft.testapp.applicationFlow.rootFlow.firstPageFlow.FirstPage
import com.starsoft.testapp.applicationFlow.rootFlow.fourPageFlow.FourPage
import com.starsoft.testapp.applicationFlow.rootFlow.secondPageFlow.SecondPage
import com.starsoft.testapp.applicationFlow.rootFlow.thirdPageFlow.ThirdPage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.launch
import toothpick.InjectConstructor


/**
 * Created by Dmitry Starkin on 26.02.2025 16:26.
 */

sealed interface UiAction{
    data class OnBottomTabButtonClicked(val tab:  BottomTab): UiAction
}

enum class BottomTab(val destination: Router.DestinationProperties, val icon: ImageVector, val label: String = EMPTY_STRING){
    FirstTab(FirstPage::class.java.simpleProperties(), Icons.Default.CheckCircle),
    SecondTab(SecondPage::class.java.simpleProperties(), Icons.Default.CheckCircle),
    ThirdTab(ThirdPage::class.java.simpleProperties(), Icons.Default.CheckCircle),
    FourTab(FourPage::class.java.simpleProperties(), Icons.Default.CheckCircle);
    val target get() = destination.target
}

val targets = BottomTab.entries.map{
    it.destination.target
}

data class UiState(
        val currentTarget: String = EMPTY_STRING,
)

@InjectConstructor
class RootUIViewModel(
        private val rootFlowSharedViewModel: SharedModel
) : ViewModel(),  CommonModel by rootFlowSharedViewModel
{
    companion object{
        val testRootUIViewModel: RootUIViewModel
            @Composable
        get() = RootUIViewModel(testRootFlowSharedViewModel).also {
                it._uiState.value =
                    UiState(BottomTab.FirstTab.target)
            }
    }
    
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState
    
    init {
        Log.d("test","RootViewModel init SharedViewModel ${rootFlowSharedViewModel.hashCode()} with commonModel ${rootFlowSharedViewModel.commonModel.hashCode()} ")
        startCollectExternalEvents()
        startNavigationEvents()
        //onUiAction(UiAction.onBottomTabButtonClicked(BottomTab.FirstTab))
    }
    
    fun onUiAction(action: UiAction){
        when(action){
            is UiAction.OnBottomTabButtonClicked -> {
                moveByRout(action.tab.destination.simpleRout())
            }
        }
    }
    
    private fun startCollectExternalEvents(){
        viewModelScope.launch {
            Log.d("test","onGlobalAction send")
            externalEventFlow.collect{
                handleExtEvent(it)
            }
        }
    }
    
    private fun startNavigationEvents(){
        viewModelScope.launch {
            Log.d("test","onGlobalAction send")
            navigationEventFlow.collect{
                when(it){
                    is OnNavigateEvent.BackDataEvent -> handleBackEvent(it)
                    is OnNavigateEvent.OnNavigate -> handleNavigationEvent(it)
                }
            }
        }
    }
    
    private fun handleExtEvent(event: ExternalEvent){
        Log.d("test","collected $event")
    }
    
    private fun handleBackEvent(event: OnNavigateEvent.BackDataEvent){
        Log.d("test","root collected BackDataEvent $event")
    }
    
    private fun handleNavigationEvent(event: OnNavigateEvent.OnNavigate){
        Log.d("test","root collected OnNavigate $event")
        if(event.reachedTarget in targets){
            _uiState.value =  _uiState.value.copy(
                currentTarget = event.reachedTarget
            )
        }
    }
   
}