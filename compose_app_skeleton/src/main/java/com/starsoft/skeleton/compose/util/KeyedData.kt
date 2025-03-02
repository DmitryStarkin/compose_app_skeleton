package com.starsoft.skeleton.compose.util

import android.os.Bundle


/**
 * Created by Dmitry Starkin on 26.02.2025 18:04.
 */

data class KeyedData(
        private val dataKey: String = EMPTY_STRING,
        private val data: Bundle = Bundle()
){
    fun requestData(key: String): Bundle? =
        if(key == dataKey && !data.isEmpty){
            data
        } else {
            null
        }
    
    fun appendData(appendData: Bundle?): KeyedData =
        copy(
            data =  data.also{
                appendData?.apply {
                    it.putAll(appendData)
                }
            }
        )
}


