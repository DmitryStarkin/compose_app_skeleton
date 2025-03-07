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