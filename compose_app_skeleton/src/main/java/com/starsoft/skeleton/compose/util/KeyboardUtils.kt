package com.starsoft.skeleton.compose.util

import android.app.Activity
import android.content.Context
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
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

/**
 * Close keyboard using InputMethodManager
 */
fun android.app.Activity.hideKeyboardByIMS() {
    getRootView().windowToken?.apply {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(
            this,
            0
        ) } ?: run{
        Log.d("test","token null")
    }
}

/**
 * Close keyboard using InputMethodManager
 */
fun android.app.Activity.showKeyboardByIMS() {
    getRootView().windowToken?.apply {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInputFromInputMethod (
                this,
                0
            ) }?: run{
        Log.d("test","token null")
    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

fun Activity.getRootView(): View {
    return findViewById<View>(android.R.id.content).also{
        Log.d("test","RootView $it")
    }
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