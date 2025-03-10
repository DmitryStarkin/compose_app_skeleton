package com.starsoft.testapp.applicationFlow.rootFlow.splashPage

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavBackStackEntry
import com.starsoft.skeleton.compose.controller.AppLevelActionController
import com.starsoft.skeleton.compose.controller.moveToTarget
import com.starsoft.skeleton.compose.baseui.Counter
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.navigation.addPopUpOption
import com.starsoft.skeleton.compose.navigation.localAppLevelActionController
import com.starsoft.skeleton.compose.navigation.asNavTarget
import com.starsoft.testapp.R
import com.starsoft.testapp.applicationFlow.rootFlow.RootScreen
import com.starsoft.testapp.applicationFlow.rootFlow.fourPageFlow.FourPage

/**
 * Created by Dmitry Starkin on 05.03.2025 18:09.
 */
@Preview
@Composable
fun SplashPageUiPreview() {
    SplashPageUi()
}

class SplashScreen : Router.ComposeScreen {
    
    override fun onCreate(owner: LifecycleOwner) {
        Log.d("test","SplashPage ${this.hashCode()} Created owner ${owner.hashCode()} ")
    }
    
    override fun onDestroy(owner: LifecycleOwner) {
        Log.d("test","SplashtPage ${this.hashCode()} Destroy owner ${owner.hashCode()} ")
    }
    
    override val content: @Composable (NavBackStackEntry, Bundle?) -> Unit = { _, data ->
        Log.d("test","SplashPageUi called  owner ${LocalLifecycleOwner.current.hashCode()}")
        SplashPageUi()
    }
}

@Composable
fun SplashPageUi(
        modifier: Modifier = Modifier,
        viewModel: AppLevelActionController? = localAppLevelActionController.current
)
{
    Surface(modifier = modifier
        .fillMaxSize(),
        color = colorResource(R.color.purple_200)
    ) {
        Box(
            modifier.
                fillMaxSize()
               
        ){
            Counter(
                remember = false,
                startValue = 10,
                endValue = 0,
                delayMs = 1000L,
                onEnd = {
                
//                    viewModel?.moveToTarget(
//                        FourPage::class.java
//                            .asNavTarget()
//                            .addPopUpOption(
//                                SplashScreen::class.java
//                                    .asNavTarget(),
//                                    inclusive = true,
//                                    saveData = false)
//                    )
                    
                    
                    viewModel?.moveToTarget(
                        RootScreen::class.java
                            .asNavTarget()
                            .addPopUpOption(
                                SplashScreen::class.java
                                    .asNavTarget(),
                                    inclusive = true,
                                    saveData = false))
               }
            ){current, isCount ->
                if(isCount){
                    Text(
                        modifier = modifier.align(Alignment.Center),
                        text = "Start after $current"
                    )
                }
            }
        }
    }
}
