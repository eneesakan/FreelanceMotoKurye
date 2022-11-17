package com.ornekapp.kuryemusteri.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class User(
    @SerializedName("email")
    var email : String,
    @SerializedName("username")
    var username : String,
    @SerializedName("phone")
    var phone : String,
    @SerializedName("usertype")
    var usertype : Int
)