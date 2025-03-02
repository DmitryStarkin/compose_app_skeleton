package com.starsoft.testapp.applicationFlow.rootFlow.secondPageFlow

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.starsoft.skeleton.compose.baseViewModel.CommonModel
import com.starsoft.skeleton.compose.baseViewModel.moveByRout
import com.starsoft.skeleton.compose.baseViewModel.showMessage
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.testapp.applicationFlow.RootFlowSharedViewModel
import com.starsoft.testapp.applicationFlow.RootFlowSharedViewModel.Companion.testRootFlowSharedViewModel
import com.starsoft.testapp.applicationFlow.SharedModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import toothpick.InjectConstructor


/**
 * Created by Dmitry Starkin on 28.02.2025 12:40.
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

val MY_BACK_DATA_KEY ="com.starsoft.testapp.applicationflow.rootFlow.secondPageFlow.SecondPageViewModel.backData"

@InjectConstructor
class SecondPageViewModel(
        private val rootFlowSharedViewModel: SharedModel
) : ViewModel(),  CommonModel by rootFlowSharedViewModel
{
    
    companion object{
        val testSecondPageViewModel: SecondPageViewModel @Composable
        get() = SecondPageViewModel(testRootFlowSharedViewModel).also {
                it._uiState.value =
                    UiState("Test")
            }
    }
    
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState
    
    fun onUiAction(action: UiAction){
        when(action){
            UiAction.FirstButtonClicked -> showMessage("Test message")
            UiAction.SecondButtonClicked -> TODO()
            UiAction.ThirdButtonClicked -> TODO()
            UiAction.FourButtonClicked -> TODO()
            UiAction.FifeButtonClicked -> moveByRout(Router.MoveBack())
        }
    }
}