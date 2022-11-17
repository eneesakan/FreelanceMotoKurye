package com.ornekapp.kuryemusteri.service

import com.ornekapp.kuryemusteri.model.Order
import com.ornekapp.kuryemusteri.model.StatusApiResponse
import com.ornekapp.kuryemusteri.model.User
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface Api {
    @POST("signup.php")
    @FormUrlEncoded
    fun signUp(@Field("uid") uid : String,
               @Field("email") email : String,
               @Field("username") username : String,
               @Field("phone") phone : String,
               @Field("usertype") usertype : Int
    ) : Call<StatusApiResponse>

    @POST("userinformation.php")
    @FormUrlEncoded
    fun getUserInformation(@Field("uid") uid : String) : Call<User>

    @POST("order.php")
    @FormUrlEncoded
    fun getOrder(@Field("uid") uid : String) : Call<List<Order>>

    @POST("neworder.php")
    @FormUrlEncoded
    fun newOrder(@Field("uid") uid : String,
                 @Field("lati") lati : String,
                 @Field("longi") longi : String,
                 @Field("recipientname") recipientname : String,
                 @Field("address") address : String,
                 @Field("packettype") packettype : Int,
                 @Field("price") price : Int,
    ) : Call<StatusApiResponse>

    @POST("myorder.php")
    @FormUrlEncoded
    fun getMyOrderUser(@Field("uid") uid : String) : Call<List<Order>>

    @POST("myordermessenger.php")
    @FormUrlEncoded
    fun getMyOrderMessenger(@Field("uid") uid : String) : Call<List<Order>>

    @POST("updateorder.php")
    @FormUrlEncoded
    fun updateOrder(@Field("id") id : Int,@Field("uid") uid : String) : Call<StatusApiResponse>

    @POST("statusupdateorder.php")
    @FormUrlEncoded
    fun statusUpdateOrder(@Field("id") id : Int,@Field("status") status : Int) : Call<StatusApiResponse>


}