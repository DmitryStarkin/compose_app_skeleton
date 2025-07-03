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


package com.starsoft.skeleton.compose.navigation

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import com.starsoft.skeleton.compose.controller.moveToTarget


/**
 * Created by Dmitry Starkin on 10.03.2025 11:37.
 */

class HostForDetached : Router.ComposeDialog{
    
    companion object{
        const val DETACHED_TARGET_KEY = "com.starsoft.skeleton.compose.navigation.HostForDetached.targetKey"
        val hostForDetachedProperties = HostForDetached::class.java.asTargetProperties().addBackButtonBehavior(Router.BackPressBehavior.Default)
        val hostForDetachedTargetKey = HostForDetached::class.java.asTarget().targetKey
    }
    
    override val content: @Composable (NavBackStackEntry, Bundle?) -> Unit = { _, data, ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
        
        ){
            data?.getNavigationTarget(DETACHED_TARGET_KEY)?.apply {
                localAppLevelActionController.current?.CreateNavHostHere(
                    listOf(this.asTargetProperties().addBackButtonBehavior(Router.BackPressBehavior.Default))
                )
                localAppLevelActionController.current?.moveToTarget(this)
                
            } ?: run{
                localAppLevelActionController.current?.moveToTarget(Router.MoveBack())
            }
        }
    }
}