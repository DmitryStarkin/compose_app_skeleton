package com.starsoft.testapp.applicationFlow.rootFlow.di.providers

import android.content.Context
import com.starsoft.skeleton.compose.transport.ErrorHandler
import toothpick.InjectConstructor
import javax.inject.Provider


/**
 * Created by Dmitry Starkin on 26.02.2025 16:04.
 */
@InjectConstructor
class ErrorHandlerProvider (private val context: Context) : Provider<ErrorHandler> {
    override fun get(): ErrorHandler = ErrorHandler(context)
}