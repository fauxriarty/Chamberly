package com.company.chamberly

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationManagerCompat
import com.company.chamberly.ui.theme.ChamberlyTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkNotificationPermission()
        val CreateButton = findViewById<Button>(R.id.create_button)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val areNotificationsEnabled = notificationManager.areNotificationsEnabled()
        if (!areNotificationsEnabled) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package", packageName, null)
            startActivity(intent)
        }


        CreateButton.setOnClickListener {
            val intent = intent
            intent.setClass(this, CreateActivity::class.java)
            startActivity(intent)
        }
        val SearchButton = findViewById<Button>(R.id.search_button)
        SearchButton.setOnClickListener {
            val intent = intent
            intent.setClass(this, SearchActivity::class.java)
            startActivity(intent)
        }

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                auth.signOut()
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
    override fun onDestroy() {
        onBackPressedCallback.remove()
        super.onDestroy()
    }
    private fun isNotificationPermissionGranted(): Boolean {
        return NotificationManagerCompat.from(this).areNotificationsEnabled()
    }
    private fun checkNotificationPermission() {
        if (!isNotificationPermissionGranted()) {
            // Notification permission is not granted, show a button to request it
            requestNotificationPermission()
        }
    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            startActivity(intent)
        } else {
            // For devices prior to Android 8, show a toast to explain how to enable notifications
            Toast.makeText(this, "Please enable notifications for this app in system settings", Toast.LENGTH_LONG).show()
        }
    }
}