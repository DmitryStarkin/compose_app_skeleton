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


package com.starsoft.skeleton.compose.controller

import android.os.Bundle
import com.starsoft.skeleton.compose.controller.ActivityLevelAction.KeyboardAction.Companion.obtainKeyboardAction
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.navigation.TargetContainer
import com.starsoft.skeleton.compose.navigation.asNavTarget
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.skeleton.compose.util.KeyboardState


/**
 * Created by Dmitry Starkin on 26.02.2025 15:34.
 */
fun AppLevelActionController.moveToTarget(destination: Class<*>, tag: String = EMPTY_STRING, parentTargets: List<TargetContainer>? = null, data: Bundle? = null){
    performActivityLevelAction(ActivityLevelAction.NavigationAction.obtainNavigationAction(destination.asNavTarget(tag, parentTargets, data)))
}

fun AppLevelActionController.moveToTarget(target: Router.Target, parentTargets: List<TargetContainer>? = null, data: Bundle? = null){
    performActivityLevelAction(ActivityLevelAction.NavigationAction.obtainNavigationAction(target.asNavTarget(data, parentTargets)))
}

fun AppLevelActionController.moveToTarget(navigationTarget: Router.NavigationTarget){
    performActivityLevelAction(ActivityLevelAction.NavigationAction.obtainNavigationAction(navigationTarget))
}

fun AppLevelActionController.showMessage(message: String){
    performActivityLevelAction(ActivityLevelAction.MessageAction.obtainMessageAction(message))
}

fun AppLevelActionController.setKeyboardState(state: KeyboardState){
    performActivityLevelAction(obtainKeyboardAction(state))
}