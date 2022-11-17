package com.ornekapp.kuryemusteri.util

import androidx.annotation.Keep

@Keep
interface Singleton { // Kullancı bilgileri için gerekli class
    companion object UserInformation {
        lateinit var email: String
        lateinit var username: String
        lateinit var phone : String
        var usertype : Int = 0
    }
}