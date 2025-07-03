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

package com.starsoft.testapp.applicationFlow.rootFlow.thirdPageFlow

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
import com.starsoft.skeleton.compose.navigation.localAppLevelActionController
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.testapp.applicationFlow.rootFlow.RootActivity
import com.starsoft.testapp.applicationFlow.rootFlow.thirdPageFlow.ThirdPageViewModel.Companion.testThirdPageViewModel
import com.starsoft.testapp.utils.ktpViewModel


/**
 * Created by Dmitry Starkin on 28.02.2025 12:52.
 */
@Preview
@Composable
fun ThirdPageUiPreview() {
    ThirdPageUi(
        data = null,
        viewModel = testThirdPageViewModel
    )
}

class ThirdPage : Router.ComposeScreen {
    
    override fun onCreate(owner: LifecycleOwner) {
        Log.d("test","ThirdPage ${this.hashCode()} Created owner ${owner.hashCode()} ")
    }
    
    override fun onDestroy(owner: LifecycleOwner) {
        Log.d("test","ThirdPage ${this.hashCode()} Destroy owner ${owner.hashCode()} ")
    }
    
    
    override val content: @Composable (NavBackStackEntry, Bundle?) -> Unit = { _, data ->
        Log.d("test","ThirdPageUi called  owner ${LocalLifecycleOwner.current.hashCode()}")
        ThirdPageUi(data = data)
    }
}

@Composable
fun ThirdPageUi(
        modifier: Modifier = Modifier,
        data: Bundle?,
        viewModel: ThirdPageViewModel = ktpViewModel<ThirdPageViewModel>(RootActivity::class))
{
    Log.d("test","ThirdPage obtained viewModel ${viewModel.hashCode()}")
    
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
            text = localAppLevelActionController.current.hashCode().toString()
        )
        Button(onClick = {
            viewModel.onUiAction(UiAction.FirstButtonClicked)
        }, modifier = modifier
            .width(200.dp)) {
            Text(text = "change keyboard state")
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