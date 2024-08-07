package com.pk.jamalzahid.faultspy

import android.app.Activity
import android.content.Context
import android.content.Intent

const val TAG = "[Fault_SPY]"
const val THREAD_TITLE = "[ ++ Fault Spy ++ ]"
object GlobalHelper {
    fun restartApp(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(it)
        }
        if (context is Activity) {
            context.finish()
        }
        Runtime.getRuntime().exit(0)
    }

    fun getLog(stackTrace: Array<StackTraceElement>):String{
        return stackTrace.joinToString(separator = "\n") { element ->
            element.toString()
        }
    }
}