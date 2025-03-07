package com.starsoft.testapp.applicationFlow.rootFlow

import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import com.starsoft.testapp.utils.viewModel
import androidx.navigation.compose.rememberNavController
import com.starsoft.skeleton.compose.controller.AppLevelActionController
import com.starsoft.skeleton.compose.baseui.BaseComposeActivity
import com.starsoft.testapp.applicationFlow.rootFlow.di.modules.mainModule
import com.starsoft.skeleton.compose.navigation.listOf
import com.starsoft.testapp.applicationFlow.RootFlowSharedViewModel
import com.starsoft.testapp.applicationFlow.SharedModel
import com.starsoft.testapp.applicationFlow.rootFlow.splashPage.SplashScreen
import toothpick.Scope
import toothpick.ktp.KTP
import javax.inject.Provider


/**
 * Created by Dmitry Starkin on 26.02.2025 16:08.
 */
class RootActivity : BaseComposeActivity(), Provider<SharedModel>{
    
    companion object{
        fun openParentScopes(): Scope = KTP.openScope(RootActivity::class)
    }
    
    override fun obtainAppLevelActionController(): AppLevelActionController = viewModel<RootFlowSharedViewModel>().value
    
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("test","RootActivity onCreate")
        KTP.openRootScope()
            .openSubScope(this::class) {
                Log.d("test","opened Scope ${it.name}")
                it.installModules(mainModule(this))
            }.inject(this)
        super.onCreate(savedInstanceState)
    }
    
    @Composable
    override fun SetRootUi() {
        Log.d("test","SetRootUi")
        val controller = rememberNavController()
        appLevelActionController.CreateNavHostHere(
            navController = controller,
            targets = listOf(
                SplashScreen::class.java,
                RootScreen::class.java),
            startTarget =  SplashScreen::class.java
        )
    }
    
    override fun get(): SharedModel = appLevelActionController as SharedModel
    
}