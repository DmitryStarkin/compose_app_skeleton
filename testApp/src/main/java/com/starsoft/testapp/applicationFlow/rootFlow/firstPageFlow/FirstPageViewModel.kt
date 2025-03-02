package com.starsoft.testapp.applicationFlow.rootFlow.firstPageFlow

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starsoft.skeleton.compose.baseViewModel.CommonModel
import com.starsoft.skeleton.compose.baseViewModel.ExternalEvent
import com.starsoft.skeleton.compose.baseViewModel.OnNavigateEvent
import com.starsoft.skeleton.compose.baseViewModel.moveByRout
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.testapp.applicationFlow.RootFlowSharedViewModel
import com.starsoft.testapp.applicationFlow.RootFlowSharedViewModel.Companion.testRootFlowSharedViewModel
import com.starsoft.testapp.applicationFlow.SharedModel
import com.starsoft.testapp.applicationFlow.rootFlow.targets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import toothpick.InjectConstructor


/**
 * Created by Dmitry Starkin on 27.02.2025 15:33.
 */

sealed interface UiAction{
   
    data object FirstButtonClicked: UiAction
    data object SecondButtonClicked: UiAction
    data object ThirdButtonClicked: UiAction
    data object FourButtonClicked: UiAction
    data object FifeButtonClicked: UiAction
}


data class UiState(
        val baskText: String = EMPTY_STRING,
)

val MY_BACK_DATA_KEY ="com.starsoft.testapp.applicationflow.rootFlow.firstPageFlow.FirstPageModel.backData"

@InjectConstructor
class FirstPageViewModel(
        private val rootFlowSharedViewModel: SharedModel
) : ViewModel(),  CommonModel by rootFlowSharedViewModel
{
    
    companion object{
       val testFirstPageViewModel: FirstPageViewModel @Composable
       get() = FirstPageViewModel(testRootFlowSharedViewModel).also {
           it._uiState.value =
               UiState("Test")
           }
    }
    
    init {
        Log.d("test","FirstPageViewModel init SharedViewModel ${rootFlowSharedViewModel.hashCode()} with commonModel ${rootFlowSharedViewModel.commonModel.hashCode()} ")
        startNavigationEvents()
    }
    
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState
    
    fun onUiAction(action: UiAction){
        when(action){
                UiAction.FirstButtonClicked -> errorHandler.handleThrowable(Exception("TestException"))
                UiAction.SecondButtonClicked -> TODO()
                UiAction.ThirdButtonClicked -> TODO()
                UiAction.FourButtonClicked -> TODO()
                UiAction.FifeButtonClicked -> moveByRout(Router.MoveBack())
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
        Log.d("test","FirstPage collected BackDataEvent $event")
    }
    
    private fun handleNavigationEvent(event: OnNavigateEvent.OnNavigate){
        Log.d("test","FirstPage collected OnNavigate $event")
        
    }
    
    }

