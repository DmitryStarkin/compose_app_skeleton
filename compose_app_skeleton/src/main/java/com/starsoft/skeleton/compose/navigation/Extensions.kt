package com.starsoft.skeleton.compose.navigation

import android.os.Bundle
import android.os.Parcelable
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
import com.starsoft.skeleton.compose.navigation.TargetCreateOptionsContainer.Companion.pack
import com.starsoft.skeleton.compose.navigation.Router.BackPressBehavior
import com.starsoft.skeleton.compose.navigation.TargetContainer.Companion.pack
import com.starsoft.skeleton.compose.util.EMPTY_STRING
import com.starsoft.skeleton.compose.util.KeyedData
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


/**
 * Created by Dmitry Starkin on 28.02.2025 16:12.
 */
@Parcelize
data class TargetContainer(
        override val destination: Class<*>,
        override val tag: String = EMPTY_STRING
): Router.Target, Parcelable {
    companion object{
        fun Router.Target.pack(): TargetContainer =
            if(this is TargetContainer){
                this
            } else {
                TargetContainer(
                    destination,
                    tag
                )
            }
    }
}



val Router.TargetProperties.target : Router.Target get() =
        TargetContainer(
            destination,
            tag
        )

data class TargetCreateOptionsContainer(
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
): Router.TargetCreateOptions{
    companion object{
        
        fun Router.TargetCreateOptions.pack(): TargetCreateOptionsContainer =
            if(this is TargetCreateOptionsContainer){
                this
            } else {
                TargetCreateOptionsContainer(
                    backPressHandleBehavior,
                    enterTransition,
                    exitTransition,
                    popEnterTransition,
                    popExitTransition,
                    sizeTransform,
                    arguments,
                    deepLinks,
                    dialogProperties,
                    nestedProperties
                )
            }
    }
}

data class TargetPropertiesContainer(
        override val destination: Class<*>,
        override val tag: String = EMPTY_STRING,
        override  val targetCreateOptions: Router.TargetCreateOptions? = null
): Router.TargetProperties

data class NestedPropertiesContainer(
        val destinations: List<Router.TargetProperties>,
        val startDest: Router.TargetProperties? = null)

@Parcelize
data class NavigationTargetContainer(override val destination: Class<*>,
                                     override val tag: String = EMPTY_STRING,
                                     override val parentTargets: List<TargetContainer>? = null,
                                     override val options: @RawValue NavOptions?  = null,
                                     override val extras: @RawValue Navigator. Extras?  = null,
                                     override val data: Bundle? = null,
                                     override val onTargetReached: ((String) -> Unit)? = null
): Router.NavigationTarget, Parcelable{
    companion object{
        fun Router.NavigationTarget.pack(): NavigationTargetContainer = if(this is NavigationTargetContainer){
            this
        } else {
            NavigationTargetContainer(
                destination,
                tag,
                parentTargets?.map {
                    it.pack()
                },
                options,
                extras,
                data,
                onTargetReached
            )
        }
    }
}

fun Router.Target.asNavTarget(data: Bundle? = null, parentTargets: List<TargetContainer>? = null, onTargetReached: ((String) -> Unit)? = null) =
    NavigationTargetContainer(destination, tag, data = data, parentTargets = parentTargets, onTargetReached = onTargetReached)

fun Class<*>.asNavTarget(tag: String = EMPTY_STRING, parentTargets: List<TargetContainer>? = null, data: Bundle? = null, onTargetReached: ((String) -> Unit)? = null): Router.NavigationTarget =
    NavigationTargetContainer(this@asNavTarget, tag, data = data, parentTargets = parentTargets, onTargetReached = onTargetReached)

fun Class<out Router.ComposeDestination>.moveBackTarget(data: KeyedData? = null, tag: String = EMPTY_STRING): Router.MoveBack =
    Router.MoveBack(data, asTargetProperties(tag).targetKey)

fun Router.NavigationTarget.addPopUpOption(properties: Router.TargetProperties? = null, inclusive: Boolean = true, saveData: Boolean = false) =
    addPopUpOption(properties?.targetKey, inclusive, saveData)

fun Router.NavigationTarget.addPopUpOption(navigationTarget: Router.NavigationTarget? = null, inclusive: Boolean = true, saveData: Boolean = false): NavigationTargetContainer =
    addPopUpOption(navigationTarget?.targetKey, inclusive,saveData)

fun Router.NavigationTarget.addPopUpOption(popUpRout: String? = null, inclusive: Boolean = true, saveData: Boolean = false): NavigationTargetContainer =
    NavigationTargetContainer(
        destination,
        tag,
        parentTargets?.map {
            it.pack()
        },
        options.trySetPopUpOption(popUpRout ?: targetKey, inclusive, saveData),
        extras,
        data,
        onTargetReached
    )

fun Router.NavigationTarget.addSingleTopOption(singleTop : Boolean = true): NavigationTargetContainer =
    NavigationTargetContainer(
        destination,
        tag,
        parentTargets?.map {
            it.pack()
        },
        options.trySetSingleTop(singleTop),
        extras,
        data,
        onTargetReached
    )

fun Router.NavigationTarget.replaceData(dataToReplace: Bundle?): NavigationTargetContainer =
    NavigationTargetContainer(
        destination,
        tag,
        parentTargets?.map {
            it.pack()
        },
        options,
        extras,
        dataToReplace,
        onTargetReached
    )

fun Router.NavigationTarget.addRestoreStateOption(restore : Boolean = true): NavigationTargetContainer =
    NavigationTargetContainer(
        destination,
        tag,
        parentTargets?.map {
            it.pack()
        },
        options.trySetRestoreState(restore),
        extras,
        data,
        onTargetReached
    )

fun Class<out Router.ComposeDestination>.asTargetProperties(tag: String = EMPTY_STRING,
                                                            targetCreateOptions: Router.TargetCreateOptions? = null): Router.TargetProperties =
    TargetPropertiesContainer(this, tag, targetCreateOptions)

fun Class<out Router.ComposeDestination>.asTarget(tag: String = EMPTY_STRING): Router.Target =
    TargetContainer(this)

fun Router.TargetProperties.addBackButtonBehavior(behavior: BackPressBehavior): Router.TargetProperties =
    TargetPropertiesContainer(
        destination,
        tag,
        targetCreateOptions?.pack()?.copy(backPressHandleBehavior = behavior) ?: TargetCreateOptionsContainer(backPressHandleBehavior = behavior)
    )

fun Router.TargetProperties.disableDefaultTransitions(): Router.TargetProperties =
   TargetPropertiesContainer(
       destination,
       tag,
       targetCreateOptions?.pack()?.copy(
           enterTransition = { EnterTransition.None },
           exitTransition = { ExitTransition.None },
           popEnterTransition = { EnterTransition.None },
           popExitTransition = { ExitTransition.None }
       ) ?: TargetCreateOptionsContainer(
           enterTransition = { EnterTransition.None },
           exitTransition = { ExitTransition.None },
           popEnterTransition = { EnterTransition.None },
           popExitTransition = { ExitTransition.None }
       )
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