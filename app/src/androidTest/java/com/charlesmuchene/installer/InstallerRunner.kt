package com.charlesmuchene.installer

import android.os.Bundle
import android.support.test.runner.AndroidJUnitRunner

/**
 * Installer custom JUnit Runner
 */
class InstallerRunner : AndroidJUnitRunner() {

    override fun onCreate(arguments: Bundle?) {
        val name = InstallerRunListener::class.java.canonicalName
        arguments?.run {
            putBoolean("debug", false)
            putString("listener", name)
        }
        super.onCreate(arguments)
    }

}