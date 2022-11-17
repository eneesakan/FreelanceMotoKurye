package com.ornekapp.kuryemusteri.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ornekapp.kuryemusteri.R
import com.ornekapp.kuryemusteri.model.Order
import com.ornekapp.kuryemusteri.model.StatusApiResponse
import com.ornekapp.kuryemusteri.service.ApiService
import kotlinx.android.synthetic.main.fragment_messenger_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MessengerMainFragment : Fragment(), GoogleMap.OnMarkerClickListener {
    private var selectedId : Int = -1
    private lateinit var service: ApiService
    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        when (it) {
            true -> {
                setupMap()
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

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        val ankara = LatLng(39.928300, 32.854673)

        service.getOrder(auth.currentUser!!.uid).enqueue(object : Callback<List<Order>>{
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                response.body()?.let {
                    for (order in it) {
                        if (order.lati != null && order.lati.isNotEmpty() && order.orderstatus == 0) {
                            val marker = googleMap.addMarker(MarkerOptions().position(LatLng(order.lati.toDouble(), order.longi.toDouble())))
                            marker!!.tag = order.id
                            googleMap.setOnMarkerClickListener(this@MessengerMainFragment);
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<Order>>, t: Throwable) {t.printStackTrace()}
        })

        googleMap.setMinZoomPreference(5f)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(ankara))

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsResultCallback.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            googleMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->

                location?.let{
                    val currentLatLong = LatLng(location.latitude, location.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_messenger_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // Veritabanı bağlantısı için gerekli kod parçası
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setupMap()

        auth = Firebase.auth
        service = ApiService()
        setupButtonClick()
    }

    private fun setupMap() { // Map Settings için gerekli kısım
        val mapFragment = childFragmentManager.findFragmentById(R.id.messenger_main_fragment_map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun setupButtonClick() { // Kullanıcıdan siparişin alındığı kod parçası
        messenger_main_fragment_accept.setOnClickListener {
            if (selectedId >= 0) {
                service.updateOrder(selectedId,auth.currentUser!!.uid).enqueue(object : Callback<StatusApiResponse> {
                    override fun onResponse(
                        call: Call<StatusApiResponse>,
                        response: Response<StatusApiResponse>
                    ) {
                        response.body()?.let {
                            if (it.status == 1) {
                                Toast.makeText(requireContext(),getString(R.string.take_order),Toast.LENGTH_LONG).show()

                                val myOrdersFragment = MyOrdersFragment()
                                val manager = requireActivity().supportFragmentManager
                                val transaction = manager.beginTransaction()
                                transaction.replace(R.id.main_activity_frame_layout, myOrdersFragment)
                                transaction.commit()
                            } else {
                                Toast.makeText(requireContext(),getString(R.string.there_is_a_problem),Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    override fun onFailure(call: Call<StatusApiResponse>, t: Throwable) {t.printStackTrace()}
                })
            } else {
                Toast.makeText(requireContext(),getString(R.string.no_order),Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMarkerClick(p0: Marker): Boolean { // Kullanıcının sipariş tipini seçmesi için gerekli kod parçası
        service.getOrder(auth.currentUser!!.uid).enqueue(object : Callback<List<Order>>{
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                response.body()?.let {
                    for (order in it) {
                        if (p0.tag == order.id) {
                            messenger_main_fragment_name.text = order.recipientname
                            messenger_main_fragment_address.text = order.address
                            var packetType = ""
                            if (order.packettype == 0) {
                                packetType = getString(R.string.box)
                            } else {
                                packetType = getString(R.string.document)
                            }

                            messenger_main_fragment_packettype.text = packetType
                            messenger_main_fragment_price.text = order.price.toString()

                            selectedId = order.id
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<Order>>, t: Throwable) {t.printStackTrace()}
        })
        return false
    }
}
