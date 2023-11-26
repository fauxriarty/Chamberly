package com.company.chamberly
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ActiveChambersActivity : AppCompatActivity() {

    // Using View Binding to reference the views
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activechambers)
    }
}