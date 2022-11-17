package com.ornekapp.kuryemusteri.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Order(
    // Order için mobil uygulama kullanıcısını veritabanında tutulması için gerekli değerlerin tutulduğu classdır.
    @SerializedName("id")
    var id : Int,
    @SerializedName("lati")
    var lati : String,
    @SerializedName("longi")
    var longi : String,
    @SerializedName("recipientname")
    var recipientname : String,
    @SerializedName("address")
    var address : String,
    @SerializedName("packettype")
    var packettype : Int,
    @SerializedName("price")
    var price : Int,
    @SerializedName("orderstatus")
    var orderstatus : Int
)