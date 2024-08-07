package pk.jamalzahid.faultspyexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pk.jamalzahid.faultspy.AppAction
import com.pk.jamalzahid.faultspy.FaultSpyAgent


/**
 * @copyright
 * Copyright (c) 2023  M. JAMAL
 * All rights reserved.
 * Created by M. JAMAL on 04/08/2024
 * @author M. JAMAL
 */

open class BaseActivity : AppCompatActivity() {
    // Initialize FirebaseCrashlytics
    val crashlytics by lazy { FirebaseCrashlytics.getInstance() }
    private var agent: FaultSpyAgent? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create FaultSpyAgent
        agent = FaultSpyAgent.Builder(this)
            .setTicker(100) // Set the interval at which the main thread is checked (in milliseconds)
            .setTimeOut(5000L) // Set timeout threshold (in milliseconds)
            .setFirebaseCrashlytics(crashlytics) // Set FirebaseCrashlytics instance
            .setAppAction(AppAction.AppActionRestart) // Set the action to perform when an ANR is detected
            .build()
    }

    override fun onStop() {
        super.onStop()
        // Stop monitoring when the activity is stopped
        agent?.stopMonitoring()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop monitoring when the activity is destroyed
        agent?.stopMonitoring()
    }

    override fun onResume() {
        super.onResume()
        // Resume monitoring when the activity is resumed
        if (agent!!.job == null) {
            agent = FaultSpyAgent.Builder(this)
                .setTicker(200)
                .setTimeOut(5000L)
                .setFirebaseCrashlytics(crashlytics)
                .setAppAction(AppAction.AppActionRestart)
                .build()
        }
    }
}