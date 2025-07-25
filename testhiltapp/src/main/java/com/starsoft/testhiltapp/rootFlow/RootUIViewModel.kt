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

package com.starsoft.testhiltapp.rootFlow

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starsoft.skeleton.compose.controller.AppLevelActionController
import com.starsoft.skeleton.compose.controller.ExternalEvent
import com.starsoft.skeleton.compose.controller.NavigationEvent
import com.starsoft.skeleton.compose.controller.moveToTarget
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.navigation.addPopUpOption
import com.starsoft.skeleton.compose.navigation.asTarget
import com.starsoft.skeleton.compose.navigation.asNavTarget
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.testhiltapp.firstPageFlow.FirstPage
import com.starsoft.testhiltapp.fourPageFlow.FourPage
import com.starsoft.testhiltapp.rootFlow.RootFlowSharedViewModel.Companion.testRootFlowSharedViewModel
import com.starsoft.testhiltapp.secondPageFlow.SecondPage
import com.starsoft.testhiltapp.thirdPageFlow.ThirdPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by Dmitry Starkin on 06.03.2025 16:37.
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

@HiltViewModel
class RootUIViewModel @Inject constructor(
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