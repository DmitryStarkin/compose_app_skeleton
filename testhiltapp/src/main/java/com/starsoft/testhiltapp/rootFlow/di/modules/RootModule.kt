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

package com.starsoft.testhiltapp.rootFlow.di.modules

import android.content.Context
import com.starsoft.skeleton.compose.controller.AppLevelActionController
import com.starsoft.skeleton.compose.controller.AppLevelActionControllerImpl
import com.starsoft.skeleton.compose.navigation.Router
import com.starsoft.skeleton.compose.navigation.RouterImpl
import com.starsoft.skeleton.compose.transport.ErrorHandler
import com.starsoft.testhiltapp.rootFlow.RootFlowSharedViewModel
import com.starsoft.testhiltapp.rootFlow.SharedModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped


/**
 * Created by Dmitry Starkin on 06.03.2025 19:02.
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
object RootModule {
    
    @ActivityRetainedScoped
    @Provides
    fun provideRouter(): Router {
        return RouterImpl()
    }
    
    @ActivityRetainedScoped
    @Provides
    fun provideErrorHandler(
            @ApplicationContext context: Context
    ): ErrorHandler {
        return ErrorHandler(context)
    }
    
    @ActivityRetainedScoped
    @Provides
    fun provideCommonModel(
            errorHandler: ErrorHandler,
            router:Router
    ): AppLevelActionController {
        return AppLevelActionControllerImpl(errorHandler, router)
    }
}

//@Module
//@InstallIn(ActivityRetainedComponent::class)
//abstract class SharedModelModule {
//
//    @ActivityRetainedScoped
//    @Binds
//    abstract fun bindSharedModel(
//            sharedModel: SharedModel
//    ): RootFlowSharedViewModel
//}

@Module
@InstallIn(ActivityRetainedComponent::class)
object SharedModelModule {
    
    @ActivityRetainedScoped
    @Provides
    fun bindSharedModel(
            model: RootFlowSharedViewModel
    ): SharedModel{
        return  model
    }
}