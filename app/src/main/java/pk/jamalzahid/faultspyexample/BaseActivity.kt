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

class BaseActivity : AppCompatActivity() {
    val crashlytics by lazy { FirebaseCrashlytics.getInstance() }
    private var agent: FaultSpyAgent? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        agent = FaultSpyAgent.Builder(this)
            .setTicker(100)
            .setTimeOut(5000L)
            .setFirebaseCrashlytics(crashlytics)
            .setAppAction(AppAction.AppActionRestart)
            .build()
    }

    override fun onStop() {
        super.onStop()
        agent?.stopMonitoring()
    }

    override fun onDestroy() {
        super.onDestroy()
        agent?.stopMonitoring()
    }

    override fun onResume() {
        super.onResume()
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