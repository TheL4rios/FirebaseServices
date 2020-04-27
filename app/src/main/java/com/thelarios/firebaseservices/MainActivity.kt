package com.thelarios.firebaseservices

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import mx.edu.ittepic.ladm_u3_practica1_arturolarios.Utils.Utils

class MainActivity : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentUser = mAuth.currentUser
        updateUI(currentUser, false)

        btnSignIn.setOnClickListener {
            val email = txtEmail.text.toString().trim()
            val password = txtPassword.text.toString().trim()

            signInCurrentUser(email, password)
        }

        btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity :: class.java))
        }

    }

    private fun signInCurrentUser(email: String, password: String)
    {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                updateUI(mAuth.currentUser, false)
            }
            .addOnFailureListener {
                updateUI(null, true)
            }
    }

    private fun updateUI(currentUser: FirebaseUser?, fail : Boolean) {
        currentUser?.let {
           startActivity(Intent(this, ActionActivity::class.java))
            finish()
        }

        if(fail)
            Utils.toastMessageLong("Ocurrió un problema, vuelva más tarde", this)
    }
}
