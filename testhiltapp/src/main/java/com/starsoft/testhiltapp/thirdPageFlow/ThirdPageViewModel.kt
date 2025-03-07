package com.starsoft.testhiltapp.thirdPageFlow

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.starsoft.skeleton.compose.controller.AppLevelActionController
import com.starsoft.skeleton.compose.controller.moveToTarget
import com.starsoft.skeleton.compose.controller.setKeyboardState
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.skeleton.compose.util.opposite
import com.starsoft.testhiltapp.rootFlow.RootFlowSharedViewModel.Companion.testRootFlowSharedViewModel
import com.starsoft.testhiltapp.rootFlow.SharedModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


/**
 * Created by Dmitry Starkin on 06.03.2025 18:42.
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

val MY_BACK_DATA_KEY ="com.starsoft.testapp.applicationflow.rootFlow.firstPageFlow.ThirdPageViewModel.backData"

@HiltViewModel
class ThirdPageViewModel @Inject constructor(
        private val rootFlowSharedViewModel: SharedModel
) : ViewModel(),  AppLevelActionController by rootFlowSharedViewModel
{
    
    companion object{
        val testThirdPageViewModel: ThirdPageViewModel @Composable
        get() = ThirdPageViewModel(testRootFlowSharedViewModel).also {
                it._uiState.value =
                    UiState("Test")
            }
    }
    
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState
    
    fun onUiAction(action: UiAction){
        when(action){
            UiAction.FirstButtonClicked -> setKeyboardState(currentKeyboardState.opposite)
            UiAction.SecondButtonClicked -> TODO()
            UiAction.ThirdButtonClicked -> TODO()
            UiAction.FourButtonClicked -> TODO()
            UiAction.FifeButtonClicked -> moveToTarget(Router.MoveBack())
        }
    }
}