package com.starsoft.testapp.applicationFlow.rootFlow

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starsoft.skeleton.compose.controller.NavigationEvent
import com.starsoft.skeleton.compose.controller.AppLevelActionController
import com.starsoft.skeleton.compose.controller.ExternalEvent
import com.starsoft.skeleton.compose.controller.moveToTarget
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.navigation.addPopUpOption
import com.starsoft.skeleton.compose.navigation.asTarget
import com.starsoft.skeleton.compose.navigation.asNavTarget
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

enum class BottomTab(val target: Router.Target, val icon: ImageVector, val label: String = EMPTY_STRING){
    FirstTab(FirstPage::class.java.asTarget(), Icons.Default.CheckCircle),
    SecondTab(SecondPage::class.java.asTarget(), Icons.Default.CheckCircle),
    ThirdTab(ThirdPage::class.java.asTarget(), Icons.Default.CheckCircle),
    FourTab(FourPage::class.java.asTarget(), Icons.Default.CheckCircle);
    val targetKey get() = target.targetKey
}

val targets = BottomTab.entries.map{
    it.target.targetKey
}

data class UiState(
        val currentTarget: String = EMPTY_STRING,
)

@InjectConstructor
class RootUIViewModel(
        private val rootFlowSharedViewModel: SharedModel
) : ViewModel(),  AppLevelActionController by rootFlowSharedViewModel
{
    companion object{
        val testRootUIViewModel: RootUIViewModel
            @Composable
        get() = RootUIViewModel(testRootFlowSharedViewModel).also {
                it._uiState.value =
                    UiState(BottomTab.FirstTab.targetKey)
            }
    }
    
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState
    
    init {
        Log.d("test","RootViewModel init SharedViewModel ${rootFlowSharedViewModel.hashCode()} with commonModel ${rootFlowSharedViewModel.appLevelActionController.hashCode()} ")
        startCollectExternalEvents()
        startNavigationEvents()
    }
    
    fun onUiAction(action: UiAction){
        when(action){
            is UiAction.OnBottomTabButtonClicked -> {
                moveToTarget(action.tab.target.asNavTarget().addPopUpOption(_uiState.value.currentTarget, inclusive = true, saveData = true))
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
            navigationEventFlow.collect {
                when(it){
                    is NavigationEvent.BackDataEvent -> handleBackEvent(it)
                    is NavigationEvent.NavigateSusses -> handleNavigationEvent(it)
                    is NavigationEvent.BackPressed -> {}
                }
            }
        }
    }
    
    private fun handleExtEvent(event: ExternalEvent){
        Log.d("test","collected $event")
    }
    
    private fun handleBackEvent(event: NavigationEvent.BackDataEvent){
        Log.d("test","root collected BackDataEvent $event")
    }
    
    private fun handleNavigationEvent(event: NavigationEvent.NavigateSusses){
        Log.d("test","root collected OnNavigate $event")
        if(event.reachedTarget in targets || event.reachedTarget.isEmpty()){
            _uiState.value =  _uiState.value.copy(
                currentTarget = event.reachedTarget
            )
        }
    }
   
}