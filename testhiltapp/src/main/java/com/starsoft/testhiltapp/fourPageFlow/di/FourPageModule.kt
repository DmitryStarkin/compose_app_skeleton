package com.starsoft.testhiltapp.fourPageFlow.di

import android.content.Context
import com.starsoft.testhiltapp.fourPageFlow.data.FourPageRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext


/**
 * Created by Dmitry Starkin on 07.03.2025 10:22.
 */
@Module
@InstallIn(ViewModelComponent::class)
object ForPageModule {
    
    @Provides
    fun provideRepo(@ApplicationContext context: Context): FourPageRepo {
        return FourPageRepo(context)
    }
    
}
