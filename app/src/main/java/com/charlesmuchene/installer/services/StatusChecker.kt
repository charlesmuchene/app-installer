package com.charlesmuchene.installer.services

import android.app.IntentService
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.content.LocalBroadcastManager
import com.charlesmuchene.installer.utils.INSTALLATION_STATUS

/**
 * Status checker service
 */
class StatusCheckerService : IntentService("StatusCheckerService") {

    private val requiredPackageName = "<package-name>"

    override fun onHandleIntent(intent: Intent?) {
        val installed = isAppInstalled()
        val statusIntent = Intent(INSTALLATION_STATUS).apply {
            putExtra(INSTALLATION_STATUS, installed)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(statusIntent)
    }

    /**
     * Check if app is installed
     *
     * @return Boolean
     */
    private fun isAppInstalled(): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val exists = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY).firstOrNull { info ->
            info.activityInfo.processName == requiredPackageName
        }
        return exists != null
    }
}
