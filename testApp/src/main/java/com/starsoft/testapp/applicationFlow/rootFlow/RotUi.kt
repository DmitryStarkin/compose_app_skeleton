package com.starsoft.testapp.applicationFlow.rootFlow

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.rememberNavController
import com.starsoft.skeleton.compose.navigation.listOf
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.navigation.localScopeIdentifier
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.testapp.R
import com.starsoft.testapp.applicationFlow.rootFlow.RootUIViewModel.Companion.testRootUIViewModel
import com.starsoft.testapp.applicationFlow.rootFlow.firstPageFlow.FirstPage
import com.starsoft.testapp.applicationFlow.rootFlow.fourPageFlow.FourPage
import com.starsoft.testapp.applicationFlow.rootFlow.secondPageFlow.SecondPage
import com.starsoft.testapp.applicationFlow.rootFlow.thirdPageFlow.ThirdPage
import com.starsoft.testapp.utils.ktpViewModel


/**
 * Created by Dmitry Starkin on 26.02.2025 16:23.
 */
@Preview
@Composable
fun RootScreenPreview() {
    RootUi(
        viewModel =  testRootUIViewModel
    )
}

val TEXT_KEY ="com.starsoft.testapp.applicationflow.rootFlow.text"

fun String.packToBundle() =
    bundleOf(
        TEXT_KEY to this
    )

fun Bundle?.getText(): String =
    this?.getString(TEXT_KEY) ?: EMPTY_STRING


class RootScreen : Router.ComposeScreen {
    
    override fun onCreate(owner: LifecycleOwner) {
        Log.d("test","RootScreen  ${this.hashCode()} onCreated owne ${owner.hashCode()} ")
    }
    override val content: @Composable (NavBackStackEntry,  Bundle?) -> Unit = { _, _, ->
        Log.d("test","localDestinationID ${localScopeIdentifier.current} ")
        Log.d("test","RootScreen called  owner ${LocalLifecycleOwner.current.hashCode()}")
        RootUi()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootUi(
        modifier: Modifier = Modifier,
        viewModel: RootUIViewModel = ktpViewModel<RootUIViewModel>(RootActivity::class)
) {
    Log.d("test","FirstPage obtained viewModel ${viewModel.hashCode()}")
    val uiState = viewModel.uiState.collectAsState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar (
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                modifier = modifier.wrapContentHeight(),
                currentTarget = uiState.value.currentTarget,
                viewModel = viewModel
            )
        }
    ) {
        Surface(modifier = modifier
            .fillMaxSize()
            .padding(it),
            color = colorResource(R.color.white)
        ) {
            val navController = rememberNavController()
            viewModel.CreateNavHostHere(navController, listOf(
                FirstPage::class.java,
                SecondPage::class.java,
                ThirdPage::class.java,
                FourPage::class.java
            ))
            viewModel.onUiAction(UiAction.OnBottomTabButtonClicked(BottomTab.FirstTab))
        }
    }
}

@Composable
fun BottomNavBar(
        modifier: Modifier,
        currentTarget: String,
        viewModel: RootUIViewModel
){
    NavigationBar(modifier=modifier.wrapContentHeight()){
        
        BottomTab.entries.forEach {
        NavigationBarItem(
            modifier = modifier.wrapContentHeight(),
            selected = it.target == currentTarget,
            onClick = {
                viewModel.onUiAction(UiAction.OnBottomTabButtonClicked(it))
            },
            icon = { Icon(it.icon, it.label) },
            //label = {it.label}
            )
        }
    }
}
