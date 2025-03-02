package com.starsoft.testapp.applicationFlow.rootFlow.fourPageFlow

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
import com.starsoft.testapp.applicationFlow.rootFlow.RootActivity
import com.starsoft.testapp.applicationFlow.rootFlow.firstPageFlow.data.FourPageRepo
import com.starsoft.testapp.utils.KTPAutoScopeCloseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import toothpick.InjectConstructor
import toothpick.ktp.KTP


/**
 * Created by Dmitry Starkin on 28.02.2025 14:01.
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

val MY_BACK_DATA_KEY ="com.starsoft.testapp.applicationflow.rootFlow.firstPageFlow.Four.backData"

@InjectConstructor
class FourPageViewModel(
        private val rootFlowSharedViewModel: SharedModel,
        private val repo: FourPageRepo
) : KTPAutoScopeCloseViewModel(),  CommonModel by rootFlowSharedViewModel
{
    
    companion object{
        val testFourPageViewModel: FourPageViewModel @Composable
        get() = FourPageViewModel(testRootFlowSharedViewModel, FourPageRepo()).also {
                it._uiState.value =
                    UiState("Test")
            }
    }
    
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState
    
    fun onUiAction(action: UiAction){
        when(action){
            UiAction.FirstButtonClicked -> showMessage(repo.stringData)
            UiAction.SecondButtonClicked -> TODO()
            UiAction.ThirdButtonClicked -> TODO()
            UiAction.FourButtonClicked -> TODO()
            UiAction.FifeButtonClicked -> moveByRout(Router.MoveBack())
        }
    }
}