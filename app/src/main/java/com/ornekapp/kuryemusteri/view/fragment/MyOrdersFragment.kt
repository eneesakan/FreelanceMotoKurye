package com.ornekapp.kuryemusteri.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ornekapp.kuryemusteri.R
import com.ornekapp.kuryemusteri.adapter.OrderRecyclerAdapter
import com.ornekapp.kuryemusteri.model.Order
import com.ornekapp.kuryemusteri.service.ApiService
import com.ornekapp.kuryemusteri.util.Singleton
import kotlinx.android.synthetic.main.fragment_my_orders.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyOrdersFragment : Fragment() {
    private lateinit var recyclerAdapter : OrderRecyclerAdapter
    private lateinit var singleton: Singleton.UserInformation
    private lateinit var service: ApiService
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerAdapter = OrderRecyclerAdapter(arrayListOf(),requireActivity())

        singleton = Singleton
        service = ApiService()
        auth = Firebase.auth

        my_orders_recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        my_orders_recycler.adapter = recyclerAdapter

        if (singleton.usertype == 0) {

            getUserOrder()
        } else {
            getMessengerOrder()
        }
    }
// Kullanıcıların tüm siparişlerinin listelendiği kod parçası


    private fun getUserOrder() {
        service.getMyOrderUser(auth.currentUser!!.uid).enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                response.body()?.let {
                    var tempList = arrayListOf<Order>()
                    for (order in it) {
                        tempList.add(order)
                    }
                    recyclerAdapter.updateRecyclerData(tempList)

                }

            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {t.printStackTrace()}
        })
    }

    private fun getMessengerOrder() {
        service.getMyOrderMessenger(auth.currentUser!!.uid).enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                response.body()?.let {
                    var tempList = arrayListOf<Order>()
                    for (order in it) {
                        tempList.add(order)
                    }
                    recyclerAdapter.updateRecyclerData(tempList)
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {t.printStackTrace()}
        })
    }
}