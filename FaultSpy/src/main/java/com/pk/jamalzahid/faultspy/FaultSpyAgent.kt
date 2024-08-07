package com.pk.jamalzahid.faultspy

/**
 * FaultSpyAgent is responsible for monitoring the main thread for Application Not Responding (ANR) or crash incidents.
 * It periodically checks the main thread's stack trace and performs actions based on the specified appAction.
 *
 * @constructor Creates an instance of FaultSpyAgent with the specified builder.
 * @property mContext The context in which the agent operates.
 * @property TIME_OUT The timeout threshold for detecting ANR (in milliseconds).
 * @property mTicker The interval at which the main thread is checked (in milliseconds).
 * @property mPreviousMethod The name of the previous method detected in the stack trace.
 * @property mReported The name of the method reported for ANR.
 * @property mDuration The duration for which the method has been blocking the main thread.
 * @property appAction The action to perform when an ANR is detected.
 * @property mFirebaseCrashlytics An instance of FirebaseCrashlytics for logging exceptions.
 * @property listMethodsExcluded A list of method names to be excluded from ANR detection.
 */
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FaultSpyAgent private constructor(builder: Builder) {

    private var mContext: Context = builder.mContext
    private var TIME_OUT = builder.getTimeOut()
    private var mTicker: Long = builder.getTicker()
    private var mPreviousMethod = ""
    private var mReported = ""
    private var mDuration = 0L
    private var appAction: AppAction? = builder.getAppAction()
    private var mFirebaseCrashlytics: FirebaseCrashlytics? = builder.getFirebaseInstance()
    private val listMethodsExcluded = mutableListOf("nativePollOnce","")
    var job: Job? = null

    init {
        startMonitoring()
        mFirebaseCrashlytics?.let{
            Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(context = mContext,it,appAction))

        }
    }
    /**
     * Start monitoring for ANR incidents.
     * job is started for coroutine.
     * A light weighted coroutine is used to avoid blocking the main thread.
     */
    private fun startMonitoring() {
        job = CoroutineScope(Dispatchers.Default).launch {
            // Log.d(TAG, "Coroutine started:  ${Thread.currentThread().name}")
            while (isActive) {
                val stacktrace = Looper.getMainLooper().thread.stackTrace
                val method = stacktrace.firstOrNull()?.methodName ?: ""
                // Log.d(TAG, "Checking main thread: ${Thread.currentThread().name}")
                if (method != mPreviousMethod) {
                    mPreviousMethod = method
                    mDuration = 0
                } else {
                    mDuration += mTicker
                }
                delay(mTicker)
                if (mDuration > TIME_OUT && method !in listMethodsExcluded) {
                    if (mReported != mPreviousMethod) {
                        mReported = mPreviousMethod
                        processANR(stacktrace)
                    }
                }
            }
        }
    }

    /**
     * Stop monitoring for ANR incidents.
     * job is cancelled and set to null.
     * to avoid memory leaks and running when app is closed but still in resents tab
     */
    fun stopMonitoring() {
        job?.cancel()
        job = null
    }

    /**
     * Throws an FaultSpyException with the provided message and stack trace.
     * @param msg The exception message.
     * @param stackTrace The stack trace.
     */
    private fun throwException(msg: String, stackTrace: Array<StackTraceElement>) {
        throw Exception(msg, stackTrace)
    }

    /**
     * Processes the detected ANR by logging the stack trace and performing the specified action.
     * @param stackTrace The stack trace of the ANR.
     */
    private fun processANR(stackTrace: Array<StackTraceElement>) {
        val msg =
            "Method: ${stackTrace.firstOrNull()?.methodName} is blocking main thread for at least $mDuration ms"
        mFirebaseCrashlytics?.recordException(Exception(msg, stackTrace))
        logStackTrace(TAG, stackTrace)
        when (appAction) {
            AppAction.AppActionExit -> {
                val details = GlobalHelper.getLog(stackTrace)
                mContext.startActivity(
                    Intent(mContext, LoggerActivity::class.java).apply {
                        putExtra(Constants.EXTRA_DETAILS, details)
                    }
                )
                System.exit(0)
            }

            AppAction.AppActionRestart -> {
                GlobalHelper.restartApp(mContext)
            }

            AppAction.AppActionException -> {
                throwException(msg, stackTrace)
            }

            null -> {
                // No action specified
            }
        }
    }

    /**
     * Logs the stack trace to the specified tag.
     * @param tag The tag for logging.
     * @param stackTrace The stack trace to log.
     */
    fun logStackTrace(tag: String, stackTrace: Array<StackTraceElement>) {
        var log = ""
        stackTrace.forEach { element ->
            log += element.toString() + "\n"
        }
        Log.i(tag, log)
    }

    /**
     * Builder class for constructing an instance of FaultSpyAgent.
     *
     * @param mContext The context in which the agent will operate.
     */
    class Builder(val mContext: Context) {
        private var TIME_OUT = 5000L
        private var mTicker = 200L
        private var mFirebaseCrashlytics: FirebaseCrashlytics? = null
        private var appAction: AppAction? = null

        /**
         * Sets the timeout threshold for detecting ANR.
         * @param timeout The timeout threshold (in milliseconds).
         * @return The Builder instance.
         */
        fun setTimeOut(timeout: Long) = apply { TIME_OUT = timeout }

        /**
         * Gets the timeout threshold for detecting ANR.
         * @return The timeout threshold (in milliseconds).
         */
        fun getTimeOut() = TIME_OUT

        /**
         * Sets the interval at which the main thread is checked.
         * @param ticker The interval (in milliseconds).
         * @return The Builder instance.
         */
        fun setTicker(ticker: Long) = apply { mTicker = ticker }

        /**
         * Gets the interval at which the main thread is checked.
         * @return The interval (in milliseconds).
         */
        fun getTicker() = mTicker

        /**
         * Sets the instance of FirebaseCrashlytics for logging exceptions.
         * @param instance The FirebaseCrashlytics instance.
         * @return The Builder instance.
         */
        fun setFirebaseCrashlytics(instance: FirebaseCrashlytics) =
            apply { this.mFirebaseCrashlytics = instance }

        /**
         * Gets the instance of FirebaseCrashlytics for logging exceptions.
         * @return The FirebaseCrashlytics instance.
         */
        fun getFirebaseInstance() = mFirebaseCrashlytics

        /**
         * Sets the action to perform when an ANR is detected.
         * @param appAction The action to perform.
         * @return The Builder instance.
         */
        fun setAppAction(appAction: AppAction) = apply { this.appAction = appAction }

        /**
         * Gets the action to perform when an ANR is detected.
         * @return The action to perform.
         */
        fun getAppAction() = appAction

        /**
         * Builds and returns an instance of FaultSpyAgent.
         * @return The constructed FaultSpyAgent instance.
         */
        fun build() = FaultSpyAgent(this)
    }

    companion object {
        private const val TAG = "FaultSpyAgent"
    }
}
