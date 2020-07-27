package com.example.mibibliotecav2.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mibibliotecav2.MainActivity
import com.example.mibibliotecav2.R
import com.example.mibibliotecav2.registro.RegisterActivity
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.regex.Pattern


class LoginActivity : AppCompatActivity() {

    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1234
    private lateinit var callbackManager: CallbackManager

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
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.activity_login)

        callbackManager = CallbackManager.Factory.create()

        BT_facebook_in.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                handleFacebookAccessToken(result?.accessToken)
            }

            override fun onCancel() {
                TODO("Not yet implemented")
            }

            override fun onError(error: FacebookException?) {
                TODO("Not yet implemented")
            }

        })


        //printHashKey(this)
        configureGoogle()
        BT_facebook_in.setReadPermissions("email")

        BT_google_gmail.setOnClickListener {
            signIn()
        }

        BT_ingresar_registro.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))

        }
        BT_ingresar_login.setOnClickListener {

            val correologin = ET_correo_login.text.toString()
            val contrasenalogin = ET_contrasena_login.text.toString()

            singInWithEmailAndPassword(correologin, contrasenalogin)


        }
    }

    fun printHashKey(pContext: Context) {
        try {
            val info: PackageInfo = pContext.packageManager
                .getPackageInfo(pContext.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("TAG", "printHashKey()", e)
        }
    }


    private fun configureGoogle() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                //Log.d("Google SignIN", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Google SignIN", "Google sign in failed", e)
                // ...
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(this, "Autenticación Fallida", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun singInWithEmailAndPassword(correologin: String, contrasenalogin: String) {
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
                        val errorfirebase: String = task.exception!!.message.toString()
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

    private fun handleFacebookAccessToken(token: AccessToken?) {
        Log.d("Facebook", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token!!.token)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this,
                        "Auntenticación fallida ${task.result}",
                        Toast.LENGTH_SHORT
                    ).show()

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