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

package com.starsoft.testapp.applicationFlow

import android.app.Application
import com.starsoft.testapp.applicationFlow.di.modules.rootModule
import toothpick.ktp.KTP
import javax.net.ssl.SSLSocketFactory


/**
 * Created by Dmitry Starkin on 26.02.2025 16:01.
 */
class  App : Application() {
    
    override fun onCreate() {
        super.onCreate()
        initToothpick()
    }
    
    private fun initToothpick(factory: SSLSocketFactory? = null) = KTP
        .openRootScope()
        .installModules(
            rootModule(this),
        )
        .inject(this)
}