package com.starsoft.testapp.applicationFlow

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.starsoft.skeleton.compose.baseViewModel.CommonModel
import com.starsoft.skeleton.compose.baseViewModel.CommonModelImpl
import com.starsoft.skeleton.compose.navigation.RouterImpl
import com.starsoft.skeleton.compose.transport.ErrorHandler
import com.starsoft.testapp.applicationFlow.rootFlow.RootActivity
import com.starsoft.testapp.utils.KTPAutoScopeCloseViewModel
import toothpick.InjectConstructor
import toothpick.ktp.KTP


/**
 * Created by Dmitry Starkin on 01.03.2025 12:03.
 */


interface SharedModel: CommonModel{
    val commonModel: CommonModel
}

@InjectConstructor
class RootFlowSharedViewModel(override val commonModel: CommonModel): KTPAutoScopeCloseViewModel(), SharedModel, CommonModel by commonModel{
    companion object{
            val testRootFlowSharedViewModel: RootFlowSharedViewModel @Composable
            get() = RootFlowSharedViewModel(CommonModelImpl(ErrorHandler(LocalContext.current), RouterImpl()))
    }
    
    init {
        Log.d("test","RootFlowSharedViewModel init with commonModel${commonModel.hashCode()}")
    }
}