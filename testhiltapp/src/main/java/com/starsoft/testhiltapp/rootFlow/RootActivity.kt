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
        val controller = rememberNavController()
        appLevelActionController.CreateNavHostHere(
            navController = controller,
            targets = com.starsoft.skeleton.compose.navigation.listOf(
                SplashScreen::class.java,
                RootScreen::class.java),
            startTarget =  SplashScreen::class.java
        )
    }
}