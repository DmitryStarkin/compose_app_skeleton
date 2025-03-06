package com.starsoft.skeleton.compose.navigation

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.window.DialogProperties
import androidx.core.os.bundleOf
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
import com.starsoft.skeleton.compose.baseViewModel.ActivityLevelAction.NavigationAction.Companion.obtainNavigationAction
import com.starsoft.skeleton.compose.baseViewModel.NavigationEvent
import com.starsoft.skeleton.compose.baseViewModel.CommonModel
import com.starsoft.skeleton.compose.navigation.Router.ComposeDestination
import com.starsoft.skeleton.compose.navigation.Router.ComposeScreen
import com.starsoft.skeleton.compose.navigation.RouterImpl.ComposeNavigationGraphEntry.Companion.getControllerForTarget
import com.starsoft.skeleton.compose.navigation.RouterImpl.ComposeNavigationGraphEntry.Companion.updateCurrentIdForDestination
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.skeleton.compose.util.KeyedData
import com.starsoft.skeleton.compose.util.LifecycleSupport
import com.starsoft.skeleton.compose.util.containsAnyItemFrom
import com.starsoft.skeleton.compose.util.isExtendInterface
import kotlinx.parcelize.Parcelize
import kotlin.reflect.KClass

/**
 * Created by Dmitry Starkin on 26.02.2025 14:49.
 */
val  localScopeIdentifier = compositionLocalOf { EMPTY_STRING }
val  localCommonModel = compositionLocalOf<CommonModel?> { error("not init") }
val  localDestinationClass = compositionLocalOf<KClass<*>> { Router.ComposeDestination::class }
val  localNavController = compositionLocalOf<NavController?> { error("not init") }

class RouterImpl: Router {
    companion object{
        const val ARG ="com.starsoft.skeleton.compose.navigation.Router.data_bundle_"
        const val ROUT_FUN_KEY ="om.starsoft.skeleton.compose.navigation.Router.routFun"
        
            fun ((String) -> Unit)?.packToBundle(): Bundle? =
                this?.let {
                    bundleOf(
                        ROUT_FUN_KEY to SussesCallbackWrapper(it)
                    )
                }
        
            fun Bundle?.addTargetReachedFunction(onTarget: ((String) -> Unit)?): Bundle? =
                this?.let {
                    if(onTarget != null){
                        it.putParcelable(ROUT_FUN_KEY, SussesCallbackWrapper(onTarget))
                    }
                    it
                } ?: onTarget.packToBundle()
            
            private fun Bundle?.getSussesCallbackFunction(): SussesCallbackWrapper? =
                if(this == null || !this.containsKey(ROUT_FUN_KEY)){
                    null
                } else {
                    getParcelable(ROUT_FUN_KEY) as SussesCallbackWrapper?
                }
    }
    
    private val composeRoutes: ArrayList<ComposeNavigationGraphEntry> = ArrayList()
    private var currentRoute: String? = null
    private var currentEntryId: String? = null
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
    
    private fun  ArrayList<ComposeNavigationGraphEntry>.getTargets(): List<String> {
        val result = ArrayList<String>()
        forEach {
            it.destinations.forEach{ properties ->
                result.add(properties.target)
            }
        }
        return result
    }
    
    private fun List<Router.DestinationProperties>.getTargets(): List<String> =
        map{
            it.target
        }
    
    private fun  ArrayList<ComposeNavigationGraphEntry>.isContainRoutFrom(entry: ComposeNavigationGraphEntry): Boolean =
        getTargets().containsAnyItemFrom(entry.destinations.map{
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
        currentEntryId = this.currentBackStackEntry?.id
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
         if(!popped){
             currentRoute = null
             commonModel?.performActivityLevelAction(obtainNavigationAction(Router.Close()))
         }
         currentEntryId = this.currentBackStackEntry?.id
        return popped
    }
    
    @Composable
    override fun CreateNavHostHere(
            navController: NavHostController,
            destinations: List<Router.DestinationProperties>,
            startDest: Router.DestinationProperties?
    ){
        if(destinations.isEmpty() || (startDest != null && startDest.target !in destinations.getTargets())) return
        composeRoutes.removeEmpty()
        val entry = ComposeNavigationGraphEntry(navController, destinations.map { DestinationsHolder(it) })
        if(composeRoutes.isContain(entry)) return
        if(composeRoutes.isContainRoutFrom(entry)) throw Exception("Rout already exists in tree use tag for unique")
        composeRoutes.add(0, entry.also {
            it.connectToLifecycle(LocalLifecycleOwner.current)
        })
        CreateNavHost(entry, startDest?.target)
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
                            arguments = it.destCreateOptions?.arguments ?: emptyList(),
                            deepLinks = it.destCreateOptions?.deepLinks ?: emptyList(),
                            enterTransition = it.destCreateOptions?.enterTransition,
                            exitTransition = it.destCreateOptions?.exitTransition,
                            popEnterTransition = it.destCreateOptions?.popEnterTransition ?: it.destCreateOptions?.enterTransition,
                            popExitTransition = it.destCreateOptions?.popExitTransition ?: it.destCreateOptions?.exitTransition,
                            sizeTransform = it.destCreateOptions?.sizeTransform
                        
                        ) { entry ->
                            Log.d("test"," ")
                            Log.d("test","ENTRY ID for ${it.destination.simpleName}  ${entry.id}")
                            Log.d("test","currentEntryId ${currentEntryId}")
                            it.getInstance(entry.id, LocalLifecycleOwner.current).apply {
                                Log.d("test","${it.destination.simpleName} get instanse ${this.hashCode()} Lifecycle ${LocalLifecycleOwner.current.hashCode()}")
                                CompositionLocalProvider(localScopeIdentifier  provides scopeIdentifier){
                                    CompositionLocalProvider(localDestinationClass  provides clasIdentifier){
                                        CompositionLocalProvider(localCommonModel  provides commonModel) {
                                            CompositionLocalProvider(localNavController  provides composeRoutes.getControllerForTarget(it.target)) {
                                            if (it.destCreateOptions?.backPressHandleBehavior == Router.BackPressBehavior.SendToMe) {
                                                BackHandler(onBack = { commonModel?.onBackPressed(it.target) })
                                            } else if (it.destCreateOptions?.backPressHandleBehavior == Router.BackPressBehavior.Default) {
                                                //todo
                                                BackHandler(onBack = { moveTo(Router.MoveBack()) })
                                            }
                                            //TODO maybe crutch
                                            if (composeRoutes.updateCurrentIdForDestination(it.target, entry.id) == null) {
                                                Log.d("test", "send navigation in compose")
                                                commonModel?.apply {
                                                    putNavigateEvent(NavigationEvent.NavigateSusses(it.target ?: EMPTY_STRING))
                                                }
                                            }
                                            content(entry, entry.arguments?.getBundle("$ARG${it.destination.name}"))
                                        }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    it.destination.isExtendInterface(Router.ComposeDialog::class.java) -> {
                        dialog(
                            route = it.target,
                            arguments = it.destCreateOptions?.arguments ?: emptyList(),
                            deepLinks = it.destCreateOptions?.deepLinks ?: emptyList(),
                            dialogProperties = it.destCreateOptions?.dialogProperties ?: DialogProperties()
                        ) {entry ->
                            it.getInstance(entry.id, LocalLifecycleOwner.current).apply {
                                CompositionLocalProvider(localScopeIdentifier  provides scopeIdentifier){
                                    CompositionLocalProvider(localDestinationClass  provides clasIdentifier){
                                        CompositionLocalProvider(localCommonModel  provides commonModel){
                                            CompositionLocalProvider(localNavController  provides composeRoutes.getControllerForTarget(it.target)) {
                                                if (it.destCreateOptions?.backPressHandleBehavior == Router.BackPressBehavior.SendToMe) {
                                                    BackHandler(onBack = { commonModel?.onBackPressed(it.target) })
                                                } else if (it.destCreateOptions?.backPressHandleBehavior == Router.BackPressBehavior.Default) {
                                                    //todo
                                                    BackHandler(onBack = { moveTo(Router.MoveBack()) })
                                                }
                                                //TODO maybe crutch
                                                if (composeRoutes.updateCurrentIdForDestination(it.target, entry.id) == null) {
                                                    Log.d("test", "send navigation in compose")
                                                    commonModel?.apply {
                                                        putNavigateEvent(NavigationEvent.NavigateSusses(it.target ?: EMPTY_STRING))
                                                    }
                                                }
                                                content(entry, entry.arguments?.getBundle("$ARG${it.destination.name}"))
                                            }
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
    
    override fun moveTo(navigationTarget: Router.NavigationTarget, data: Bundle?){
        Log.d("test","move to ${navigationTarget} ")
        Log.d("test","current route ${currentRoute} ")
        if(navigationTarget is Router.NavigationTarget.NavigationTargetStub) {
            return
        }
        val currId = currentEntryId
        val unionData = navigationTarget.data?.let {
            data?.apply {
                it.putAll(this)
            }
            it
        } ?: data
        //TODO remove log
        fun logSuses(){
            if(currId != currentEntryId){
                Log.d("test","navigate susses ")}
        }
        
        fun afterNavigate(keyedData: KeyedData? = null){
            if(currId != currentEntryId){
                keyedData?.let { data ->
                    commonModel?.apply {
                        putNavigateEvent(NavigationEvent.BackDataEvent(data.appendData(unionData)))
                    }
                }
                navigationTarget.onTargetReached?.apply {
                    invoke(currentRoute ?: EMPTY_STRING)
                }
                commonModel?.apply {
                        putNavigateEvent(NavigationEvent.NavigateSusses(currentRoute ?: EMPTY_STRING))
                }
            }
        }
        
        when(navigationTarget){
            is Router.MoveBack -> {
                (navigationTarget.hostMarker?.let { onHostRout ->
                    composeRoutes.getControllerForTarget(onHostRout)
                } ?: currentRoute?.let {currentRout ->
                    composeRoutes.getControllerForTarget(currentRout)
                })?.apply {
                    Log.d("test","try popBackStack with ${this.hashCode()} ")
                    popBackStackInternal()
                    logSuses()
                    logStack("after navigation")
                    afterNavigate(navigationTarget.keyedData)
                } ?: run{
                    commonModel?.performActivityLevelAction(obtainNavigationAction(Router.Close()))
                }
            }
            
            is Router.PopUpTo -> {
                TODO()
            }
            
            else -> {
                Log.d("test","try move to ${navigationTarget.target} ")
                composeRoutes.getControllerForTarget(navigationTarget.target)?.apply {
                    navigateInternal(navigationTarget.target, Bundle().also {
                                                                    it.putBundle("$ARG${navigationTarget.target}", unionData)
                                                                    //it.addTargetReachedFunction(rout.onTargetReached)
                                                                }, navigationTarget.options, navigationTarget.extras)
                    logSuses()
                    logStack("after navigation")
                } ?: run{
                    commonModel?.performActivityLevelAction(obtainNavigationAction(Router.Close()))
                    Exception("null controller in navigate").printStackTrace()
                }
                afterNavigate()
            }
            
            
        }
    }
    
    private data class ComposeNavigationGraphEntry(var controller: NavHostController?, val destinations: List<DestinationsHolder>):
        LifecycleSupport {
            var currentEntryId: String? = null
            companion object{
                fun List<ComposeNavigationGraphEntry>.getControllerForTarget(target: String): NavHostController? =
                    find {
                        target in it.destinations.map { destination ->
                            destination.target
                        }
                    }?.controller?.also {
                        //TODO
                        it.logStack("Before navigate")
                    }
                
                fun List<ComposeNavigationGraphEntry>.isHasDestination(target: String): Boolean =
                    find {
                        target in it.destinations.map { destination ->
                            destination.target
                        }
                    }?.let{true} ?: false
                
                fun List<ComposeNavigationGraphEntry>.updateCurrentIdForDestination(target: String, id: String): String? =
                    find {
                        target in it.destinations.map { destination ->
                            destination.target
                        }
                    }?.let {
                        val current = it.currentEntryId
                        it.currentEntryId = id
                        current
                    }
        }
        
        override fun finalizeTask() {
            controller = null
            currentEntryId = null
        }
    }
    
    private data class DestinationsHolder(
        val properties: Router.DestinationProperties
    ): Router.DestinationProperties by properties{
        
        private val instances: ArrayList<DestinationInstanceHolder> = ArrayList()
        
        private fun  ArrayList<DestinationInstanceHolder>.removeEmpty() {
            val new = this.mapNotNull {
                if (it.destinationInstance == null) {
                    null
                } else {
                    it
                }
            }
            this.clear()
            this.addAll(new)
        }
        
        fun getInstance(id: String, owner: LifecycleOwner): Router.ComposeDestination {
            instances.removeEmpty()
            return instances.find {
                it.id == id && it.destinationInstance != null
            }?.destinationInstance ?: run{
                DestinationInstanceHolder(properties, id).let {
                    it.connectToLifecycle(owner)
                    instances.add(it)
                    it.destinationInstance!!
                }
            }
        }
    }
    
    private  class DestinationInstanceHolder(properties: Router.DestinationProperties, val id: String): LifecycleSupport{
        val destinationInstance get() = _destinationInstance
        
        private var _destinationInstance: Router.ComposeDestination? =
            
            try {
                properties.destination.getDeclaredConstructor().newInstance().apply {
                    _destinationInstance = this
                }
            } catch (e: IllegalAccessException){
                e.printStackTrace()
                properties.destination.kotlin.objectInstance?.apply {
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
    
    @Parcelize
    class SussesCallbackWrapper(val onTarget: (String) -> Unit) : Parcelable {
        fun onTargetReached(target: String) = onTarget(target)
    }
}

//TODO remove log
fun NavHostController.logStack(startMsg: String = EMPTY_STRING): NavHostController{
    
    Log.d("test"," ")
    Log.d("test","controller ${this.hashCode()} ")
    Log.d("test",startMsg)
    currentBackStack.value.map {
        Pair(it.destination.route, it.id)
    }?.forEach {
        Log.d("test","${it.first} ${it.second}")
    }
    Log.d("test"," ")
    return this
}



