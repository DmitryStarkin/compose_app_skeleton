package com.starsoft.testapp.applicationFlow

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.starsoft.skeleton.compose.controller.AppLevelActionController
import com.starsoft.skeleton.compose.controller.AppLevelActionControllerImpl
import com.starsoft.skeleton.compose.navigation.RouterImpl
import com.starsoft.skeleton.compose.transport.ErrorHandler
import com.starsoft.testapp.utils.KTPAutoScopeCloseViewModel
import toothpick.InjectConstructor


/**
 * Created by Dmitry Starkin on 01.03.2025 12:03.
 */


interface SharedModel: AppLevelActionController{
    val appLevelActionController: AppLevelActionController
}

@InjectConstructor
class RootFlowSharedViewModel(override val appLevelActionController: AppLevelActionController): KTPAutoScopeCloseViewModel(), SharedModel, AppLevelActionController by appLevelActionController{
    companion object{
            val testRootFlowSharedViewModel: RootFlowSharedViewModel @Composable
            get() = RootFlowSharedViewModel(AppLevelActionControllerImpl(ErrorHandler(LocalContext.current), RouterImpl()))
    }
    
    init {
        Log.d("test","RootFlowSharedViewModel init with commonModel${appLevelActionController.hashCode()}")
    }
    
    override fun onCleared() {
        super.onCleared()
        appLevelActionController.clean()
    }
}