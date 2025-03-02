package com.starsoft.testapp.applicationFlow.rootFlow.di.providers


import com.starsoft.skeleton.compose.baseViewModel.CommonModel
import com.starsoft.skeleton.compose.baseViewModel.CommonModelImpl
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.transport.ErrorHandler
import toothpick.InjectConstructor
import javax.inject.Provider


/**
 * Created by Dmitry Starkin on 01.03.2025 18:36.
 */


@InjectConstructor
class CommonModelProvider(errorHandler: ErrorHandler,
                          router: Router
) : Provider<CommonModel> {
    val commonModel = CommonModelImpl(errorHandler, router)
    override fun get(): CommonModel = commonModel
}