package com.starsoft.skeleton.compose.baseViewModel

import android.os.Bundle
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.navigation.simpleRout
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.skeleton.compose.util.KeyboardState


/**
 * Created by Dmitry Starkin on 26.02.2025 15:34.
 */
fun CommonModel.moveToDest(destination: Class<*>, tag: String = EMPTY_STRING, data: Bundle? = null){
    performActivityLevelAction(ActivityLevelAction.NavigationAction.obtainNavigationAction(destination.simpleRout(tag, data)))
}

fun CommonModel.moveByRout(rout: Router.Rout){
    performActivityLevelAction(ActivityLevelAction.NavigationAction.obtainNavigationAction(rout))
}

fun CommonModel.showMessage(message: String){
    performActivityLevelAction(ActivityLevelAction.MessageAction.obtainMessageAction(message))
}

fun CommonModel.setKeyboardState(state: KeyboardState){
    performActivityLevelAction(ActivityLevelAction.KeyboardAction(state))
}