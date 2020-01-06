package com.example.myapplication.model

import com.google.android.gms.maps.model.LatLng

class Coordinates(val latitude: String = "", val longitude: String = ""){
    fun isEmpty(): Boolean = latitude == "" || longitude == ""
    fun getCords(): LatLng = LatLng(latitude.toDouble(), longitude.toDouble())
}
