package com.starsoft.testhiltapp.fourPageFlow.data

import android.content.Context


/**
 * Created by Dmitry Starkin on 06.03.2025 18:50.
 */
class FourPageRepo(context: Context) {
    val stringData: String get() = "repo return ${System.currentTimeMillis()}"
}