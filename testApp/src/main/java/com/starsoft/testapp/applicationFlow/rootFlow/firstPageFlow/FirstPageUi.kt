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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavBackStackEntry
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.navigation.localDestinationClass
import com.starsoft.skeleton.compose.navigation.localScopeIdentifier
import com.starsoft.skeleton.compose.navigation.localtarget
import com.starsoft.skeleton.compose.util.EMPTY_STRING
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
        FirstPageUi(data = data)
    }
}

@Composable
fun FirstPageUi(
        modifier: Modifier = Modifier,
        data: Bundle?,
        viewModel: FirstPageViewModel = ktpViewModel<FirstPageViewModel>(RootActivity::class))
{
    Log.d("test","FirstPage obtained viewModel ${viewModel.hashCode()}")
    
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
        Text(
            text = localtarget.current.hashCode().toString()
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
            Text(text = "Move next")
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
            Text(text = "Open dialog")
        }
        
        Button(onClick = {
            viewModel.onUiAction(UiAction.FifeButtonClicked)
        }, modifier = modifier
            .width(200.dp)) {
            Text(text = "Move back")
        }
        if(uiState.value.baskText.isNotEmpty() ){
            Text(
                text = uiState.value.baskText
            )
        }
    }
}