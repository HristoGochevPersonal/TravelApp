package com.example.travelapp.presentation.cityWithLandmarks


// Landmark model for usage in the presentation layer
data class LandmarkModel(
    val id: Int,
    var name: String,
    var description: String,
    var cityId: Int
)
