package com.pk.jamalzahid.faultspy

import android.content.Context
import android.content.Intent
import com.google.firebase.crashlytics.FirebaseCrashlytics


/**
 * @copyright
 * Copyright (c) 2023  M. JAMAL
 * All rights reserved.
 * Created by M. JAMAL on 12/07/2024
 * @author M. JAMAL
 */

class ExceptionHandler(
    val context: Context,
    private val crashlytics: FirebaseCrashlytics,
    private val appAction: AppAction?
) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(thread: Thread, exception: Throwable) {
        val msg = "Reason: ${exception.localizedMessage}"
        exception.printStackTrace()
        crashlytics.recordException(Exception(msg, exception.stackTrace))
        when (appAction) {
            AppAction.AppActionExit -> {
                val details = GlobalHelper.getLog(exception.stackTrace)
                context.startActivity(
                    Intent(context, LoggerActivity::class.java).apply {
                        putExtra(Constants.EXTRA_DETAILS, details)
                    }
                )
                System.exit(0)
            }

            AppAction.AppActionRestart -> {
                GlobalHelper.restartApp(context)
            }

            AppAction.AppActionException -> {
                throwException(msg, exception.stackTrace)
            }

            null -> {
                // No action specified
            }
        }
    }
    /**
     * Throws an FaultSpyException with the provided message and stack trace.
     * @param msg The exception message.
     * @param stackTrace The stack trace.
     */
    private fun throwException(msg: String, stackTrace: Array<StackTraceElement>) {
        throw Exception(msg, stackTrace)
    }
}