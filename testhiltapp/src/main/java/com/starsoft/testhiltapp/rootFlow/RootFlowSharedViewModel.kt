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
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.starsoft.skeleton.compose.controller.AppLevelActionController
import com.starsoft.skeleton.compose.controller.AppLevelActionControllerImpl
import com.starsoft.skeleton.compose.navigation.RouterImpl
import com.starsoft.skeleton.compose.transport.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


/**
 * Created by Dmitry Starkin on 06.03.2025 16:25.
 */

interface SharedModel: AppLevelActionController {
    val appLevelActionController: AppLevelActionController
}

@HiltViewModel
class RootFlowViewModel @Inject constructor(val rootFlowSharedViewModel: RootFlowSharedViewModel): ViewModel(), SharedModel by rootFlowSharedViewModel{
    override fun onCleared() {
        super.onCleared()
        rootFlowSharedViewModel.clean()
    }
}

class RootFlowSharedViewModel @Inject constructor(override val appLevelActionController: AppLevelActionController):  SharedModel, AppLevelActionController by appLevelActionController{
    companion object{
        val testRootFlowSharedViewModel: RootFlowSharedViewModel @Composable
        get() = RootFlowSharedViewModel(AppLevelActionControllerImpl(ErrorHandler(LocalContext.current), RouterImpl()))
    }
    
    init {
        Log.d("test","RootFlowSharedViewModel init with commonModel${appLevelActionController.hashCode()}")
    }
    
    override fun clean(){
        appLevelActionController.clean()
   }
}