package com.ornekapp.kuryemusteri.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ornekapp.kuryemusteri.R
import com.ornekapp.kuryemusteri.model.Order
import com.ornekapp.kuryemusteri.service.ApiService
import com.ornekapp.kuryemusteri.util.Singleton
import kotlinx.android.synthetic.main.fragment_user_information.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserInformationFragment : Fragment() {
    private lateinit var singleton: Singleton.UserInformation
    private lateinit var service: ApiService
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_information, container, false)
    }
// Kullanıcı bilgilerinin görüntülendiği kod parçası.

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        singleton = Singleton
        service = ApiService()
        auth = Firebase.auth

        setupButtonClick()

        user_fragment_username.hint = singleton.username
        user_fragment_email.hint = singleton.email
        user_fragment_phone.hint = singleton.phone

        if (singleton.usertype == 0) {
            user_fragment_user_type.text = getString(R.string.costumer)
            user_fragment_total_earn_layout.visibility = View.GONE
            user_fragment_confirm_license.visibility = View.GONE
        } else {
            user_fragment_user_type.text = getString(R.string.messenger)
            user_fragment_total_earn_layout.visibility = View.VISIBLE
            user_fragment_confirm_license.visibility = View.VISIBLE

            service.getMyOrderMessenger(auth.currentUser!!.uid).enqueue(object :
                Callback<List<Order>> {
                override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                    response.body()?.let {
                        var totalEarn = 0
                        for (order in it) {
                            totalEarn +=(order.price)
                        }
                        user_fragment_total_earn.text = totalEarn.toString()
                    }
                }

                override fun onFailure(call: Call<List<Order>>, t: Throwable) {t.printStackTrace()}
            })

        }
    }

    private fun setupButtonClick() {
        user_fragment_password_change.setOnClickListener {
            val passwordChangeFragment = PasswordChangeFragment()
            val manager = requireActivity().supportFragmentManager
            val transaction = manager.beginTransaction()
            transaction.replace(R.id.main_activity_frame_layout, passwordChangeFragment)
            transaction.commit()
        }

        user_fragment_confirm_license.setOnClickListener {
            Toast.makeText(requireContext(),getString(R.string.no_access),Toast.LENGTH_SHORT).show()
        }
    }
}