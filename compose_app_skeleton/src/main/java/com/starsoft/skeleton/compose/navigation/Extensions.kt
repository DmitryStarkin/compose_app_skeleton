package com.starsoft.skeleton.compose.navigation

import android.os.Bundle
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.starsoft.skeleton.compose.navigation.Router.BackPressBehavior
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.skeleton.compose.util.KeyedData


/**
 * Created by Dmitry Starkin on 28.02.2025 16:12.
 */
data class DestinationPropertiesContainer(
        override val destination: Class<out Router.ComposeDestination>,
        override val tag: String = EMPTY_STRING,
        override val backPressHandleBehavior: BackPressBehavior  =  BackPressBehavior.BySystem,
        override val enterTransition: @JvmSuppressWildcards() (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
        override val exitTransition: @JvmSuppressWildcards() (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
        override val popEnterTransition: @JvmSuppressWildcards() (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = enterTransition,
        override val popExitTransition: @JvmSuppressWildcards() (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = exitTransition,
        override val sizeTransform: @JvmSuppressWildcards() (AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? = null,
        override val arguments: List<NamedNavArgument> = emptyList(),
        override val deepLinks: List<NavDeepLink> = emptyList(),
        override val dialogProperties:  DialogProperties? = null,
        override val nestedProperties: Router.NestedProperties? = null
): Router.DestinationProperties

data class NestedPropertiesContainer(
        val destinations: List<Router.DestinationProperties>,
        val startDest: Router.DestinationProperties? = null)

data class SimpleNavigationTarget(override val destination: Class<*>,
                                  override val tag: String = EMPTY_STRING,
                                  override val options: NavOptions?  = null,
                                  override val extras: Navigator. Extras?  = null,
                                  override val data: Bundle? = null,
                                  override val onTargetReached: ((String) -> Unit)? = null
): Router.NavigationTarget

fun Router.DestinationProperties.simpleTarget(data: Bundle? = null, onTargetReached: ((String) -> Unit)? = null) =
    SimpleNavigationTarget(destination, tag, data = data, onTargetReached = onTargetReached)

fun Class<*>.simpleTarget(tag: String = EMPTY_STRING, data: Bundle? = null, onTargetReached: ((String) -> Unit)? = null): Router.NavigationTarget =
    SimpleNavigationTarget(this@simpleTarget, tag, data = data, onTargetReached = onTargetReached)

fun Class<out Router.ComposeDestination>.moveBackTarget(data: KeyedData? = null, tag: String = EMPTY_STRING): Router.MoveBack =
    Router.MoveBack(data, simpleProperties(tag).target)

fun Router.NavigationTarget.addPopUpOption(properties: Router.DestinationProperties? = null, inclusive: Boolean = true, saveData: Boolean = false) =
    addPopUpOption(properties?.target, inclusive, saveData)

fun Router.NavigationTarget.addPopUpOption(navigationTarget: Router.NavigationTarget? = null, inclusive: Boolean = true, saveData: Boolean = false): SimpleNavigationTarget =
    addPopUpOption(navigationTarget?.target, inclusive,saveData)

fun Router.NavigationTarget.addPopUpOption(popUpRout: String? = null, inclusive: Boolean = true, saveData: Boolean = false): SimpleNavigationTarget =
    SimpleNavigationTarget(
        destination,
        tag,
        options.trySetPopUpOption(popUpRout ?: target, inclusive, saveData),
        extras,
        data,
        onTargetReached
    )

fun Router.NavigationTarget.addSingleTopOption(singleTop : Boolean = true): SimpleNavigationTarget =
    SimpleNavigationTarget(
        destination,
        tag,
        options.trySetSingleTop(singleTop),
        extras,
        data,
        onTargetReached
    )

fun Router.NavigationTarget.addRestoreStateOption(restore : Boolean = true): SimpleNavigationTarget =
    SimpleNavigationTarget(
        destination,
        tag,
        options.trySetRestoreState(restore),
        extras,
        data,
        onTargetReached
    )

fun Class<out Router.ComposeDestination>.simpleProperties(tag: String = EMPTY_STRING): Router.DestinationProperties = DestinationPropertiesContainer(this, tag)

fun Router.DestinationProperties.addBackButtonBehavior(behavior: BackPressBehavior): Router.DestinationProperties =
    DestinationPropertiesContainer(
        destination,
        tag,
        behavior,
        enterTransition ?: { EnterTransition.None },
        exitTransition ?: { ExitTransition.None },
        popEnterTransition ?: { EnterTransition.None },
        popExitTransition ?: { ExitTransition.None },
        sizeTransform,
        arguments,
        deepLinks,
        dialogProperties,
        nestedProperties
    )

fun Router.DestinationProperties.disableDefaultTransitions(): Router.DestinationProperties =
   DestinationPropertiesContainer(
        destination,
        tag,
        backPressHandleBehavior,
        enterTransition ?: { EnterTransition.None },
        exitTransition ?: { ExitTransition.None },
        popEnterTransition ?: { EnterTransition.None },
        popExitTransition ?: { ExitTransition.None },
        sizeTransform,
        arguments,
        deepLinks,
        dialogProperties,
        nestedProperties
    )

private fun NavOptions?.trySetPopUpOption(popUpRout: String? = null, inclusive: Boolean = true, saveData: Boolean = false): NavOptions? =
    if(this?.popUpToRoute == null)  {
        this?.setPopUpOption(popUpRout, inclusive, saveData) ?:
        NavOptions.Builder().setPopUpTo(popUpRout, inclusive, saveData).setRestoreState(saveData).build()
    } else {
        this
    }

private fun NavOptions?.trySetSingleTop(singleTop: Boolean): NavOptions? =
    if((singleTop != (this?.shouldLaunchSingleTop() == true))  ){
        this?.setLaunchSingleTop(singleTop) ?: NavOptions.Builder().setLaunchSingleTop(singleTop).build()
    } else {
        this
    }

private fun NavOptions?.trySetRestoreState(restoreState: Boolean): NavOptions? =
    if((restoreState != (this?.shouldRestoreState() == true))  ){
        this?.setRestoreState(restoreState) ?: NavOptions.Builder().setRestoreState(restoreState).build()
    } else {
        this
    }

private fun NavOptions.setPopUpOption(popUpRout: String? = null, inclusive: Boolean = true, saveData: Boolean = false): NavOptions =
    NavOptions.Builder().let {
            it.setPopUpTo(popUpRout, inclusive, saveData)
            it.setRestoreState(this.shouldRestoreState())
            it.setLaunchSingleTop(this.shouldLaunchSingleTop())
            it.build()
        }

private fun NavOptions.setLaunchSingleTop(launch: Boolean): NavOptions  =
    NavOptions.Builder().let {
        this.popUpToRoute?.let {route ->
            it.setPopUpTo(route, this.isPopUpToInclusive(), this.shouldRestoreState()) }
            it.setRestoreState(this.shouldRestoreState())
            it.setLaunchSingleTop(launch)
            it.build()
        }

private fun NavOptions.setRestoreState(restore: Boolean): NavOptions  =
    NavOptions.Builder().let {
        this.popUpToRoute?.let {route ->
            it.setPopUpTo(route, this.isPopUpToInclusive(), this.shouldRestoreState()) }
            it.setRestoreState(restore)
            it.setLaunchSingleTop(this.shouldLaunchSingleTop())
            it.build()
        }