/*
 * Copyright (c) 2025. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the «License»);
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  //www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an «AS IS» BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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