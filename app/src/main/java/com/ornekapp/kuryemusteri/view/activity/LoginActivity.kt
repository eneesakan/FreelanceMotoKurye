package com.ornekapp.kuryemusteri.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ornekapp.kuryemusteri.R
import com.ornekapp.kuryemusteri.model.User
import com.ornekapp.kuryemusteri.service.ApiService
import com.ornekapp.kuryemusteri.util.Singleton
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.sin

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var service: ApiService
    private lateinit var singleton: Singleton.UserInformation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth
        service = ApiService()
        singleton = Singleton

        setupButtonClick()
    }

    private fun setupButtonClick() {
        main_activity_sign_in.setOnClickListener {
            signIn()
        }
        main_activity_forgot_password.setOnClickListener {
            forgotPassword()
        }
        main_activity_sign_up.setOnClickListener {
            val intent = Intent(this,SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signIn() {
        val email = login_activity_email.text
        val password = login_activity_password.text
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email.toString(),password.toString()).addOnSuccessListener {
                service.getUserInformation(auth.currentUser!!.uid).enqueue(object : Callback<User>{
                    override fun onResponse(call: Call<User>, response: Response<User>) {

                        response.body()?.let {
                            singleton.email = it.email
                            singleton.phone = it.phone
                            singleton.username = it.username
                            singleton.usertype = it.usertype

                            Toast.makeText(this@LoginActivity,getString(R.string.sign_in_success),Toast.LENGTH_LONG).show()
                            val intent = Intent(this@LoginActivity,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    override fun onFailure(call: Call<User>, t: Throwable) { t.printStackTrace() }
                })
            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this,getString(R.string.empty_field),Toast.LENGTH_LONG).show()
        }
    }

    private fun forgotPassword() {
        val email = login_activity_email.text
        if (email.isNotEmpty()) {
            auth.sendPasswordResetEmail(email.toString()).addOnCompleteListener {
                Toast.makeText(this,getString(R.string.password_reset_send_email),Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this,getString(R.string.enter_email),Toast.LENGTH_LONG).show()
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            service.getUserInformation(currentUser.uid).enqueue(object : Callback<User>{
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    response.body()?.let {
                        singleton.email = it.email
                        singleton.phone = it.phone
                        singleton.username = it.username
                        singleton.usertype = it.usertype

                        val intent = Intent(this@LoginActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                override fun onFailure(call: Call<User>, t: Throwable) { t.printStackTrace() }
            })
        }
    }
}