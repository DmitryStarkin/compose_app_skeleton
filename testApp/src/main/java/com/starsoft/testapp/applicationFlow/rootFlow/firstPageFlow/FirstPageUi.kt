package com.starsoft.testapp.applicationFlow.rootFlow.firstPageFlow

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavBackStackEntry
import com.starsoft.skeleton.compose.baseui.Counter
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.navigation.localDestinationClass
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.skeleton.compose.util.extractState
import com.starsoft.testapp.applicationFlow.rootFlow.RootActivity
import com.starsoft.testapp.applicationFlow.rootFlow.firstPageFlow.FirstPageViewModel.Companion.testFirstPageViewModel
import com.starsoft.testapp.utils.ktpViewModel


/**
 * Created by Dmitry Starkin on 27.02.2025 15:32.
 */

@Preview
@Composable
fun FirstPageUiPreview() {
    FirstPageUi(
        data = null,
        viewModel = testFirstPageViewModel
        )
}

class FirstPage : Router.ComposeScreen {
    
    override fun onCreate(owner: LifecycleOwner) {
        Log.d("test","FirstPage ${this.hashCode()} Created owner ${owner.hashCode()} ")
    }
    
    override fun onDestroy(owner: LifecycleOwner) {
        Log.d("test","FirstPage ${this.hashCode()} Destroy owner ${owner.hashCode()} ")
    }
    
    override val content: @Composable (NavBackStackEntry,  Bundle?) -> Unit = { _,  data ->
        Log.d("test","FirstPageUi called  owner ${LocalLifecycleOwner.current.hashCode()}")
        FirstPageUi(
            data = data,
            viewModel = ktpViewModel<FirstPageViewModel>(RootActivity::class)
        )
    }
}

@Composable
fun FirstPageUi(
        modifier: Modifier = Modifier,
        data: Bundle?,
        viewModel: FirstPageViewModel)
{
    Log.d("test","FirstPage obtained viewModel ${viewModel.hashCode()}")
    
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = localDestinationClass.current.simpleName ?: EMPTY_STRING
        )
        Count(
            viewModel.uiState.extractState {
                countDo
            }
        )
        Button(onClick = {
            viewModel.onUiAction(UiAction.FirstButtonClicked)
        }, modifier = modifier
            .width(200.dp)) {
            Text(text = "Show Error")
        }
        
        Button(onClick = {
            viewModel.onUiAction(UiAction.SecondButtonClicked)
        }, modifier = modifier
            .width(200.dp)
        ) {
            Text(text = "Increase count")
        }
        
        Button(onClick = {
            viewModel.onUiAction(UiAction.ThirdButtonClicked)
        }, modifier = modifier
            .width(200.dp)) {
            Text(text = "Decrease count")
        }
        
        Button(onClick = {
            viewModel.onUiAction(UiAction.FourButtonClicked)
        }, modifier = modifier
            .width(200.dp)) {
            Text(text = "Current time mills")
        }
        
        Button(onClick = {
            viewModel.onUiAction(UiAction.FifeButtonClicked)
        }, modifier = modifier
            .width(200.dp)) {
            Text(text = "Move back")
        }
        BottomText(
            viewModel.uiState.extractState { baskText }
        )
    }
}
@Composable
fun Count(
        uiState: State<Int>
){
    Log.d("test","Count called")
    Counter(
        remember = true,
        startValue = 0,
        endValue = uiState.value,
        delayMs = 1000L,
        onEnd = {Log.d("test","onEnd")}
    ){current, isCount ->
        if(isCount){
            Text(text = "$current count do ${uiState.value}")
        }
    }
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