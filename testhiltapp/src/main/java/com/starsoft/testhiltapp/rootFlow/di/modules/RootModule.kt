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