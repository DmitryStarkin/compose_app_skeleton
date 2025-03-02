package com.starsoft.testapp.utils

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.CallSuper
import androidx.compose.runtime.Composable
import androidx.fragment.app.Fragment
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import toothpick.Scope
import toothpick.ktp.KTP


/**
 * Created by Dmitry Starkin on 26.02.2025 16:16.
 */
open class KTPAutoScopeCloseViewModel(): ViewModel() {
    
    var scope: Scope? = null
    
    @CallSuper
    override fun onCleared() {
        scope?.apply {
            KTP.closeScope(this.name)
        }
        super.onCleared()
    }
}

/**
 * Created by Dmitry Starkin on 10.01.2023 12:57.
 */
class ModelFactory<V : ViewModel> (
        private val model:  V
): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass == model.javaClass){
            model as T
        } else {
            ViewModelProvider.NewInstanceFactory().create(modelClass)
        }
    }
}

/**
 * use something like this
 * val viewModel: SomeViewModel by initViewModel{
 *      KTP.openRootScope()
 *          .openSubScope(name) {
 *              it.installModules(someModule())
 *              .closeOnViewModelCleared(this)
 *  }.createInstance<SomeViewModel>()
 *}
 * or simple
 * val viewModel: SomeViewModel by initViewModel{
 *      SomeViewModel()
 * }
 */

inline fun <reified T : ViewModel> Fragment.initViewModel(crossinline init: () -> T): Lazy<T> = lazy {
    ViewModelProvider(this, ModelFactory(init())).get(T::class.java)
}

inline fun <reified T > Scope.createInstance(): T = getInstance(T::class.java)

class NamedScopeViewModelFactory(
        private val scopeName: Any
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) = if (KTP.isScopeOpen(scopeName)) {
        Log.d("test","Scope Open $scopeName")
        val scope = KTP.openScope(scopeName)
        (scope.getInstance(modelClass) as T)
            .also{
            if(it is KTPAutoScopeCloseViewModel){
                it.scope = scope
            }
        }
    } else {
        Log.d("test","Scope not Open $scopeName")
        val scope = KTP.openRootScope().openSubScope(scopeName)
        (scope.getInstance(modelClass) as T)
            .also{
            if(it is KTPAutoScopeCloseViewModel){
                it.scope = scope
            }
                }
    }
}

class ScopedBaseViewModelFactory(
        private val subScope: Scope?,
        private val scopeName: Any,
        private val init: Scope.() -> Unit
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = (if (KTP.isScopeOpen(scopeName)) {
        KTP.openScope(scopeName)
    } else {
        subScope?.let{ scope ->
            if(!KTP.isScopeOpen(scope.name)) throw Exception("subScope must be opened")
            scope.openSubScope(scopeName){
                it.init()}
        } ?: KTP.openRootScope().openSubScope(scopeName){
            it.init()}
    }).let {
        val model = it.getInstance(modelClass) as T
        if(model is KTPAutoScopeCloseViewModel){
            model.scope = it
        }
        model
    }
}

/**
 * if inject only in viewModel
 * use something like this
 * val viewModel: SomeViewModel by scopedViewModel{
 *  installModules(SomeModule())
 * }
 * or
 * val viewModel: SomeViewModel by scopedViewModel( KTP.openRootScope()
 *          .openSubScope(name)){
 *  installModules(SomeModule())
 * }
 * created scope close automatic if ViewModel clearing
 */

inline fun <reified T : ViewModel> ViewModelStoreOwner.scopedViewModel(subScope: Scope? = null, noinline init: Scope.() -> Unit = {}): Lazy<T> = lazy {
    ViewModelProvider(this, ScopedBaseViewModelFactory(subScope, T::class, init)).get(T::class.java)
}

class ScopedViewModelFactory(private val scopeName: Any) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        KTP.openScope(scopeName).getInstance(modelClass) as T
}

inline fun <reified T : ViewModel> Fragment.viewModel(): Lazy<T> = lazy {
    ViewModelProvider(this, NamedScopeViewModelFactory(this::class)).get(T::class.java)
}

//TODO check is owner()::class is right
inline fun <reified T : ViewModel> Fragment.sharedViewModel(useOwnerScope: Boolean = true, crossinline owner: () -> ViewModelStoreOwner): Lazy<T> = lazy {
    ViewModelProvider(owner(), NamedScopeViewModelFactory(if(useOwnerScope){owner()::class} else {this::class})).get(T::class.java)
}

inline fun <reified T : ViewModel> Fragment.viewModel(crossinline scopeName: () -> Any): Lazy<T> =
    lazy {
        ViewModelProvider(this, ScopedViewModelFactory(scopeName())).get(T::class.java)
    }

inline fun <reified T : ViewModel> ComponentActivity.viewModel(): Lazy<T> = lazy {
    ViewModelProvider(this, NamedScopeViewModelFactory(this::class)).get(T::class.java)
}

@Suppress("MissingJvmstatic")
@Composable
public inline fun <reified VM : ViewModel> ktpViewModel(
        viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
        key: String? = null,
        factory: ViewModelProvider.Factory? = null,
        extras: CreationExtras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
            viewModelStoreOwner.defaultViewModelCreationExtras
        } else {
            CreationExtras.Empty
        }
): VM = androidx.lifecycle.viewmodel.compose.viewModel(VM::class, viewModelStoreOwner, key, NamedScopeViewModelFactory( viewModelStoreOwner::class), extras).also {
    Log.d("test","ktpViewModel  ${viewModelStoreOwner?.javaClass?.name} ${viewModelStoreOwner?.hashCode()} ")
}

@Suppress("MissingJvmstatic")
@Composable
public inline fun <reified VM : ViewModel> ktpViewModel(
        scopeName: Any,
        viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
        key: String? = null,
        factory: ViewModelProvider.Factory? = null,
        extras: CreationExtras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
            viewModelStoreOwner.defaultViewModelCreationExtras
        } else {
            CreationExtras.Empty
        }
): VM = androidx.lifecycle.viewmodel.compose.viewModel(VM::class, viewModelStoreOwner, key, NamedScopeViewModelFactory(scopeName), extras).also {
    Log.d("test","ViewModel ${viewModelStoreOwner} ${viewModelStoreOwner?.javaClass?.name} ${viewModelStoreOwner?.hashCode()} ")
}