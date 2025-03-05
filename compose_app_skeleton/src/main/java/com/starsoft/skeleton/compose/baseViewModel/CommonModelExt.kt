package com.starsoft.skeleton.compose.baseViewModel

import android.os.Bundle
import com.starsoft.skeleton.compose.baseViewModel.ActivityLevelAction.KeyboardAction.Companion.obtainKeyboardAction
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.navigation.simpleTarget
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.skeleton.compose.util.KeyboardState


/**
 * Created by Dmitry Starkin on 26.02.2025 15:34.
 */
fun CommonModel.moveToTarget(destination: Class<*>, tag: String = EMPTY_STRING, data: Bundle? = null){
    performActivityLevelAction(ActivityLevelAction.NavigationAction.obtainNavigationAction(destination.simpleTarget(tag, data)))
}

fun CommonModel.moveToTarget(destination: Router.DestinationProperties, data: Bundle? = null){
    performActivityLevelAction(ActivityLevelAction.NavigationAction.obtainNavigationAction(destination.simpleTarget(data)))
}

fun CommonModel.moveToTarget(navigationTarget: Router.NavigationTarget){
    performActivityLevelAction(ActivityLevelAction.NavigationAction.obtainNavigationAction(navigationTarget))
}

fun CommonModel.showMessage(message: String){
    performActivityLevelAction(ActivityLevelAction.MessageAction.obtainMessageAction(message))
}

fun CommonModel.setKeyboardState(state: KeyboardState){
    performActivityLevelAction(obtainKeyboardAction(state))
}