package com.starsoft.skeleton.compose.transport

import android.content.Context
import com.starsoft.skeleton.compose.R
import com.starsoft.skeleton.compose.baseViewModel.ActivityLevelAction
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
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