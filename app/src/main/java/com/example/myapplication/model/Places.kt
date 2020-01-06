package com.example.myapplication.model

import com.beust.klaxon.Json

class Places(val id: String = "", val name: String = "", val address: String = "", val coordinates: Coordinates = Coordinates(),
             @Json(name = "life-span") val life_span: LifeSpan = LifeSpan())
