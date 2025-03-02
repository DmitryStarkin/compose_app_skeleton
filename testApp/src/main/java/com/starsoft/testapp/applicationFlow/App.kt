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