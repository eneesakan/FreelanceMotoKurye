package com.ornekapp.kuryemusteri.view.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ornekapp.kuryemusteri.R
import com.ornekapp.kuryemusteri.model.StatusApiResponse
import com.ornekapp.kuryemusteri.service.ApiService
import com.ornekapp.kuryemusteri.util.GPSTracker
import kotlinx.android.synthetic.main.fragment_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainFragment : Fragment() {
    private var packetTypeIndex: Int = 0
    private lateinit var service: ApiService
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButtonClick()

        packetTypeSpinner()
        auth = Firebase.auth
        service = ApiService()

    }


    private fun setupButtonClick() {
        main_fragment_order.setOnClickListener {
            locationPermission()
        }
    }

    private fun locationPermission() {
        val permission = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionsResultCallback.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            newOrder()
        }
    }

    private fun newOrder() { // Kullanıcın yeni sipraiş vermesi için gerekli olan kod parçası
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val mGPS = GPSTracker(requireContext())
            if (mGPS.canGetLocation()) {
                mGPS.location

                val recipientName = main_fragment_recipient_name.text
                val address = main_fragment_address.text
                val price = main_fragment_price.text

                if (recipientName.isNotEmpty() && address.isNotEmpty() && price.isNotEmpty() && packetTypeIndex != 0) {

                    var lati = mGPS.latitude.toString()
                    var longi = mGPS.longitude.toString()

                    if (lati.isEmpty() || lati == null) {
                        lati = 39.928300.toString();
                    }
                    if (longi.isEmpty() || longi == null) {
                        longi = 32.854673.toString();
                    }

                    service.newOrder(
                        auth.currentUser!!.uid,
                        mGPS.latitude.toString(),
                        mGPS.longitude.toString(),
                        recipientName.toString(),
                        address.toString(),
                        (packetTypeIndex - 1),
                        price.toString().toInt()
                    ).enqueue(object :
                        Callback<StatusApiResponse> {
                        override fun onResponse(
                            call: Call<StatusApiResponse>,
                            response: Response<StatusApiResponse>
                        ) {
                            response.body()?.let {
                                if (it.status == 1) {
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.order_created),
                                        Toast.LENGTH_LONG
                                    )
                                        .show()

                                    val myOrdersFragment = MyOrdersFragment()
                                    val manager = requireActivity().supportFragmentManager
                                    val transaction = manager.beginTransaction()
                                    transaction.replace(
                                        R.id.main_activity_frame_layout,
                                        myOrdersFragment
                                    )
                                    transaction.commit()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.there_is_a_problem),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<StatusApiResponse>, t: Throwable) {
                            t.printStackTrace()
                        }
                    })
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.empty_field),
                        Toast.LENGTH_LONG
                    ).show()
                }

            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.there_is_a_problem),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.there_is_a_problem),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    private fun packetTypeSpinner() { // Paket tipinin seçilmesi için gerekli adapter
        val packetType = main_fragment_packet_type

        val list = listOf(
            getString(R.string.select),
            getString(R.string.box),
            getString(R.string.document)
        )

        val adapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            list
        ) {
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
                if (position == packetType.selectedItemPosition) {
                    view.setTextColor(requireActivity().getColor(R.color.firstColor))
                }

                return view
            }
        }

        packetType.adapter = adapter
        packetType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                packetTypeIndex = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        when (it) {
            true -> {
                newOrder()
            }
            false -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.no_permissions),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}