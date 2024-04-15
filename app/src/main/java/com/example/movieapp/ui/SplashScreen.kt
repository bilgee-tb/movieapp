package com.example.movieapp.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movieapp.ui.loginAndRegister.LoginActivity
import com.example.movieapp.R
import com.example.movieapp.ui.activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreen : AppCompatActivity() {
    @Inject
    lateinit var  firebaseAuth: FirebaseAuth
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onStart() {
        super.onStart()
        // Delay for 2 seconds before navigating to the appropriate activity
        Handler(Looper.getMainLooper()).postDelayed({
            firebaseAuth.currentUser?.let {
                startActivity(Intent(this, MainActivity::class.java))
            } ?: run {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish() // Finish the current activity to prevent user from returning to it
        }, 2000) // Delay in milliseconds (2000 milliseconds = 2 seconds)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spalsh_screen)

        sharedPreferences = getSharedPreferences("user_login", MODE_PRIVATE)


    }

}




