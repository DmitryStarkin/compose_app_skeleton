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

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavBackStackEntry
import com.starsoft.skeleton.compose.controller.AppLevelActionController
import com.starsoft.skeleton.compose.controller.moveToTarget
import com.starsoft.skeleton.compose.baseui.Counter
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.navigation.addPopUpOption
import com.starsoft.skeleton.compose.navigation.localAppLevelActionController
import com.starsoft.skeleton.compose.navigation.asNavTarget
import com.starsoft.testhiltapp.R


/**
 * Created by Dmitry Starkin on 06.03.2025 18:55.
 */
@Preview
@Composable
fun SplashPageUiPreview() {
    SplashPageUi()
}

class SplashScreen : Router.ComposeScreen {
    
    override fun onCreate(owner: LifecycleOwner) {
        Log.d("test","SplashPage ${this.hashCode()} Created owner ${owner.hashCode()} ")
    }
    
    override fun onDestroy(owner: LifecycleOwner) {
        Log.d("test","SplashtPage ${this.hashCode()} Destroy owner ${owner.hashCode()} ")
    }
    
    override val content: @Composable (NavBackStackEntry, Bundle?) -> Unit = { _, data ->
        Log.d("test","SplashPageUi called  owner ${LocalLifecycleOwner.current.hashCode()}")
        SplashPageUi()
    }
}

@Composable
fun SplashPageUi(
        modifier: Modifier = Modifier,
        viewModel: AppLevelActionController? = localAppLevelActionController.current
)
{
    Surface(modifier = modifier
        .fillMaxSize(),
        color = colorResource(R.color.purple_200)
    ) {
        Box(
            modifier.
            fillMaxSize()
        
        ){
            Counter(
                remember = false,
                startValue = 10,
                endValue = 0,
                delayMs = 1000L,
                onEnd = {
                    viewModel?.moveToTarget(
                        RootScreen::class.java
                            .asNavTarget()
                            .addPopUpOption(
                                SplashScreen::class.java
                                    .asNavTarget(),
                                inclusive = true,
                                saveData = false))
                }
            ){current, isCount ->
                if(isCount){
                    Text(
                        modifier = modifier.align(Alignment.Center),
                        text = "Start after $current"
                    )
                }
            }
        }
    }
}