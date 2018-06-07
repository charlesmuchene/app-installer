package com.charlesmuchene.installer

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.test.InstrumentationRegistry
import android.support.test.filters.LargeTest
import android.support.test.filters.SdkSuppress
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.*
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
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

    private val openDrawerText = "Open navigation drawer"
    private val settingsPackage = "com.android.settings"
    private val gmailPackage = "com.google.android.gm"
    private val accountsText = "Accounts"
    private val addAccount = "Add account"
    private val settingsText = "Settings"
    private val googleText = "Google"

    private lateinit var device: UiDevice
    private lateinit var context: Context
    private val timeout = 5000L

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
        device.wait(Until.hasObject(By.pkg(settingsPackage).depth(0)), timeout)

        UiScrollable(UiSelector().scrollable(true)).run {
            scrollForward()
            scrollTextIntoView(accountsText)
        }

        device.findObject(UiSelector().text(accountsText)).click()
//        device.findObject(UiSelector().text("Accounts")).click()
        device.findObject(UiSelector().text(addAccount)).click()
//
//        UiScrollable(UiSelector().scrollable(true)).run {
//            scrollForward()
//            scrollTextIntoView(googleText)
//        }

        device.findObject(UiSelector().text(googleText)).click()

        device.wait(Until.findObject(By.clazz(WebView::class.java)), timeout)
        device.findObject(UiSelector().instance(0)
                .className(EditText::class.java)).apply {
            waitForExists(timeout)
            text = "we@sb.them"
        }

        device.findObject(UiSelector().instance(0).className(Button::class.java)).apply {
            waitForExists(timeout)
            click()
        }

        device.findObject(UiSelector().instance(0)
                .className(EditText::class.java)).apply {
            waitForExists(timeout)
            text = "password"
        }

    }

    @Test
    @Throws(UiObjectNotFoundException::class)
    fun addAccountToGmail() {
        device.pressHome()
        val intent = context.packageManager.getLaunchIntentForPackage(gmailPackage).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(gmailPackage).depth(0)), timeout)
        device.findObject(By.desc(openDrawerText)).click()
        UiScrollable(UiSelector().scrollable(true)).run {
            scrollForward()
            scrollTextIntoView(settingsText)
        }
        device.findObject(UiSelector().text(settingsText)).clickAndWaitForNewWindow()
        device.findObject(UiSelector().text(addAccount)).clickAndWaitForNewWindow()
        device.findObject(UiSelector().text(googleText)).clickAndWaitForNewWindow()

    }

}
