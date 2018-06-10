package com.charlesmuchene.installer

import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunListener

/**
 * Installer run listener
 */
class InstallerRunListener : RunListener() {

    override fun testFailure(failure: Failure?) {
        super.testFailure(failure)
        failure?.let { fail ->
            // Hack: Inevitable failure since we're disconnect adb
            if (fail.description.methodName == "resetDeviceBridge") {
                println("ResetDeviceBridge run successfully")
            }
        }
    }
}