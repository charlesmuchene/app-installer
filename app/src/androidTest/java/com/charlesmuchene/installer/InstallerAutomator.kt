package com.charlesmuchene.installer

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.test.InstrumentationRegistry
import android.support.test.filters.LargeTest
import android.support.test.filters.SdkSuppress
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.*
import android.util.Log
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.JELLY_BEAN_MR2)
class InstallerAutomator {

    private val settingsPackage = "com.android.settings"
    private val accountsText = "Cloud and accounts"
    private lateinit var device: UiDevice
    private lateinit var context: Context
    private val launchTimeout = 5000L

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        context = InstrumentationRegistry.getContext()
    }

    @Test
    @Throws(UiObjectNotFoundException::class)
    fun openSettings() {
        device.pressHome()

        val intent = context.packageManager.getLaunchIntentForPackage(settingsPackage).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(settingsPackage).depth(0)), launchTimeout)

        UiScrollable(UiSelector().scrollable(true)).run {
            scrollForward()
            scrollTextIntoView(accountsText)
        }

        device.findObject(UiSelector().text(accountsText)).click()
        device.findObject(UiSelector().text("Accounts")).click()
        device.findObject(UiSelector().text("Add account")).click()

        UiScrollable(UiSelector().scrollable(true)).run {
            scrollForward()
            scrollTextIntoView("Google")
        }

        device.findObject(UiSelector().text("Google")).click()

    }
}
