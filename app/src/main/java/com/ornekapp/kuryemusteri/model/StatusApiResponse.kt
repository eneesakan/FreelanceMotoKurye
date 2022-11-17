package com.ornekapp.kuryemusteri.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class StatusApiResponse(
    // Bu class sayesinde servisin durumunun kontrol edildiği kod parçası
    @SerializedName("status")
    @Expose
    var status : Int
)