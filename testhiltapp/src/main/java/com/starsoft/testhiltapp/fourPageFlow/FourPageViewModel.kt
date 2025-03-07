package com.starsoft.testhiltapp.fourPageFlow

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.starsoft.skeleton.compose.controller.AppLevelActionController
import com.starsoft.skeleton.compose.controller.moveToTarget
import com.starsoft.skeleton.compose.controller.showMessage
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.testhiltapp.fourPageFlow.data.FourPageRepo
import com.starsoft.testhiltapp.rootFlow.RootFlowSharedViewModel.Companion.testRootFlowSharedViewModel
import com.starsoft.testhiltapp.rootFlow.SharedModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


/**
 * Created by Dmitry Starkin on 06.03.2025 18:47.
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

@HiltViewModel
class FourPageViewModel @Inject constructor(
        private val rootFlowSharedViewModel: SharedModel,
        private val repo: FourPageRepo
) : ViewModel(),  AppLevelActionController by rootFlowSharedViewModel
{
    
    companion object{
        val testFourPageViewModel: FourPageViewModel @Composable
        get() = FourPageViewModel(testRootFlowSharedViewModel, FourPageRepo(LocalContext.current)).also {
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
            UiAction.FifeButtonClicked -> moveToTarget(Router.MoveBack())
        }
    }
}