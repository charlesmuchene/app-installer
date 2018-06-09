package com.charlesmuchene.installer

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.test.InstrumentationRegistry
import android.support.test.filters.LargeTest
import android.support.test.filters.SdkSuppress
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.*
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

    // TODO Expose these configurable user settings
    private val networkSSID = "322412373536"
    private val networkPassword = "######"

    private val accountEmail = "safeboda13"
    private val accountPassword = "safetester"


    private val settingsPackage = "com.android.settings"
    private val accountsText = "Accounts"
    private val addAccount = "Add account"
    private val googleText = "Google"

    private lateinit var device: UiDevice
    private lateinit var context: Context
    private val timeout = 5000L

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        context = InstrumentationRegistry.getContext()
    }

    /**
     * Open settings
     */
    private fun openSettings() {
        device.pressHome()
        val intent = context.packageManager.getLaunchIntentForPackage(settingsPackage).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(settingsPackage).depth(0)), timeout)
        device.waitForIdle()
    }

    @Test
    @Throws(UiObjectNotFoundException::class)
    fun connectToWifi() {
        device.pressHome()
        device.openQuickSettings()
        val wifiButtonDescription = "Wi-Fi On,,Open Wi-Fi settings."
        val wifiSelector = UiSelector().className(Button::class.java).description(wifiButtonDescription)
        device.findObject(wifiSelector)?.longClick()
        device.waitForIdle(timeout)
        device.wait(Until.findObject(By.clazz(TextView::class.java).text(networkSSID)), timeout)
        device.findObject(UiSelector().text(networkSSID)).click()
        val passwordInput = device.findObject(UiSelector().resourceId("com.android.settings:id/password"))
        passwordInput.text = networkPassword
        device.findObject(By.clazz(Button::class.java).text("CONNECT")).click()

        // TODO Wait for connectivity

    }

    @Test
    @Throws(UiObjectNotFoundException::class)
    fun addGoogleAccount() {
        openSettings()
        UiScrollable(UiSelector().scrollable(true)).run {
            flingForward()
            scrollTextIntoView(accountsText)
        }
        device.findObject(UiSelector().text(accountsText)).click()
        device.findObject(UiSelector().text(addAccount)).click()
        device.findObject(UiSelector().text(googleText)).click()
        device.waitForIdle()
        device.wait(Until.findObject(By.clazz(WebView::class.java)), timeout)
        device.findObject(UiSelector().instance(0)
                .className(EditText::class.java).text("Email or phone")).apply {
            waitForExists(timeout)
            text = accountEmail
        }
        device.findObject(By.descContains("NEXT").res("identifierNext")).click()
        device.waitForIdle()

        device.findObject(UiSelector().instance(0)
                .className(EditText::class.java).resourceId("password")).apply {
            waitForExists(timeout)
            text = accountPassword
        }
        device.findObject(By.descContains("NEXT").res("passwordNext")).click()
        device.waitForIdle()
        device.findObject(UiSelector().className(View::class.java).resourceId("next")).apply {
            waitForExists(timeout)
            click()
        }
        device.waitForIdle()
        

        // TODO Add a scroll to enable the next button

    }

}
