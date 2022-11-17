package com.ornekapp.kuryemusteri.service

import com.ornekapp.kuryemusteri.model.Order
import com.ornekapp.kuryemusteri.model.StatusApiResponse
import com.ornekapp.kuryemusteri.model.User
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiService {

    private val baseUrl = "https://yigithanyaramis.com/apps/kuryemusteri/"
// Kullanıcı verilerinin tutulması için gerekli veritabanı işlemlerinin yapıldığı kısım. Burada baseUrl' de ki hostinge kurulu olan veritabanına
// gerekli işlemler yapılmaktadır.
    private val api = Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(Api::class.java)

    fun signUp(
        uid : String,
        email: String,
        username: String,
        phone: String,
        usertype: Int
    ): Call<StatusApiResponse> {
        return api.signUp(uid, email, username, phone, usertype)
    }

    fun getUserInformation(uid : String): Call<User> {
        return api.getUserInformation(uid)
    }

    fun getOrder(uid : String): Call<List<Order>> {
        return api.getOrder(uid)
    }

    fun newOrder(
        uid : String,
        lati: String,
        longi: String,
        recipientname: String,
        address: String,
        packettype: Int,
        price: Int
    ): Call<StatusApiResponse> {
        return api.newOrder(uid, lati, longi, recipientname, address, packettype, price)
    }

    fun getMyOrderUser(uid : String): Call<List<Order>> {
        return api.getMyOrderUser(uid)
    }

    fun getMyOrderMessenger(uid : String): Call<List<Order>> {
        return api.getMyOrderMessenger(uid)
    }

    fun updateOrder(id : Int,uid : String): Call<StatusApiResponse> {
        return api.updateOrder(id,uid)
    }

    fun statusUpdateOrder(id : Int,status : Int): Call<StatusApiResponse> {
        return api.statusUpdateOrder(id,status)
    }
}