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
