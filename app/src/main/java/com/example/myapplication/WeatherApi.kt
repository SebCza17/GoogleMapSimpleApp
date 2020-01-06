package com.example.myapplication

import com.beust.klaxon.Klaxon
import com.example.myapplication.model.PlaceJeson
import java.io.FileNotFoundException
import java.net.URL
class WeatherApi(val coreURL: String){

    fun getPlaces(placeName: String, offset: Int = 0, limit: Int = 20): PlaceJeson?{
        return try {
            Klaxon().parse<PlaceJeson>(
                URL(
                    coreURL +
                            "place" +
                            "?fmt=json" +
                            "&query=" + placeName +
                            "&limit=" + limit +
                            "&offset=" + offset
                ).readText()
            )
        }catch (ex: FileNotFoundException){
            println("Catch FileNotFoundException")
            null
        }
    }
}

