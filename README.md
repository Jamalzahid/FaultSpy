### Android Fault Spy
Android Fault Spy is the most simplest and light weight library that helps android developers to detect ANRs and crashes. The FaultSpy library helps in detecting and reporting Application Not Responding (ANR) and crash issues in Android applications. It provides integration with Firebase Crashlytics for reporting and allows setting custom actions when an ANR or Crash is detected.

### Features
1. Detect ANR
2. Detect a function which is approaching ANR limit and send report to firebase prior to ANR occurrence.
3. Detect Crash
4. Prevent from down ranking your app on google play console by exiting app prior to anr or crash occurrence.

### What is Android ANR (Application Not Responding)
when a developer do most heavy jobs on UI thread (more than 5 seconds usually) and UI thread still receive more request/events for doing a task then Android system raises ANR message. This is extremely bad effect on your app and may lead to the failure of your business.
### Android ANR does matter
Google recommends/suggests your app on play store. If your app raises too many ANRs then your app will be ranked down

### Android ANR Durations
1. Normal on UI Thread in any activity = 5 secs
2. BroadCast = 10 sec
3. Service = 20 sec

### What is Android Crash or Exception
An Android crash or exception occurs when an application encounters an unexpected error that disrupts its normal operation, leading to the app forcefully closing. This can result from unhandled exceptions, resource mismanagement, or code errors.
### Android Crash or Exception does matter
Google recommends/suggests your app on play store. If your app raises too many crashes or exceptions then your app will be ranked down



**Note: Example app is included in the project. Just clone the repo**

## Android Fault Spy Library
### Implement:

```
implementation("io.github.jamalzahid:faultspy:1.0.0")
```
## Usage
```
// It's recommended to use BaseActivity for best perfromance 
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
```

### Tips
#### Firebase Integration:
By setting up FirebaseCrashlytics, you will receive reports in the form of fatal exceptions (not ANR/crash). This means if any function is approaching the ANR limit or Error occurs, a report will be sent to Firebase. This can help in identifying potential ANR or Error issues before they cause a crash.

#### Preventing ANR Reports to Google Play Console:
By setting an app action such as AppAction.AppActionExit, the library will prevent ANR and Crashes from being reported to the Google Play Console by exiting the application before the ANR or Crash occurs.

## Change Log
**version 1.0.0**
Initial release

## Support Me
If you like this library then star it. 

