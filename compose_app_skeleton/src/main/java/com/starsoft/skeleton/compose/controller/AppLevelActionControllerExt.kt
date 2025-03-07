package com.starsoft.skeleton.compose.controller

import android.os.Bundle
import com.starsoft.skeleton.compose.controller.ActivityLevelAction.KeyboardAction.Companion.obtainKeyboardAction
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.navigation.toNavTarget
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.skeleton.compose.util.KeyboardState


/**
 * Created by Dmitry Starkin on 26.02.2025 15:34.
 */
fun AppLevelActionController.moveToTarget(destination: Class<*>, tag: String = EMPTY_STRING, data: Bundle? = null){
    performActivityLevelAction(ActivityLevelAction.NavigationAction.obtainNavigationAction(destination.toNavTarget(tag, data)))
}

fun AppLevelActionController.moveToTarget(target: Router.Target, data: Bundle? = null){
    performActivityLevelAction(ActivityLevelAction.NavigationAction.obtainNavigationAction(target.toNavTarget(data)))
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