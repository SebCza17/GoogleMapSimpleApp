package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import com.example.myapplication.model.PlaceJeson

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.timePicker
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.net.URL
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.concurrent.timer

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val searchView = findViewById<SearchView>(R.id.searchView)
        val weatherApi = WeatherApi("http://musicbrainz.org/ws/2/")
        val markerList = hashMapOf<Int, ArrayList<Marker>>()
        var timeFlag = 0

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                    doAsync {
                        var placeJesonTMP = weatherApi.getPlaces(query.toString())
                        val placeJeson = PlaceJeson()

                        for (offset in 0..placeJesonTMP?.count!! / 20) {
                            for (i in 0 until placeJesonTMP!!.places.size){
                                if(placeJesonTMP.places[i].life_span.isNew()){

                                    placeJesonTMP.places.get(i)?.let { placeJeson.places.add(it) }

                                }
                            }
                            placeJesonTMP = weatherApi.getPlaces(query.toString(), offset * 20)
                        }
                        println(placeJeson.places.size)
                        uiThread {
                            for (i in 0 until placeJeson.places.size){
                                if (!placeJeson.places[i].coordinates.isEmpty()){
                                    val cords = placeJeson.places[i].coordinates.getCords()
                                    val timeLife = placeJeson.places[i].life_span.getLifeSpan()
                                    val marker = mMap.addMarker(MarkerOptions().position(cords).title(placeJeson.places[i].name))

                                    if(markerList[timeLife] == null){
                                        val markerInList = ArrayList<Marker>()
                                        markerInList.add(marker)
                                        markerList[timeLife] = markerInList
                                    }else
                                        markerList[timeLife]?.add(marker)

                                }
                            }
                            timeFlag = 1
                            println(markerList.keys)
                        }
                    }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mMap.clear()
                return false
            }
        })
        doAsync {
            Timer("SettingUp", true).scheduleAtFixedRate(1000, 1000) {
                println(timeFlag)
                if (timeFlag != 0) {
                    if (timeFlag <= 30) {
                        if (markerList[timeFlag]?.size != 0 && markerList[timeFlag] != null) {
                            uiThread {
                                println("remove $timeFlag size " + markerList[timeFlag]?.size)
                                val marker: Marker? = markerList[timeFlag]?.removeAt(0)
                                marker?.remove()
                            }
                        } else
                            timeFlag++
                    } else
                        timeFlag = 0
                }
            }
        }
    }
}
