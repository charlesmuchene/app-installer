package com.charlesmuchene.installer.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import com.charlesmuchene.installer.R
import com.charlesmuchene.installer.services.StatusCheckerService
import com.charlesmuchene.installer.utils.INSTALLATION_STATUS
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private val statusReceiver = StatusReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onResume() {
        super.onResume()
        updateStatus(R.string.checking_status)
        LocalBroadcastManager.getInstance(this).registerReceiver(statusReceiver,
                IntentFilter(INSTALLATION_STATUS))
        startService(Intent(this, StatusCheckerService::class.java))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(statusReceiver)
    }

    /**
     * Update status
     *
     * @param status Status to update to
     */
    private fun updateStatus(@StringRes status: Int) {
        val colorResource = when (status) {
            R.string.not_installed -> R.color.colorNotInstalled
            R.string.app_installed -> R.color.colorInstalled
            else -> R.color.colorChecking
        }
        statusTextView.setText(status)
        statusTextView.setTextColor(ContextCompat.getColor(this, colorResource))
    }

    /**
     * Status receiver class
     */
    private inner class StatusReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val status = intent?.getBooleanExtra(INSTALLATION_STATUS, false) ?: false
            val resource = if (status) R.string.app_installed else R.string.not_installed
            updateStatus(resource)
        }
    }

}
