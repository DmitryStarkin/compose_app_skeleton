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

package com.starsoft.testapp.applicationFlow.rootFlow

import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import com.starsoft.testapp.utils.viewModel
import androidx.navigation.compose.rememberNavController
import com.starsoft.skeleton.compose.controller.AppLevelActionController
import com.starsoft.skeleton.compose.baseui.BaseComposeActivity
import com.starsoft.testapp.applicationFlow.rootFlow.di.modules.mainModule
import com.starsoft.skeleton.compose.navigation.listOf
import com.starsoft.testapp.applicationFlow.RootFlowSharedViewModel
import com.starsoft.testapp.applicationFlow.SharedModel
import com.starsoft.testapp.applicationFlow.rootFlow.splashPage.SplashScreen
import toothpick.Scope
import toothpick.ktp.KTP
import javax.inject.Provider


/**
 * Created by Dmitry Starkin on 26.02.2025 16:08.
 */
class RootActivity : BaseComposeActivity(), Provider<SharedModel>{
    
    companion object{
        fun openParentScopes(): Scope = KTP.openScope(RootActivity::class)
    }
    
    override fun obtainAppLevelActionController(): AppLevelActionController = viewModel<RootFlowSharedViewModel>().value
    
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("test","RootActivity onCreate")
        KTP.openRootScope()
            .openSubScope(this::class) {
                Log.d("test","opened Scope ${it.name}")
                it.installModules(mainModule(this))
            }.inject(this)
        super.onCreate(savedInstanceState)
    }
    
    @Composable
    override fun SetRootUi() {
        Log.d("test","SetRootUi")
        appLevelActionController.CreateNavHostHere(
            targets = listOf(
                SplashScreen::class.java,
                RootScreen::class.java),
            startTarget =  SplashScreen::class.java
        )
    }
    
    override fun get(): SharedModel = appLevelActionController as SharedModel
    
}