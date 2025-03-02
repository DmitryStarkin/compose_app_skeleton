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
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.skeleton.compose.util.KeyedData


/**
 * Created by Dmitry Starkin on 28.02.2025 16:12.
 */
data class DestinationPropertiesContainer(
        override val destination: Class<out Router.ComposeDestination>,
        override val tag: String = EMPTY_STRING,
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

data class SimpleRout(override val destination: Class<*>,
                      override val tag: String = EMPTY_STRING,
                      override val options: NavOptions?  = null,
                      override val extras: Navigator. Extras?  = null,
                      override val data: Bundle? = null,
                      override val onTargetReached: ((String) -> Unit)? = null
): Router.Rout{
                      
                      }



fun Router.DestinationProperties.simpleRout(tag: String = EMPTY_STRING, data: Bundle? = null, onTargetReached: ((String) -> Unit)? = null) =
    SimpleRout(this@simpleRout.destination, tag, data = data, onTargetReached = onTargetReached)

fun Class<*>.simpleRout(tag: String = EMPTY_STRING, data: Bundle? = null, onTargetReached: ((String) -> Unit)? = null): Router.Rout =
    SimpleRout(this@simpleRout, tag, data = data, onTargetReached = onTargetReached)

fun Class<out Router.ComposeDestination>.moveBackRout(data: KeyedData? = null, tag: String = EMPTY_STRING): Router.MoveBack =
    Router.MoveBack(data, simpleProperties(tag).target)

fun Router.Rout.addPopUpOption(properties: Router.DestinationProperties? = null, inclusive: Boolean = true, saveData: Boolean = false) =
    addPopUpOption(properties?.target, inclusive, saveData)

fun Router.Rout.addPopUpOption(rout: Router.Rout? = null, inclusive: Boolean = true, saveData: Boolean = false): SimpleRout =
    addPopUpOption(rout?.target, inclusive,saveData)

fun Router.Rout.addPopUpOption(popUpRout: String? = null, inclusive: Boolean = true, saveData: Boolean = false): SimpleRout =
    SimpleRout(
        destination,
        tag,
        NavOptions.Builder().setPopUpTo(popUpRout ?: target, inclusive, saveData).setRestoreState(saveData).build(),
        extras,
        data,
        onTargetReached
    )

fun Router.Rout.addSingleTopOption(singleTop : Boolean = true): SimpleRout =
    SimpleRout(
        destination,
        tag,
        NavOptions.Builder().setLaunchSingleTop(singleTop).build(),
        extras,
        data,
        onTargetReached
    )

fun Class<out Router.ComposeDestination>.simpleProperties(tag: String = EMPTY_STRING): Router.DestinationProperties = DestinationPropertiesContainer(this, tag)

fun Router.DestinationProperties.disableDefaultTransitions(): Router.DestinationProperties =
   DestinationPropertiesContainer(
        destination,
        tag,
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

private fun NavOptions?.trySetSingleTop(singleTop: Boolean): NavOptions? =
    if((singleTop != (this?.shouldLaunchSingleTop() == true))  ){
        this?.setLaunchSingleTop(singleTop) ?: NavOptions.Builder().setLaunchSingleTop(singleTop).build()
    } else {
        this
    }

private fun NavOptions?.trySetRestoreState(restoreState: Boolean): NavOptions? =
    if((restoreState != (this?.shouldRestoreState() == true))  ){
        this?.setRestoreState(restoreState) ?: NavOptions.Builder().setLaunchSingleTop(restoreState).build()
    } else {
        this
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