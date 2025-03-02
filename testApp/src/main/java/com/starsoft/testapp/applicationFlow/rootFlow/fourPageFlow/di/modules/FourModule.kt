package com.starsoft.testapp.applicationFlow.rootFlow.firstPageFlow.di.modules

import com.starsoft.testapp.applicationFlow.rootFlow.firstPageFlow.data.FourPageRepo
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module


/**
 * Created by Dmitry Starkin on 28.02.2025 14:10.
 */
fun fourModule() = module {
    bind<FourPageRepo>().toInstance(FourPageRepo())
}