package com.starsoft.skeleton.compose.navigation

import android.os.Bundle
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.starsoft.skeleton.compose.baseViewModel.CommonModel
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.skeleton.compose.util.KeyedData
import com.starsoft.skeleton.compose.util.LifecycleSupport

/**
 * Created by Dmitry Starkin on 28.02.2025 14:48.
 */
interface Router: HostCreator {
    
    var commonModel: CommonModel?
    
    fun moveTo(navigationTarget: NavigationTarget, data: Bundle? = null)
  
    interface Target{
        val destination: Class<*>
        val tag: String get() = EMPTY_STRING
        val targetKey: String get() = "${destination.name}$tag"
    }
    
    interface NavigationTarget : Target{
        val options: NavOptions? get() = null
        val extras: Navigator. Extras? get() = null
        val data: Bundle? get() = null
        
        val onTargetReached: ((String) -> Unit)? get() = null
        
        class NavigationTargetStub(): NavigationTarget {
            override val destination = NavigationTargetStub::class.java
        }
    }
    
    interface ComposeDestination: LifecycleSupport {
        val destinationName: String get() = this::class.java.name
        val clasIdentifier get() = this::class
        val scopeIdentifier: String get() = "${destinationName}_${this.hashCode()}"
        val content: @Composable (NavBackStackEntry, Bundle?) -> Unit
        
        fun isTheSame(other: ComposeDestination): Boolean = destinationName == other.destinationName
        
        override fun finalizeTask() {
            //do nothing
        }
    }
    
    interface TargetProperties: Target {
        val targetCreateOptions: TargetCreateOptions? get() = null
        fun isTheSameTarget(other: TargetProperties) = targetKey == other.targetKey
    }
    
    interface TargetCreateOptions{
        val backPressHandleBehavior: BackPressBehavior get() =  BackPressBehavior.BySystem
        val enterTransition: @JvmSuppressWildcards() (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? get() = null
        val exitTransition: @JvmSuppressWildcards() (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? get() = null
        val popEnterTransition: @JvmSuppressWildcards() (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? get() = enterTransition
        val popExitTransition: @JvmSuppressWildcards() (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? get() = exitTransition
        val sizeTransform: @JvmSuppressWildcards() (AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? get() = null
        val arguments: List<NamedNavArgument> get() = emptyList()
        val deepLinks: List<NavDeepLink> get() = emptyList()
        val dialogProperties:  DialogProperties? get() = null
        val nestedProperties: NestedProperties? get() = null
    }
    
    interface NestedProperties{
        val destinations: List<Router.TargetProperties>
        val startDest: Router.TargetProperties? get() =  null
    }
    
    interface ComposeScreen: ComposeDestination
    interface ComposeDialog: ComposeDestination
    interface NestedNavigation: ComposeDestination
    
    enum class BackPressBehavior{
        BySystem,
        Default,
        SendToMe
    }
    
    class Close(): NavigationTarget {
        override val destination = Close::class.java
    }
    
    data class OpenLink(val link: String) : NavigationTarget {
        override val destination: Class<*>
            get() = OpenLink::class.java
    }
    
    data class StopService(private val service:  Class<*>): NavigationTarget {
        override val destination =service
    }
    
    data class MoveBack(val keyedData: KeyedData? = null,
                        val hostMarker: String? = null,
                        override val onTargetReached: ((String) -> Unit)?  = null): NavigationTarget, ComposeDestination {
        override val destination = MoveBack::class.java
        override val content: @Composable (NavBackStackEntry, Bundle?) -> Unit = { _, _ ->}
    }
    
    data class PopUpTo(val where: TargetProperties,
                       override val data: Bundle?,
                       val inclusive: Boolean = true,
                       val saveData: Boolean = true): NavigationTarget, ComposeDestination {
        override val destination get() =  where.destination
        override val content: @Composable (NavBackStackEntry, Bundle?) -> Unit = { _, _ ->}
    }
}