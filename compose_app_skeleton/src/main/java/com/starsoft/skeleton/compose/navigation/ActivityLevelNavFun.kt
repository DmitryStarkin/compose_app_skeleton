package com.starsoft.skeleton.compose.navigation

import android.app.Service
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import com.starsoft.skeleton.compose.R
import com.starsoft.skeleton.compose.util.isInstanceOrExtend
import kotlinx.parcelize.Parcelize
import androidx.core.net.toUri

/**
 * Created by Dmitry Starkin on 26.02.2025 15:26.
 */

private val services: HashMap<Class<*>, Intent> = HashMap()

enum class FinishBehavior(val needFinish: Boolean) {
    Finish(true),
    NoFinish(false)
}

private const val CUSTOMIZE_INTENT_FUN_KEY = "com.starsoft.skeleton.compose.navigation.customizeIntentFun"
private const val REPLACE_FLAG_KEY = "com.starsoft.skeleton.compose.navigation.replaceOrFinish"

fun ((Intent) -> Unit).packToBundle(): Bundle =
    bundleOf(
        CUSTOMIZE_INTENT_FUN_KEY to IntentCustomizeWrapper(this)
    )

fun Bundle?.addBackIntentCustomiseFun(customize: (Intent) -> Unit): Bundle =
    this?.let {
        it.putParcelable(CUSTOMIZE_INTENT_FUN_KEY, IntentCustomizeWrapper(customize))
        it
    } ?: customize.packToBundle()

fun Bundle?.getIntentCustomizeFunction(): IntentCustomizeWrapper? =
    if(this == null || !this.containsKey(CUSTOMIZE_INTENT_FUN_KEY)){
        null
    } else {
        getParcelable(CUSTOMIZE_INTENT_FUN_KEY) as IntentCustomizeWrapper?
    }

fun Bundle?.addFinishFlag(finishBehavior: FinishBehavior): Bundle =
    this?.let {
        it.putBoolean(REPLACE_FLAG_KEY, finishBehavior.needFinish)
        it
    } ?: finishBehavior.packToBundle()

fun FinishBehavior.packToBundle(): Bundle =
    bundleOf(
        REPLACE_FLAG_KEY to this.needFinish
    )

fun Bundle?.getRFinishFlag(defValue: FinishBehavior = FinishBehavior.NoFinish): Boolean =
    this?.getBoolean(REPLACE_FLAG_KEY, defValue.needFinish) ?: defValue.needFinish

fun Context.moveToActivity(rout: Class<*>, data: Bundle?) {
    startActivity(Intent(this, rout).apply {
        data?.let {
            it.getIntentCustomizeFunction()?.customize(this)
            putExtras(it)
        }
    }
    )
}

fun Context.moveToService(rout: Class<*>, data: Bundle?) {
    val intent = Intent(this, rout).also {
        data?.let {data ->
            data.getIntentCustomizeFunction()?.customize(it)
            it.putExtras(data)
        }
    }
    services[rout] = intent
    startService(intent)
}

fun Context.openWebLink(
        link: String,
        @StringRes chooserTextId: Int = R.string.select_app_to_open_link
) {
    val intent = Intent(Intent.ACTION_VIEW, link.toUri())
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // needed for call Activity using context outside other Activity
    packageManager?.apply {
        intent.resolveActivity(this)?.apply {
            try {
                startActivity(intent);
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent.createChooser(
                        intent,
                        getString(chooserTextId)
                    )
                )
            }
        } ?: run{
            Log.d("test", "component null ")
            startActivity(
                Intent.createChooser(
                    intent,
                    getString(chooserTextId)
                )
            )
        }
    } ?: run{
        Log.d("test", "component null ")
        startActivity(
            Intent.createChooser(
                intent,
                getString(chooserTextId)
            )
        )
    }
}

fun Context.tryStopAsService(service: Class<*>) {
    if(service.isInstanceOrExtend(Service::class.java) ){
        services.get(service)?.apply {
            stopService(this)
        }
    }
}

@Parcelize
class IntentCustomizeWrapper(val customizeAction: (Intent) -> Unit) : Parcelable {
    fun customize(intent: Intent) = customizeAction(intent)
}