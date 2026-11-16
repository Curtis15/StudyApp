package com.example.studyapp.data

// session data
data class StudySession(
    val id: String,
    val title: String,
    val course: String,
    val building: String,
    val room: String,
    val startTime: Long,
    val endTime: Long,
    val maxStudents: Int,
    val hostName: String,
    var memberNames: MutableList<String>,
    val isEnded: Boolean = false
)