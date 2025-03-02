package com.starsoft.skeleton.compose.util

import android.app.Activity
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat


/**
 * Created by Dmitry Starkin on 28.02.2025 13:50.
 */

enum class KeyboardState(val visible: Boolean){
    Showed(true),
    Hidden(false);
    
}
val  KeyboardState.opposite : KeyboardState get() = if(visible){
    KeyboardState.Hidden
} else {
    KeyboardState.Showed
}

fun keyboardStateByVisibility(state: Boolean) = if(state){
    KeyboardState.Showed
}else{
    KeyboardState.Hidden
}


fun Activity.getRootView(): View {
    return findViewById<View>(android.R.id.content)
}

fun Activity.isKeyboardVisible(): Boolean =
    getRootView().isKeyboardVisible()

fun Activity.isKeyboardInVisible(): Boolean =
    !isKeyboardVisible()

fun View.isKeyboardVisible(): Boolean =
    ViewCompat.getRootWindowInsets(this)?.isVisible(WindowInsetsCompat.Type.ime())
        ?: false

/**
 * Close keyboard using Insets
 */
fun Activity.hideKeyboard() {
    WindowCompat.getInsetsController(window, window.decorView).apply {
        hide(WindowInsetsCompat.Type.ime())
    }
}

/**
 * Show keyboard using Insets
 */
fun Activity.showKeyboard() {
    WindowCompat.getInsetsController(window, window.decorView).apply {
        show(WindowInsetsCompat.Type.ime())
    }
}