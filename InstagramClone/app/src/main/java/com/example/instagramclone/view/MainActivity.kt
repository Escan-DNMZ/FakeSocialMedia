package com.example.instagramclone.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.instagramclone.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        val curentUser = auth.currentUser
        if (curentUser != null){
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    fun signInClick(view: View){
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                val intent = Intent(this, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener{
                Toast.makeText(this,"Invalid Email or Password", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this,"Enter Email and Password", Toast.LENGTH_LONG).show()
        }

    }

    fun signUpClick(view: View){
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()){
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                val intent = Intent(this, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener{
                Toast.makeText(this,"Enter Email and Password", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this,"Enter Email and Password", Toast.LENGTH_LONG).show()
        }

    }
}