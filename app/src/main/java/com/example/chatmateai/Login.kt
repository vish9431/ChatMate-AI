package com.example.chatmateai

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        progressBar = findViewById(R.id.progressbar)

        btnLogin.setOnClickListener {
            loginUser()
        }

        val sign = findViewById<Button>(R.id.btnSignup)
        sign.setOnClickListener {
            // Navigate to SignUp Screen
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {
        val email = findViewById<EditText>(R.id.editTextEmail).text.toString().trim()
        val password = findViewById<EditText>(R.id.editTextPassword).text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Show the progress bar while loging
        progressBar.visibility = ProgressBar.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, updating UI
                    val user: FirebaseUser? = auth.currentUser
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    // Navigate to MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    // Navigate to another activity or perform other actions here

                } else {
                    // If sign-in fails, displaying a message to the user.
                    Toast.makeText(this, "Authentication failed.${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }

    }
}