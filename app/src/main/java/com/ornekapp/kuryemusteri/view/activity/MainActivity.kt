package com.ornekapp.kuryemusteri.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ornekapp.kuryemusteri.R
import com.ornekapp.kuryemusteri.util.Singleton
import com.ornekapp.kuryemusteri.view.fragment.MainFragment
import com.ornekapp.kuryemusteri.view.fragment.MessengerMainFragment
import com.ornekapp.kuryemusteri.view.fragment.MyOrdersFragment
import com.ornekapp.kuryemusteri.view.fragment.UserInformationFragment
import com.ornekapp.kuryemusteri.view.util.HamburgerDrawable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_drawer.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var singleton: Singleton.UserInformation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        singleton = Singleton


        val toggle = object : ActionBarDrawerToggle(
            this,
            main_activity_drawer_layout,
            main_activity_toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        ) {}
        toggle.setDrawerArrowDrawable(HamburgerDrawable(this))
        main_activity_drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        val mainFragment = MainFragment()
        val messengerMainFragment = MessengerMainFragment()
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()

        if (singleton.usertype == 0) {
            transaction.replace(R.id.main_activity_frame_layout, mainFragment)
        } else {
            transaction.replace(R.id.main_activity_frame_layout, messengerMainFragment)
        }

        transaction.commit()

        setupButtonClick()
    }

    private fun setupButtonClick() {

        navigation_drawer_user_information.setOnClickListener {
            onBackPressed()
            val userInformationFragment = UserInformationFragment()
            val manager = supportFragmentManager
            val transaction = manager.beginTransaction()
            transaction.replace(R.id.main_activity_frame_layout, userInformationFragment)
            transaction.commit()
        }

        navigation_drawer_new_order.setOnClickListener {
            onBackPressed()
            val mainFragment = MainFragment()
            val messengerMainFragment = MessengerMainFragment()
            val manager = supportFragmentManager
            val transaction = manager.beginTransaction()
            if (singleton.usertype == 0) {
                transaction.replace(R.id.main_activity_frame_layout, mainFragment)
            } else {
                transaction.replace(R.id.main_activity_frame_layout, messengerMainFragment)
            }
            transaction.commit()
        }

        navigation_drawer_my_order.setOnClickListener {
            onBackPressed()
            val myOrdersFragment = MyOrdersFragment()
            val manager = supportFragmentManager
            val transaction = manager.beginTransaction()
            transaction.replace(R.id.main_activity_frame_layout, myOrdersFragment)
            transaction.commit()
        }

        navigation_drawer_sign_out.setOnClickListener {
            onBackPressed()
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        if (main_activity_drawer_layout.isDrawerOpen(GravityCompat.START)) {
            main_activity_drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}