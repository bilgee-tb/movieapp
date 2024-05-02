package com.example.movieapp.ui.activity.authentication.loginActivity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movieapp.databinding.ActivityLoginBinding
import com.example.movieapp.ui.activity.MainActivity
import com.example.movieapp.ui.activity.authentication.registerActivity.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    @Inject
    // Creating firebaseAuth object
    lateinit var auth: FirebaseAuth

    // SharedPreferences for storing login status
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase auth object
        auth = FirebaseAuth.getInstance()

        // Get SharedPreferences instance
        sharedPreferences = getSharedPreferences("user_login", MODE_PRIVATE)

        // Check if user is already logged in
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            navigateToMainActivity()
            return  // Exit if already logged in
        }

        binding.btnLogin.setOnClickListener {
            login()
        }

    }


    private fun login() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Input Validation - Check for empty or null fields
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            // Improved Error Handling with Specific Messages
            val errorMessage = when {
                TextUtils.isEmpty(email) && TextUtils.isEmpty(password) -> {
                    "Please enter your email and password."
                }

                TextUtils.isEmpty(email) -> "Please enter your email address."
                TextUtils.isEmpty(password) -> "Please enter your password."
                else -> "An unknown error occurred." // Fallback for unexpected issues
            }

            // Display the error message
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        } else {
            // Sign in with Firebase authentication
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show()
                    navigateToMainActivity()
                }  else {
                    val errorMessage = task.exception?.message
                    if (errorMessage != null) {
                        toast("Failed to authenticate: $errorMessage")
                    } else {
                        toast("Failed to authenticate for unknown reasons.")
                    }
                }
            }
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun onRegisterNowClick(view: View) {
        // Start RegisterActivity
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}