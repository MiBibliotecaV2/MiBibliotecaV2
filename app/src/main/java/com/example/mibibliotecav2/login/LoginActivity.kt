package com.example.mibibliotecav2.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mibibliotecav2.MainActivity
import com.example.mibibliotecav2.R
import com.example.mibibliotecav2.registro.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern


class LoginActivity : AppCompatActivity() {

    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    //parte del codigo arreglada
    override fun onStart() {
        super.onStart()
        val user = mAuth.currentUser
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        IB_facebook_login.setOnClickListener {
            Toast.makeText(this, "Disponible próximamente", Toast.LENGTH_SHORT).show()
        }

        IB_gmail_login.setOnClickListener {
            Toast.makeText(this, "Disponible próximamente", Toast.LENGTH_SHORT).show()
        }

        BT_ingresar_registro.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))

        }
        BT_ingresar_login.setOnClickListener {

            val correologin = ET_correo_login.text.toString()
            val contrasenalogin = ET_contrasena_login.text.toString()

            if (correologin.isEmpty() || contrasenalogin.isEmpty()) {
                Toast.makeText(this, "Por favor, llene los campos", Toast.LENGTH_SHORT).show()
            } else if (!isValidEmailId(correologin)) {
                Toast.makeText(this, "El correo ingresado no es válido", Toast.LENGTH_SHORT)
                    .show()
            } else {
                mAuth.signInWithEmailAndPassword(correologin, contrasenalogin)
                    .addOnCompleteListener(
                        this
                    ) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()

                        } else {
                            // If sign in fails, display a message to the user.
                            var errorfirebase: String = task.exception!!.message.toString()
                            when (errorfirebase) {
                                "There is no user record corresponding to this identifier. The user may have been deleted." ->
                                    Toast.makeText(this, "El usuario no existe", Toast.LENGTH_SHORT)
                                        .show()
                                "The password is invalid or the user does not have a password." ->
                                    Toast.makeText(
                                        this,
                                        "La clave es incorrecta",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                else -> Toast.makeText(this, errorfirebase, Toast.LENGTH_SHORT)
                                    .show()

                            }

                        }
                    }

            }


        }
    }

    private fun isValidEmailId(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }
}