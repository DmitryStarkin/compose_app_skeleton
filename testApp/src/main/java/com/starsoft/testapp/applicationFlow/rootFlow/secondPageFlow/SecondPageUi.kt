package com.starsoft.testapp.applicationFlow.rootFlow.secondPageFlow

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavBackStackEntry
import com.starsoft.skeleton.compose.baseui.CircularProgressSpinner
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.navigation.localDestinationClass
import com.starsoft.skeleton.compose.navigation.localScopeIdentifier
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.skeleton.compose.util.extractState
import com.starsoft.testapp.applicationFlow.rootFlow.RootActivity
import com.starsoft.testapp.applicationFlow.rootFlow.secondPageFlow.SecondPageViewModel.Companion.testSecondPageViewModel
import com.starsoft.testapp.utils.ktpViewModel


/**
 * Created by Dmitry Starkin on 28.02.2025 12:40.
 */
@Preview
@Composable
fun SecondPageUiPreview() {
    SecondPageUi(
        data = null,
        viewModel = testSecondPageViewModel
    )
}

class SecondPage : Router.ComposeScreen {
    
    override fun onCreate(owner: LifecycleOwner) {
        Log.d("test","SecondPage ${this.hashCode()} Created owner ${owner.hashCode()} ")
    }
    
    override fun onDestroy(owner: LifecycleOwner) {
        Log.d("test","SecondPage ${this.hashCode()} Destroy owner ${owner.hashCode()} ")
    }
    
    
    override val content: @Composable (NavBackStackEntry, Bundle?) -> Unit = { _, data ->
        Log.d("test","SecondPageUi called  owner ${LocalLifecycleOwner.current.hashCode()}")
        SecondPageUi(
            data = data,
            viewModel = ktpViewModel<SecondPageViewModel>(RootActivity::class)
        )
    }
}

@Composable
fun SecondPageUi(
        modifier: Modifier = Modifier,
        data: Bundle?,
        viewModel: SecondPageViewModel)
{
    Log.d("test","SecondPage obtained viewModel ${viewModel.hashCode()}")
    
    val uiState = viewModel.uiState.collectAsState()
    
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = localDestinationClass.current.simpleName ?: EMPTY_STRING
        )
        Text(
            text = localScopeIdentifier.current.hashCode().toString()
        )
        Button(onClick = {
            viewModel.onUiAction(UiAction.FirstButtonClicked)
        }, modifier = modifier
            .width(200.dp)) {
            Text(text = "Show Message")
        }
        
        Button(onClick = {
            viewModel.onUiAction(UiAction.SecondButtonClicked)
        }, modifier = modifier
            .width(200.dp)
        ) {
            Text(text = "Show Spinner")
        }
        
        Button(onClick = {
            viewModel.onUiAction(UiAction.ThirdButtonClicked)
        }, modifier = modifier
            .width(200.dp)) {
            Text(text = "Not impl")
        }
        
        Button(onClick = {
            viewModel.onUiAction(UiAction.FourButtonClicked)
        }, modifier = modifier
            .width(200.dp)) {
            Text(text = "Not impl")
        }
        
        Button(onClick = {
            viewModel.onUiAction(UiAction.FifeButtonClicked)
        }, modifier = modifier
            .width(200.dp)) {
            Text(text = "Move back")
        }
        BottomText(
            viewModel.uiState.extractState {
               baskText
            }
        )
    }

    CircularProgressSpinner( viewModel.uiState.extractState { spinnerVisibility })
}

@Composable
fun BottomText(
        textState: State<String>
){
    Log.d("test","BottomText called")
    
    if(textState.value.isNotEmpty() ){
        Text(
            text = "curremt time ${textState.value}"
        )
    }
}