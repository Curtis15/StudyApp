package com.example.studyapp.data

// Building name and location
data class Building(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

val Buildings = listOf(
    Building("Dean B. Ellis Library", 0.0, 0.0),
    Building("Student Union", 0.0, 0.0)
)
