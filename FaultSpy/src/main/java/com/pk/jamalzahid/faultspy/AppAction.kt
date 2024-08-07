package com.pk.jamalzahid.faultspy

/**
 * Represents different actions that can be performed when an ANR or crash is detected.
 */
sealed class AppAction {
    /**
     * Exit the application when an ANR or crash is detected.
     */
    object AppActionExit : AppAction()

    /**
     * Restart the application when an ANR or crash is detected.
     */
    object AppActionRestart : AppAction()

    /**
     * Throw an exception when an ANR or crash is detected.
     */
    object AppActionException : AppAction()
}
