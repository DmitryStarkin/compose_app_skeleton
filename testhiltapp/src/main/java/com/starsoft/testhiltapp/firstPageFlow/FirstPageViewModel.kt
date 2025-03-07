package com.starsoft.testhiltapp.firstPageFlow

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starsoft.skeleton.compose.controller.AppLevelActionController
import com.starsoft.skeleton.compose.controller.ExternalEvent
import com.starsoft.skeleton.compose.controller.NavigationEvent
import com.starsoft.skeleton.compose.controller.moveToTarget
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.testhiltapp.rootFlow.RootFlowSharedViewModel.Companion.testRootFlowSharedViewModel
import com.starsoft.testhiltapp.rootFlow.SharedModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by Dmitry Starkin on 06.03.2025 18:34.
 */
const val COUNT_START_VALUE = 60

sealed interface UiAction{
    data object FirstButtonClicked: UiAction
    data object SecondButtonClicked: UiAction
    data object ThirdButtonClicked: UiAction
    data object FourButtonClicked: UiAction
    data object FifeButtonClicked: UiAction
}


data class UiState(
        val baskText: String = EMPTY_STRING,
        val countDo: Int = COUNT_START_VALUE
)

val MY_BACK_DATA_KEY ="com.starsoft.testapp.applicationflow.rootFlow.firstPageFlow.FirstPageModel.backData"

@HiltViewModel
class FirstPageViewModel @Inject constructor(
        private val rootFlowSharedViewModel: SharedModel
) : ViewModel(),  AppLevelActionController by rootFlowSharedViewModel
{
    
    companion object{
        val testFirstPageViewModel: FirstPageViewModel @Composable
        get() = FirstPageViewModel(testRootFlowSharedViewModel).also {
                it._uiState.value =
                    UiState("Test", 60)
            }
    }
    
    init {
        Log.d("test","FirstPageViewModel init SharedViewModel ${rootFlowSharedViewModel.hashCode()} with commonModel ${rootFlowSharedViewModel.appLevelActionController.hashCode()} ")
        startNavigationEvents()
    }
    
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState
    
    fun onUiAction(action: UiAction){
        when(action){
            UiAction.FirstButtonClicked -> errorHandler.handleThrowable(Exception("TestException"))
            UiAction.SecondButtonClicked -> _uiState.value = _uiState.value.copy(countDo = _uiState.value.countDo + 1)
            UiAction.ThirdButtonClicked -> _uiState.value = _uiState.value.copy(countDo = _uiState.value.countDo - 1)
            UiAction.FourButtonClicked -> _uiState.value = _uiState.value.copy(baskText = "${System.currentTimeMillis()}")
            UiAction.FifeButtonClicked -> moveToTarget(Router.MoveBack())
        }
    }
    
    private fun startNavigationEvents(){
        viewModelScope.launch {
            navigationEventFlow.collect {
                when(it){
                    is NavigationEvent.BackDataEvent -> handleBackEvent(it)
                    is NavigationEvent.NavigateSusses -> handleNavigationEvent(it)
                    is NavigationEvent.BackPressed -> {
                        //TODO
                    }
                }
            }
        }
    }
    
    private fun handleExtEvent(event: ExternalEvent){
        Log.d("test","collected $event")
    }
    
    private fun handleBackEvent(event: NavigationEvent.BackDataEvent){
        Log.d("test","FirstPage collected BackDataEvent $event")
    }
    
    private fun handleNavigationEvent(event: NavigationEvent.NavigateSusses){
        Log.d("test","FirstPage collected OnNavigate $event")
        
    }
    
}