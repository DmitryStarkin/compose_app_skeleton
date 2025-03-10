package com.starsoft.skeleton.compose.navigation

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.rememberNavController
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
            Log.d("test","HostForDetached data  ${data} ")
            data?.getNavigationTarget(DETACHED_TARGET_KEY)?.apply {
                val navController = rememberNavController()
                localAppLevelActionController.current?.CreateNavHostHere(
                    navController,
                    listOf(this.asTargetProperties().addBackButtonBehavior(Router.BackPressBehavior.Default))
                )
                localAppLevelActionController.current?.moveToTarget(this)
                
            } ?: run{
                Log.d("test","HostForDetached data  null move back ")
                localAppLevelActionController.current?.moveToTarget(Router.MoveBack())
            }
        }
    }
}