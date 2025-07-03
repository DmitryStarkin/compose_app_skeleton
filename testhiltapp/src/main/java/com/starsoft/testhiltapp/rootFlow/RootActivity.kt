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
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.starsoft.skeleton.compose.controller.AppLevelActionController
import com.starsoft.skeleton.compose.baseui.BaseComposeActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Dmitry Starkin on 06.03.2025 16:27.
 */
@AndroidEntryPoint
class RootActivity : BaseComposeActivity() {
    
    private val exampleViewModel: RootFlowViewModel by viewModels()
    
    override fun obtainAppLevelActionController(): AppLevelActionController = exampleViewModel
    
    @Composable
    override fun SetRootUi() {
        Log.d("test","SetRootUi")
        appLevelActionController.CreateNavHostHere(
            targets = com.starsoft.skeleton.compose.navigation.listOf(
                SplashScreen::class.java,
                RootScreen::class.java),
            startTarget =  SplashScreen::class.java
        )
    }
}