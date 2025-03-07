package com.starsoft.testapp.applicationFlow.rootFlow.di.modules

import com.starsoft.skeleton.compose.controller.AppLevelActionController
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.transport.ErrorHandler
import com.starsoft.testapp.applicationFlow.SharedModel
import com.starsoft.testapp.applicationFlow.rootFlow.di.providers.ErrorHandlerProvider
import com.starsoft.testapp.applicationFlow.rootFlow.di.providers.RouterProvider
import com.starsoft.testapp.applicationFlow.rootFlow.RootActivity
import com.starsoft.testapp.applicationFlow.rootFlow.di.providers.AppLevelActionControllerProvider
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module


/**
 * Created by Dmitry Starkin on 26.02.2025 16:12.
 */
fun mainModule(sharedViewModelProvider: RootActivity) = module {
    bind<ErrorHandler>().toProvider(ErrorHandlerProvider::class).singleton()
    bind<Router>().toProvider(RouterProvider::class).providesSingleton()
    bind<AppLevelActionController>().toProvider(AppLevelActionControllerProvider::class).singleton()
    bind<SharedModel>().toProviderInstance(sharedViewModelProvider)
}