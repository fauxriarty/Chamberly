package com.company.chamberly

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback

import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkNotificationPermission()

        val CreateButton = findViewById<Button>(R.id.createChamberButton)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val areNotificationsEnabled = notificationManager.areNotificationsEnabled()
        if (!areNotificationsEnabled) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package", packageName, null)
            startActivity(intent)
        }

        val sharedPreferences = getSharedPreferences("userDetails", Context.MODE_PRIVATE)
        val displayName = sharedPreferences.getString("displayName", null) ?: "Anonymous"
        val usernameTextView = findViewById<TextView>(R.id.usernameTextView)
        usernameTextView.text = displayName

        val followUsButton = findViewById<Button>(R.id.followUs)
        followUsButton.setOnClickListener {
            openInstagramPage("https://www.instagram.com/chamberly_app/")
        }


        CreateButton.setOnClickListener {
            val intent = intent
            intent.setClass(this, CreateActivity::class.java)
            startActivity(intent)
        }
        val SearchButton = findViewById<Button>(R.id.findChamberButton)
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
    private fun openInstagramPage(url: String) {
        try {
            // Try to open the Instagram page in the Instagram app
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$url"))
            intent.setPackage("com.instagram.android")
            startActivity(intent)
        } catch (e: Exception) {
            // If the Instagram app is not installed, open the page in a web browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$url"))
            startActivity(intent)
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