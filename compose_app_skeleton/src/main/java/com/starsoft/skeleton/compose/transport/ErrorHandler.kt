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



package com.starsoft.skeleton.compose.transport

import android.content.Context
import com.starsoft.skeleton.compose.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException


/**
 * Created by Dmitry Starkin on 26.02.2025 15:18.
 */
open class ErrorHandler(
        private val context: Context,
) {
    companion object{
        private const val RESPONSE_CLOSED_MESSAGE = "closed"
    }
    
//    private val _error = Channel<String>()
//    fun getError(): Flow<String> = _error.receiveAsFlow()
    
    private val _error = MutableSharedFlow<String>()
    val errorFlow: SharedFlow<String> = _error
    
    var ignoredTroubles: List<Class<*>> = emptyList()
    
    private fun sendError(error: String) {
        MainScope().launch {
            _error.emit(error)
        }
    }
    
    open fun handleThrowable(throwable: Throwable) {
        throwable.printStackTrace()
        if(throwable::class.java in ignoredTroubles) return
        when {
            throwable is UnknownHostException -> {
                sendError(context.getString(R.string.no_internet_error))
            }
            
            throwable is SocketTimeoutException -> {
                sendError(context.getString(R.string.server_time_out_error))
            }
            
            throwable is CancellationException -> {
                // nothing to do
            }

//            throwable is InvalidPageException -> {
//                // nothing to do
//            }
            
            throwable is IllegalStateException && throwable.message == RESPONSE_CLOSED_MESSAGE -> {
                // TODO temporally disabled
                //_error.postValue(Event(context.getString(R.string.unknown_authorization_error)))
            }
            
            else -> {
                throwable.message?.takeIf { it.isNotEmpty() }?.let { message ->
                    sendError(message)
                } ?: run{sendError(context.getString(R.string.unknown_error)) }
            }
        }
    }
}