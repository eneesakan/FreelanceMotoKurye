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
import kotlinx.android.synthetic.main.fragment_password_change.*
import java.util.logging.Level

class PasswordChangeFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_password_change, container, false)
    }
// Kullanıcının şifresini unutması durumunda mobil uygulama aracılığıyla yeni şifre oluşturması için gerekli kod parçasıdır.
// Bu işlem sonucunda kullanıcıya gerekli bildirimler verilmektedir.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        setupButtonClick()
    }

    private fun setupButtonClick() {
        password_change_fragment_change.setOnClickListener {
            val newPassword = password_change_fragment_new_password.text
            val newPasswordAgain = password_change_fragment_new_password_again.text
            if (newPassword.isNotEmpty() && newPasswordAgain.isNotEmpty()) {
                auth.currentUser!!.updatePassword(newPassword.toString()).addOnSuccessListener {
                    Toast.makeText(requireContext(),getString(R.string.password_change_success),Toast.LENGTH_LONG).show()
                    val userInformationFragment = UserInformationFragment()
                    val manager = requireActivity().supportFragmentManager
                    val transaction = manager.beginTransaction()
                    transaction.replace(R.id.main_activity_frame_layout, userInformationFragment)
                    transaction.commit()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(),getString(R.string.empty_field),Toast.LENGTH_LONG).show()
            }
        }
    }
}