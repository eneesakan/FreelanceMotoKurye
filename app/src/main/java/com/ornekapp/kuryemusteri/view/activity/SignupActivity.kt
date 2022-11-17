package com.ornekapp.kuryemusteri.view.activity

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ornekapp.kuryemusteri.R
import com.ornekapp.kuryemusteri.model.StatusApiResponse
import com.ornekapp.kuryemusteri.service.ApiService
import com.ornekapp.kuryemusteri.util.Singleton
import kotlinx.android.synthetic.main.activity_signup.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    var userTypeIndex : Int = 0
    private lateinit var auth: FirebaseAuth
    private lateinit var service: ApiService
    private lateinit var singleton: Singleton.UserInformation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = Firebase.auth
        service = ApiService()
        singleton = Singleton

        setupButtonClick()

        userTypeSpinner()
    }

    private fun userTypeSpinner() {
        val userType = sign_up_activity_type

        val list = listOf(
            getString(R.string.select),
            getString(R.string.costumer),
            getString(R.string.messenger)
        )

        // initialize an array adapter for spinner
        val adapter: ArrayAdapter<String> = object: ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            list
        ){
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(
                    position,
                    convertView,
                    parent
                ) as TextView
                view.setTypeface(view.typeface, Typeface.BOLD)
                if (position == userType.selectedItemPosition){
                    view.setTextColor(getColor(R.color.firstColor))
                }

                return view
            }
        }

        userType.adapter = adapter
        userType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                userTypeIndex = position
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupButtonClick() {
        sign_up_activity_back.setOnClickListener {
            finish()
        }

        sign_up_activity_sign_up.setOnClickListener {
            signUp()
        }
    }
// Kullanıcıların bilgilerini girmesi dahilinde gerekli sorgular sonucunda kayıt işlemi gerçekleştirilir.
// Bu işlem firebase ile gerçek zamanlı olarak veritabanına işlenmektedir.
// Toast Output ile bildirimler verilir.
    private fun signUp() {
        val userName = sign_up_activity_username.text
        val password = sign_up_activity_password.text
        val email = sign_up_activity_email.text
        val phone = sign_up_activity_phone.text

        if (userName.isNotEmpty() && password.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && userTypeIndex != 0) {
            auth.createUserWithEmailAndPassword(email.toString(),password.toString()).addOnSuccessListener {
                service.signUp(auth.currentUser!!.uid,email.toString(),userName.toString(),phone.toString(),(userTypeIndex-1)).enqueue(object :
                    Callback<StatusApiResponse>{
                    override fun onResponse(
                        call: Call<StatusApiResponse>,
                        response: Response<StatusApiResponse>
                    ) {
                        response.body()?.let {
                            if (it.status == 1) {
                                singleton.usertype = userTypeIndex-1
                                singleton.username = userName.toString()
                                singleton.email = email.toString()
                                singleton.phone = phone.toString()

                                Toast.makeText(this@SignupActivity,getString(R.string.success_sign_up),Toast.LENGTH_LONG).show()

                                val intent = Intent(this@SignupActivity,MainActivity::class.java)
                                startActivity(intent)
                                finish()


                            } else {
                                auth.currentUser!!.delete()
                                Toast.makeText(this@SignupActivity,getString(R.string.there_is_a_problem),Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<StatusApiResponse>, t: Throwable) {t.printStackTrace()}

                })
            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this,getString(R.string.empty_field),Toast.LENGTH_LONG).show()
        }
    }
}