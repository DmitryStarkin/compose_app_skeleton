package com.starsoft.testhiltapp.fourPageFlow

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavBackStackEntry
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.navigation.localDestinationClass
import com.starsoft.skeleton.compose.navigation.localScopeIdentifier
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.testhiltapp.fourPageFlow.FourPageViewModel.Companion.testFourPageViewModel


/**
 * Created by Dmitry Starkin on 06.03.2025 18:46.
 */
@Preview
@Composable
fun FourPageUiPreview() {
    FourPageUi(
        data = null,
        viewModel = testFourPageViewModel
    )
}

class FourPage : Router.ComposeScreen {
    
    override fun onDestroy(owner: LifecycleOwner) {
        Log.d("test","FourPage ${this.hashCode()} Destroy owner ${owner.hashCode()} ")
    }
    
    
    override val content: @Composable (NavBackStackEntry, Bundle?) -> Unit = { _, data ->
        Log.d("test","FourPageUi called  owner ${LocalLifecycleOwner.current.hashCode()}")
        FourPageUi(data = data)
    }
}

@Composable
fun FourPageUi(
        modifier: Modifier = Modifier,
        data: Bundle?,
        viewModel: FourPageViewModel  = hiltViewModel())
{
    Log.d("test","FourPage obtained viewModel ${viewModel.hashCode()}")
    
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
            Text(text = "Show Repo data")
        }
        
        Button(onClick = {
            viewModel.onUiAction(UiAction.SecondButtonClicked)
        }, modifier = modifier
            .width(200.dp)
        ) {
            Text(text = "Not impl")
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
        if(uiState.value.baskText.isNotEmpty() ){
            Text(
                text = uiState.value.baskText
            )
        }
    }
}