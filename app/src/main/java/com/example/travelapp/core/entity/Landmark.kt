package com.example.travelapp.core.entity

// Landmark model for usage in the core layer
data class Landmark(
    val landmarkId:Int,
    val name: String,
    val description: String,
    val cityId: Int,
)