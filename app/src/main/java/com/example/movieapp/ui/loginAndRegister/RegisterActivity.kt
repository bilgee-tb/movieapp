package com.example.movieapp.ui.loginAndRegister

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movieapp.databinding.ActivityRegisterBinding
import com.example.movieapp.ui.activity.MainActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth // Injecting FirebaseAuth instance
    private lateinit var binding: ActivityRegisterBinding
    lateinit var userEmail: String
    lateinit var userPassword: String
    lateinit var createAccountInputsArray: Array<EditText>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        createAccountInputsArray =
            arrayOf(binding.etEmail, binding.etPassword, binding.etConfirmPassword)

        binding.btnRegister.setOnClickListener {
            signIn()
        }

    }

    /* check if there's a signed-in user*/

    override fun onStart() {
        super.onStart()
        val user: FirebaseUser? = firebaseAuth.currentUser
        user?.let {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun notEmpty(): Boolean = binding.etEmail.text.toString().trim().isNotEmpty() &&
            binding.etPassword.text.toString().trim().isNotEmpty() &&
            binding.etConfirmPassword.text.toString().trim().isNotEmpty()

    private fun identicalPassword(): Boolean {
        var identical = false
        if (notEmpty() &&
            binding.etPassword.text.toString().trim() == binding.etConfirmPassword.text.toString()
                .trim()
        ) {
            identical = true
        } else if (!notEmpty()) {
            createAccountInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        } else {
            toast("passwords are not matching !")
        }
        return identical
    }

    private fun signIn() {
        if (identicalPassword()) {
            // identicalPassword() returns true only  when inputs are not empty and passwords are identical
            userEmail = binding.etEmail.text.toString().trim()
            userPassword = binding.etPassword.text.toString().trim()

            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast("created account successfully !")
                        sendEmailVerification()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
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

    /* send verification email to the new user. This will only
    *  work if the firebase user is not null.
    */
//
//    private fun sendEmailVerification() {
//        firebaseUser?.let {
//            it.sendEmailVerification().addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    toast("email sent to $userEmail")
//                }
//            }
//        }
//    }
    private fun sendEmailVerification() {
        firebaseAuth.currentUser?.let { user ->
            user.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toast("Email sent to ${user.email}")
                } else {
                    toast("Failed to send verification email")
                }
            }
        }
    }

    fun onLoginClick(view: View) {
        // Start RegisterActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}




