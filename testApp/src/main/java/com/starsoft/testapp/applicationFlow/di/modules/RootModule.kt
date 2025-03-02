package com.starsoft.testapp.applicationFlow.di.modules

import android.content.Context
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module


/**
 * Created by Dmitry Starkin on 26.02.2025 16:03.
 */
fun rootModule(context: Context) = module {
    bind<Context>().toInstance(context)
}