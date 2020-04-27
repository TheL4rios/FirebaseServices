package com.thelarios.firebaseservices

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import mx.edu.ittepic.ladm_u3_practica1_arturolarios.Utils.Utils

class SignUpActivity : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btnSignUp.setOnClickListener {
            val email = txtEmail.text.toString()
            val pass = txtPassword.text.toString()
            val rPass = txtRepeatPassword.text.toString()

            signUp(email, pass, rPass)
        }
    }

    private fun signUp(email: String, pass: String, rPass: String) {
        if (pass == rPass)
        {
            mAuth.createUserWithEmailAndPassword(email.trim(), pass.trim())
                .addOnSuccessListener {
                    startActivity(Intent(this, ActionActivity :: class.java))
                    finish()
                }
                .addOnFailureListener {
                    Utils.toastMessageLong("Algo salió mal, Revise su conexión a internet", this)
                }
            return
        }

        Utils.toastMessageLong("Las contraseñas no coinciden", this)
    }
}
