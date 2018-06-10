package com.charlesmuchene.installer.services

import android.app.IntentService
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import com.charlesmuchene.installer.utils.INSTALLATION_STATUS

/**
 * Status checker service
 */
class StatusCheckerService : IntentService("StatusCheckerService") {

    private val requiredPackageName = "com.safeboda.driver"

    override fun onHandleIntent(intent: Intent?) {
        val installed = isSBDriverInstalled()
        val statusIntent = Intent(INSTALLATION_STATUS).apply {
            putExtra(INSTALLATION_STATUS, installed)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(statusIntent)
    }

    /**
     * Check if driver app is installed
     *
     * @return Boolean
     */
    private fun isSBDriverInstalled(): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val exists = packageManager.queryIntentActivities(intent, 0).firstOrNull { info ->
            info.resolvePackageName == requiredPackageName
        }
        return exists != null
    }
}
