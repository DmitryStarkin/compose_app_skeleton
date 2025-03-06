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
    
    private val composeTargets: ArrayList<ComposeNavigationGraphEntry> = ArrayList()
    private var currentTarget: String? = null
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
                result.add(properties.targetKey)
            }
        }
        return result
    }
    
    private fun List<Router.TargetProperties>.getTargets(): List<String> =
        map{
            it.targetKey
        }
    
    private fun  ArrayList<ComposeNavigationGraphEntry>.isContainRoutFrom(entry: ComposeNavigationGraphEntry): Boolean =
        getTargets().containsAnyItemFrom(entry.destinations.map{
            it.targetKey
        })
    
    private fun  ArrayList<ComposeNavigationGraphEntry>.isContain(entry: ComposeNavigationGraphEntry): Boolean =
        find {
            it.controller == entry.controller && it.destinations.isTheSame(entry.destinations)
            
        } != null
    
    private fun List<Router.Target>.isTheSame(other: List<Router.Target>): Boolean =
        if(size != other.size){
            false
        } else {
            zip(other).all {
                it.first.targetKey == it.second.targetKey
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
        currentTarget = this.currentDestination?.route
        currentEntryId = this.currentBackStackEntry?.id
    }
    
    private fun NavController.popBackStackInternal(): Boolean
     {
        val popped = popBackStack()
         if(popped){
             currentTarget = this.currentDestination?.route
             if(currentTarget == emptyDest.destinationName){
                 return popBackStackInternal()
             }
         }
         if(!popped){
             currentTarget = null
             commonModel?.performActivityLevelAction(obtainNavigationAction(Router.Close()))
         }
         currentEntryId = this.currentBackStackEntry?.id
        return popped
    }
    
    @Composable
    override fun CreateNavHostHere(
            navController: NavHostController,
            targets: List<Router.TargetProperties>,
            startTarget: Router.Target?
    ){
        if(targets.isEmpty() || (startTarget != null && startTarget.targetKey !in targets.getTargets())) return
        composeTargets.removeEmpty()
        val entry = ComposeNavigationGraphEntry(navController, targets.map { DestinationsHolder(it) })
        if(composeTargets.isContain(entry)) return
        if(composeTargets.isContainRoutFrom(entry)) throw Exception("Rout already exists in tree use tag for unique")
        composeTargets.add(0, entry.also {
            it.connectToLifecycle(LocalLifecycleOwner.current)
        })
        CreateNavHost(entry, startTarget?.targetKey)
    }
    
    @Composable
    override fun CreateNavHostHere(
            navController: NavHostController,
            targets: ListWrapper<Class<out Router.ComposeDestination>>,
            startTarget: Class<out Router.ComposeDestination>?
    ){
        CreateNavHostHere( navController, targets.map { it.asTargetProperties() }, startTarget?.asTarget())
    }
    
    @Composable
    private fun CreateNavHost(
            entry: ComposeNavigationGraphEntry,
            startDest: String? = null
    ){
        NavHost(entry.controller!!, startDestination = startDest?.let { if(it in entry.destinations.map{it.targetKey}){it} else {emptyDest.destinationName} } ?: emptyDest.destinationName) {
            if( startDest == null){
                composable(route = emptyDest.destinationName) { entry ->
                    emptyDest.content(entry,  null)
                }
            }
            entry.destinations.forEach {
                when{
                    it.destination.isExtendInterface(ComposeScreen::class.java) -> {
                        composable(
                            route = it.targetKey,
                            arguments = it.targetCreateOptions?.arguments ?: emptyList(),
                            deepLinks = it.targetCreateOptions?.deepLinks ?: emptyList(),
                            enterTransition = it.targetCreateOptions?.enterTransition,
                            exitTransition = it.targetCreateOptions?.exitTransition,
                            popEnterTransition = it.targetCreateOptions?.popEnterTransition ?: it.targetCreateOptions?.enterTransition,
                            popExitTransition = it.targetCreateOptions?.popExitTransition ?: it.targetCreateOptions?.exitTransition,
                            sizeTransform = it.targetCreateOptions?.sizeTransform
                        
                        ) { entry ->
                            Log.d("test"," ")
                            Log.d("test","ENTRY ID for ${it.destination.simpleName}  ${entry.id}")
                            Log.d("test","currentEntryId ${currentEntryId}")
                            it.getInstance(entry.id, LocalLifecycleOwner.current).apply {
                                Log.d("test","${it.destination.simpleName} get instanse ${this.hashCode()} Lifecycle ${LocalLifecycleOwner.current.hashCode()}")
                                CompositionLocalProvider(localScopeIdentifier  provides scopeIdentifier){
                                    CompositionLocalProvider(localDestinationClass  provides clasIdentifier){
                                        CompositionLocalProvider(localCommonModel  provides commonModel) {
                                            CompositionLocalProvider(localNavController  provides composeTargets.getControllerForTarget(it.targetKey)) {
                                            if (it.targetCreateOptions?.backPressHandleBehavior == Router.BackPressBehavior.SendToMe) {
                                                BackHandler(onBack = { commonModel?.onBackPressed(it.targetKey) })
                                            } else if (it.targetCreateOptions?.backPressHandleBehavior == Router.BackPressBehavior.Default) {
                                                //todo
                                                BackHandler(onBack = { moveTo(Router.MoveBack()) })
                                            }
                                            //TODO maybe crutch
                                            if (composeTargets.updateCurrentIdForDestination(it.targetKey, entry.id) == null) {
                                                Log.d("test", "send navigation in compose")
                                                commonModel?.apply {
                                                    putNavigateEvent(NavigationEvent.NavigateSusses(it.targetKey ?: EMPTY_STRING))
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
                            route = it.targetKey,
                            arguments = it.targetCreateOptions?.arguments ?: emptyList(),
                            deepLinks = it.targetCreateOptions?.deepLinks ?: emptyList(),
                            dialogProperties = it.targetCreateOptions?.dialogProperties ?: DialogProperties()
                        ) {entry ->
                            it.getInstance(entry.id, LocalLifecycleOwner.current).apply {
                                CompositionLocalProvider(localScopeIdentifier  provides scopeIdentifier){
                                    CompositionLocalProvider(localDestinationClass  provides clasIdentifier){
                                        CompositionLocalProvider(localCommonModel  provides commonModel){
                                            CompositionLocalProvider(localNavController  provides composeTargets.getControllerForTarget(it.targetKey)) {
                                                if (it.targetCreateOptions?.backPressHandleBehavior == Router.BackPressBehavior.SendToMe) {
                                                    BackHandler(onBack = { commonModel?.onBackPressed(it.targetKey) })
                                                } else if (it.targetCreateOptions?.backPressHandleBehavior == Router.BackPressBehavior.Default) {
                                                    //todo
                                                    BackHandler(onBack = { moveTo(Router.MoveBack()) })
                                                }
                                                //TODO maybe crutch
                                                if (composeTargets.updateCurrentIdForDestination(it.targetKey, entry.id) == null) {
                                                    Log.d("test", "send navigation in compose")
                                                    commonModel?.apply {
                                                        putNavigateEvent(NavigationEvent.NavigateSusses(it.targetKey ?: EMPTY_STRING))
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
        Log.d("test","current route ${currentTarget} ")
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
                    invoke(currentTarget ?: EMPTY_STRING)
                }
                commonModel?.apply {
                        putNavigateEvent(NavigationEvent.NavigateSusses(currentTarget ?: EMPTY_STRING))
                }
            }
        }
        
        when(navigationTarget){
            is Router.MoveBack -> {
                (navigationTarget.hostMarker?.let { onHostRout ->
                    composeTargets.getControllerForTarget(onHostRout)
                } ?: currentTarget?.let { currentRout ->
                    composeTargets.getControllerForTarget(currentRout)
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
                Log.d("test","try move to ${navigationTarget.targetKey} ")
                composeTargets.getControllerForTarget(navigationTarget.targetKey)?.apply {
                    navigateInternal(navigationTarget.targetKey, Bundle().also {
                                                                    it.putBundle("$ARG${navigationTarget.targetKey}", unionData)
                                                                    //it.addTargetReachedFunction(rout.onTargetReached)
                                                                }, navigationTarget.options, navigationTarget.extras)
                    logSuses()
                    logStack("after navigation")
                } ?: run{
                    commonModel?.performActivityLevelAction(obtainNavigationAction(Router.Close()))
                    Exception("controller for target not found").printStackTrace()
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
                            destination.targetKey
                        }
                    }?.controller?.also {
                        //TODO
                        it.logStack("Before navigate")
                    }
                
                fun List<ComposeNavigationGraphEntry>.isHasDestination(target: String): Boolean =
                    find {
                        target in it.destinations.map { destination ->
                            destination.targetKey
                        }
                    }?.let{true} ?: false
                
                fun List<ComposeNavigationGraphEntry>.updateCurrentIdForDestination(target: String, id: String): String? =
                    find {
                        target in it.destinations.map { destination ->
                            destination.targetKey
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
        val targets: Router.TargetProperties
    ): Router.TargetProperties by targets{
        
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
                DestinationInstanceHolder(targets, id).let {
                    it.connectToLifecycle(owner)
                    instances.add(it)
                    it.destinationInstance!!
                }
            }
        }
    }
    
    private  class DestinationInstanceHolder(properties: Router.Target, val id: String): LifecycleSupport {
        val destinationInstance get() = _destinationInstance
        
        private var _destinationInstance: Router.ComposeDestination? =
            
            try {
                properties.destination.getDeclaredConstructor().newInstance()?.let {
                    if(it is Router.ComposeDestination){
                        it
                    } else {
                        throw Exception("Must be Router.ComposeDestination")
                    }
                }
            } catch (e: IllegalAccessException){
                e.printStackTrace()
                properties.destination.kotlin.objectInstance?.let {
                    if(it is Router.ComposeDestination){
                        it
                    } else {
                        throw Exception("Must be Router.ComposeDestination")
                    }
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



