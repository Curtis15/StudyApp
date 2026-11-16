package com.example.studyapp.data

data class User(
    val name: String,
    val email: String,
    val password: String,
    var loggedIn: Boolean = false
)
