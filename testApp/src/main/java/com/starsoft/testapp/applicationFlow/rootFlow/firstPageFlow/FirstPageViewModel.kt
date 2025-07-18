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

package com.starsoft.testapp.applicationFlow.rootFlow.firstPageFlow

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
import com.starsoft.testapp.applicationFlow.RootFlowSharedViewModel.Companion.testRootFlowSharedViewModel
import com.starsoft.testapp.applicationFlow.SharedModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import toothpick.InjectConstructor


/**
 * Created by Dmitry Starkin on 27.02.2025 15:33.
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

@InjectConstructor
class FirstPageViewModel(
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

