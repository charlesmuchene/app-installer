package com.charlesmuchene.installer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.test.InstrumentationRegistry
import android.support.test.filters.LargeTest
import android.support.test.filters.SdkSuppress
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.*
import android.view.View
import android.webkit.WebView
import android.widget.*
import com.charlesmuchene.installer.utils.ACCOUNT_EMAIL
import com.charlesmuchene.installer.utils.ACCOUNT_PASSWORD
import com.charlesmuchene.installer.utils.NETWORK_PASSWORD
import com.charlesmuchene.installer.utils.NETWORK_SSID
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Automates SB Driver App requirements
 *
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.KITKAT)
class InstallerAutomator {

    private lateinit var networkSSID: String
    private lateinit var accountEmail: String
    private lateinit var accountPassword: String
    private lateinit var networkPassword: String

    private val settingsPackage = "com.android.settings"
    private val sbDriverPackage = "com.safeboda.driver"
    private lateinit var device: UiDevice
    private lateinit var context: Context
    private val timeout = 5000L

    init {
        parseArguments()
    }

    @Before
    fun setup() {
        context = InstrumentationRegistry.getContext()
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()
    }

    /**
     * Parse test arguments
     */
    private fun parseArguments() {
        with(InstrumentationRegistry.getArguments()) {
            networkSSID = getString(NETWORK_SSID) ?: ""
            accountEmail = getString(ACCOUNT_EMAIL) ?: ""
            networkPassword = getString(NETWORK_PASSWORD) ?: ""
            accountPassword = getString(ACCOUNT_PASSWORD) ?: ""
        }
    }

    @Test
    @Throws(UiObjectNotFoundException::class)
    fun resetDeviceBridge() {
        device.pressHome()
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            data = Uri.fromParts("package", settingsPackage, null)
        }
        context.startActivity(intent)
        device.waitForIdle()
        device.findObject(UiSelector().text("Storage")).click()
        device.findObject(UiSelector().text("CLEAR DATA")
                .className(Button::class.java)).clickAndWaitForNewWindow()
        device.findObject(UiSelector().text("OK").className(Button::class.java)).click()
        device.pressHome()
        device.openNotification()
        device.findObject(UiSelector().className(TextView::class.java)
                .text("USB debugging connected")).clickAndWaitForNewWindow()
        device.findObject(UiSelector().className(Switch::class.java).instance(0)
                .checked(true)).click()
    }

    @Test
    @Throws(UiObjectNotFoundException::class, IllegalArgumentException::class)
    fun connectToWifi() {
        if (networkSSID.isBlank())
            throw IllegalArgumentException("Provide a network SSID or make sure your WIFI is discoverable")
        val intent = Intent(android.provider.Settings.ACTION_WIFI_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        device.waitForIdle()
        device.wait(Until.findObject(By.clazz(TextView::class.java).text(networkSSID)), timeout)
        device.findObject(UiSelector().text(networkSSID)).click()
        device.findObject(UiSelector()
                .resourceId("com.android.settings:id/password")).apply {
            text = networkPassword
        }
        device.findObject(By.clazz(Button::class.java).text("CONNECT")).click()
        device.waitForIdle(timeout)
    }

    @Test
    @Throws(UiObjectNotFoundException::class)
    fun addGoogleAccount() {
        val intent = Intent(android.provider.Settings.ACTION_ADD_ACCOUNT).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        }
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(settingsPackage).depth(0)), timeout)
        device.waitForIdle()
        device.wait(Until.findObject(By.clazz(WebView::class.java)), timeout)
        device.findObject(UiSelector().instance(0)
                .className(EditText::class.java).text("Email or phone")).apply {
            waitForExists(timeout)
            text = accountEmail
        }
        device.findObject(UiSelector().text("NEXT").resourceId("identifierNext")).click()
        device.waitForIdle()

        device.findObject(UiSelector().instance(0)
                .className(EditText::class.java).resourceId("password")).apply {
            waitForExists(timeout)
            text = accountPassword
        }
        device.findObject(UiSelector().text("NEXT").resourceId("passwordNext")).click()
        device.waitForIdle()
        device.findObject(UiSelector().className(View::class.java).resourceId("next")).apply {
            waitForExists(timeout)
            click()
        }
        device.waitForIdle()

        UiScrollable(UiSelector().scrollable(true)).run {
            flingForward()
            scrollForward()
        }
        device.waitForIdle()
        device.findObject(UiSelector().text("NEXT").className(Button::class.java))?.apply {
            waitForExists(timeout)
            click()
        }
        device.waitForIdle()
        device.pressHome()
    }

    @Test
    @Throws(UiObjectNotFoundException::class)
    fun launchApp() {
        val intent = context.packageManager.getLaunchIntentForPackage(sbDriverPackage)
        context.startActivity(intent)
        device.waitForIdle()
    }

    @Test
    @Throws(UiObjectNotFoundException::class)
    fun optimizeBattery() {
        val intent = Intent(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        device.findObject(UiSelector().scrollable(true).className(Spinner::class.java)).click()
        device.findObject(UiSelector().className(CheckedTextView::class.java)
                .text("All apps")).click()

        // TODO New app should request user properly to allow whitelisting
    }

}
