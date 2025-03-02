package com.starsoft.skeleton.compose.navigation

import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.starsoft.skeleton.compose.baseViewModel.OnNavigateEvent
import com.starsoft.skeleton.compose.baseViewModel.CommonModel
import com.starsoft.skeleton.compose.navigation.Router.ComposeDestination
import com.starsoft.skeleton.compose.navigation.Router.ComposeScreen
import com.starsoft.skeleton.compose.navigation.RouterImpl.ComposeNavigationGraphEntry.Companion.getControllerForDestination
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.skeleton.compose.util.LifecycleSupport
import com.starsoft.skeleton.compose.util.containsAnyItemFrom
import com.starsoft.skeleton.compose.util.isExtendInterface
import kotlin.reflect.KClass

/**
 * Created by Dmitry Starkin on 26.02.2025 14:49.
 */
val  localScopeIdentifier = compositionLocalOf { EMPTY_STRING }
val  localtarget = compositionLocalOf { EMPTY_STRING }
val  localDestinationClass = compositionLocalOf<KClass<*>> { Router.ComposeDestination::class }

class RouterImpl: Router {
    companion object{
        const val ARG ="com.starsoft.skeleton.compose.navigation.Router.data_bundle_"
    }
    
    private val composeRoutes: ArrayList<ComposeNavigationGraphEntry> = ArrayList()
    
    private var currentRoute: String? = null
    
    override var commonModel: CommonModel? = null
    
    private fun  ArrayList<ComposeNavigationGraphEntry>.removeEmpty() {
        val new = this.mapNotNull {
            if (it.controller == null) {
                null
            } else {
                it
            }
        }
        this.clear()
        this.addAll(new)
    }
    
    private fun  ArrayList<ComposeNavigationGraphEntry>.getRoutes(): List<String> {
        val result = ArrayList<String>()
        forEach {
            it.destinations.forEach{ properties ->
                result.add(properties.target)
            }
        }
        return result
    }
    
    private fun  ArrayList<ComposeNavigationGraphEntry>.isContainRoutFrom(entry: ComposeNavigationGraphEntry): Boolean =
        getRoutes().containsAnyItemFrom(entry.destinations.map{
            it.target
        })
    
    private fun  ArrayList<ComposeNavigationGraphEntry>.isContain(entry: ComposeNavigationGraphEntry): Boolean =
        find {
            it.controller == entry.controller && it.destinations.isTheSame(entry.destinations)
            
        } != null
    
    private fun List<Router.DestinationProperties>.isTheSame(other: List<Router.DestinationProperties>): Boolean =
        if(size != other.size){
            false
        } else {
            zip(other).all {
                it.first.target == it.second.target
            }
        }
    
    private fun NavController.navigateInternal(
            route: String,
            args: Bundle?,
            navOptions: NavOptions? = null,
            navigatorExtras: Navigator.Extras? = null
    ) {
        val nodeId = graph.findNode(route = route)?.id
        try{
            if (nodeId != null) {
                navigate(nodeId, args, navOptions, navigatorExtras)
            } else {
                navigate(route, navOptions, navigatorExtras)
            }
        } catch (e: IllegalStateException){
            e.printStackTrace()
        }
        currentRoute = this.currentDestination?.route
    }
    
    private fun NavController.popBackStackInternal(): Boolean
     {
        val popped = popBackStack()
        if(popped){
            currentRoute = this.currentDestination?.route
            if(currentRoute == emptyDest.destinationName){
                return popBackStackInternal()
            }
        }
        return popped
    }
    
    @Composable
    override fun CreateNavHostHere(
            navController: NavHostController,
            destinations: List<Router.DestinationProperties>,
            startDest: Router.DestinationProperties?
    ){
        if(destinations.isEmpty() || (startDest != null && startDest !in destinations)) return
        composeRoutes.removeEmpty()
        val entry = ComposeNavigationGraphEntry(navController, destinations.map { DestinationHolder(it) })
        if(composeRoutes.isContain(entry)) return
        if(composeRoutes.isContainRoutFrom(entry)) throw Exception("Rout already exists in tree use tag unique")
        CreateNavHost(entry, startDest?.target)
        composeRoutes.add(0, entry.also {
            it.connectToLifecycle(LocalLifecycleOwner.current)
        })
    }
    
    @Composable
    override fun CreateNavHostHere(
            navController: NavHostController,
            destinations: ListWrapper<Class<out Router.ComposeDestination>>,
            startDest: Class<out Router.ComposeDestination>?
    ){
        CreateNavHostHere( navController, destinations.map { it.simpleProperties() }, startDest?.simpleProperties())
    }
    
    @Composable
    private fun CreateNavHost(
            entry: ComposeNavigationGraphEntry,
            startDest: String? = null
    ){
        NavHost(entry.controller!!, startDestination = startDest?.let { if(it in entry.destinations.map{it.target}){it} else {emptyDest.destinationName} } ?: emptyDest.destinationName) {
            if( startDest == null){
                composable(route = emptyDest.destinationName) { entry ->
                    emptyDest.content(entry,  null)
                }
            }
            entry.destinations.forEach {
                when{
                    it.destination.isExtendInterface(ComposeScreen::class.java) ->{
                        composable(
                            route = it.target,
                            arguments = it.arguments,
                            deepLinks = it.deepLinks,
                            enterTransition = it.enterTransition,
                            exitTransition = it.exitTransition,
                            popEnterTransition = it.popEnterTransition,
                            popExitTransition = it.popExitTransition,
                            sizeTransform = it.sizeTransform
                        
                        ) { entry ->
                            it.destinationInstance.apply {
                                it.connectToLifecycle(LocalLifecycleOwner.current)
                                CompositionLocalProvider(localScopeIdentifier  provides scopeIdentifier){
                                    CompositionLocalProvider(localDestinationClass  provides clasIdentifier){
                                        CompositionLocalProvider(localtarget  provides it.target){
                                            content(entry,  entry.arguments?.getBundle("$ARG${it.destination.name}"))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    it.destination.isExtendInterface(Router.ComposeDialog::class.java) -> {
                        dialog(
                            route = it.target,
                            arguments = it.arguments,
                            deepLinks = it.deepLinks,
                            dialogProperties = it.dialogProperties ?: DialogProperties()
                        ) {entry ->
                            it.destinationInstance.apply {
                                it.connectToLifecycle(LocalLifecycleOwner.current)
                                CompositionLocalProvider(localScopeIdentifier  provides scopeIdentifier){
                                    CompositionLocalProvider(localDestinationClass  provides clasIdentifier){
                                        CompositionLocalProvider(localtarget  provides it.target){
                                            content(entry,  entry.arguments?.getBundle("$ARG${it.destination.name}"))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    it.destination.isExtendInterface(Router.NestedNavigation::class.java) -> {
                        throw Exception("Already not supported")
//                        it.nestedProperties?.let {properties ->
//                            this.navigation(
//                                route = it.rout,
//                                startDestination = properties.startDest?.let {start ->
//                                    if(start in properties.destinations.map{prop ->
//                                            prop.destination}){
//                                        start.name} else {emptyDest.destinationName} } ?: emptyDest.destinationName)
//                            {
//                                //TODO
//                            }
//                        }
                    }
                }
            }
        }
    }
    
    override fun moveTo(rout: Router.Rout, data: Bundle?){
        Log.d("test","move to ${rout} ")
        if(rout is Router.Rout.RoutStub) {
            return
        }
        
        val unionData = rout.data?.let {
            data?.apply {
                it.putAll(this)
            }
            it
        } ?: data
        
        when(rout){
            is Router.MoveBack -> {
                (rout.destAsHostMarker?.let { onHostRout ->
                    composeRoutes.getControllerForDestination(onHostRout)
                } ?: currentRoute?.let {currentRout ->
                    composeRoutes.getControllerForDestination(currentRout)
                })?.apply {
                    if(popBackStackInternal()){
                        rout.keyedData?.let { keyedData ->
                            commonModel?.apply {
                                putNavigateEvent(OnNavigateEvent.BackDataEvent(keyedData.appendData(unionData)))
                            }
                        }
                        rout.onTargetReached?.apply {
                            currentRoute?.let {
                                invoke(it)
                            }
                        }
                        commonModel?.apply {
                            currentRoute?.let {
                                putNavigateEvent(OnNavigateEvent.OnNavigate(it))
                            }
                        }
                    }
                }
            }
            
            else ->{
                composeRoutes.getControllerForDestination(rout.target)?.navigateInternal(rout.target, Bundle().also {
                    it.putBundle("$ARG${rout.target}", unionData) }, rout.options, rout.extras)
                rout.onTargetReached?.apply {
                    currentRoute?.let {
                        invoke(it)
                    }
                }
                commonModel?.apply {
                    currentRoute?.let {
                        putNavigateEvent(OnNavigateEvent.OnNavigate(it))
                    }
                }
            }
        }
    }
    
    private data class ComposeNavigationGraphEntry(var controller: NavHostController?, val destinations: List<DestinationHolder>):
        LifecycleSupport {
        companion object{
            fun List<ComposeNavigationGraphEntry>.getControllerForDestination(rout: String): NavHostController? =
                find {
                    rout in it.destinations.map { destination ->
                        destination.target
                    }
                }?.controller
            
            fun List<ComposeNavigationGraphEntry>.isHasDestination(rout: String): Boolean =
                find {
                    rout in it.destinations.map { destination ->
                        destination.target
                    }
                }?.let{true} ?: false
        }
        
        override fun finalizeTask() {
            controller = null
        }
    }
    
    private data class DestinationHolder(
        val properties: Router.DestinationProperties
    ): LifecycleSupport, Router.DestinationProperties by properties{
        
        private  var _destinationInstance: Router.ComposeDestination? = null
        
        val destinationInstance: Router.ComposeDestination
            get() = _destinationInstance ?:
            try{
                destination.getDeclaredConstructor().newInstance().apply {
                    _destinationInstance = this
                }
            } catch (e: IllegalAccessException){
                e.printStackTrace()
                destination.kotlin.objectInstance?.apply {
                    _destinationInstance = this
                } ?: run{ throw Exception("Unknown create instance error")}
            }
        
        override fun onCreate(owner: LifecycleOwner) {
            _destinationInstance?.onCreate(owner)
        }
        
        override fun onStop(owner: LifecycleOwner) {
            _destinationInstance?.onStop(owner)
        }
        
        override fun onStart(owner: LifecycleOwner) {
            _destinationInstance?.onStart(owner)
        }
        
        override fun onResume(owner: LifecycleOwner) {
            _destinationInstance?.onResume(owner)
        }
        
        override fun onPause(owner: LifecycleOwner) {
            _destinationInstance?.onPause(owner)
        }
        
        override fun onDestroy(owner: LifecycleOwner) {
            _destinationInstance?.onDestroy(owner)
            super.onDestroy(owner)
        }
        
        override fun finalizeTask() {
            _destinationInstance?.finalizeTask()
            _destinationInstance = null
        }
    }
    
    private val emptyDest: ComposeDestination by lazy {
        object : ComposeDestination {
            override val content: @Composable (NavBackStackEntry, Bundle?) -> Unit = { _, _ ->}
        }
    }
}



