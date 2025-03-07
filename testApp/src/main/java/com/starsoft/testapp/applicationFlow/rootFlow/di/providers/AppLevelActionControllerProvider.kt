package com.starsoft.testapp.applicationFlow.rootFlow.di.providers


import com.starsoft.skeleton.compose.controller.AppLevelActionController
import com.starsoft.skeleton.compose.controller.AppLevelActionControllerImpl
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.transport.ErrorHandler
import toothpick.InjectConstructor
import javax.inject.Provider


/**
 * Created by Dmitry Starkin on 01.03.2025 18:36.
 */


@InjectConstructor
class AppLevelActionControllerProvider(errorHandler: ErrorHandler,
                                       router: Router
) : Provider<AppLevelActionController> {
    val controller = AppLevelActionControllerImpl(errorHandler, router)
    override fun get(): AppLevelActionController = controller
}