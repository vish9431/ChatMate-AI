package com.example.chatmateai

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Signup : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        auth = FirebaseAuth.getInstance()

        val btnSignup = findViewById<Button>(R.id.btnsign)
        progressBar = findViewById(R.id.progressbar)

        btnSignup.setOnClickListener {

            registerUser()
        }
    }
    private fun registerUser() {
        val email = findViewById<EditText>(R.id.editTextEmail).text.toString().trim()
        val password = findViewById<EditText>(R.id.editTextPassword).text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Show the progress bar while signing up
        progressBar.visibility = ProgressBar.VISIBLE


        // Firebase Authentication to create a user with email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, updating UI
                    val user: FirebaseUser? = auth.currentUser
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    // Navigate to another activity or perform other actions here
                    // Navigate to MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, displayng a message to the user.
                    Toast.makeText(this, "Registration failed. ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

}