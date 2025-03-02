package com.starsoft.testapp.applicationFlow.rootFlow.di.providers

import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.navigation.RouterImpl
import toothpick.InjectConstructor
import javax.inject.Provider


/**
 * Created by Dmitry Starkin on 26.02.2025 16:05.
 */
@InjectConstructor
class RouterProvider : Provider<Router> {
    override fun get(): Router = RouterImpl()
}