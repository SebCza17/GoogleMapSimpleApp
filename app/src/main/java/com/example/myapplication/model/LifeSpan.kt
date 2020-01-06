package com.example.myapplication.model

class LifeSpan(val begin: String = ""){
    fun isNew(): Boolean {
        if (begin != "")
            return begin.substring(0, 4).toInt() >= 1990
        else
            return false
    }

    fun getLifeSpan(): Int = begin.substring(0, 4).toInt() - 1990
}
