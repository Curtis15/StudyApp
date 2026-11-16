package com.example.studyapp.mockbackend

import com.example.studyapp.data.Buildings
import com.example.studyapp.data.SessionRepository
import com.example.studyapp.data.SessionRepositoryImpl
import com.example.studyapp.data.StudySession
import java.util.Calendar

private fun millis(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
    val c = Calendar.getInstance()
    c.clear()
    c.set(year, month - 1, day, hour, minute)
    return c.timeInMillis
}

var sessions: SessionRepository = SessionRepositoryImpl()

fun mockBackend(){
    var session1 = StudySession(
        "1",
        "Computer Org Study Sesh",
        "Computer Organization",
        Buildings[0].name,
        "Third Floor of Library",
        millis(2026, 11, 18, 17, 0),
        millis(2026, 11, 18, 19, 30),
        4,
        "Ed",
        mutableListOf("Julia", "Ed")
    )

    var session2 = StudySession(
        "2",
        "Digital Electronics Study Sesh",
        "Digital Electronics",
        Buildings[1].name,
        "Howl's Hub",
        millis(2026, 11, 18, 17, 0),
        millis(2026, 11, 18, 19, 30),
        5,
        "Eddy",
        mutableListOf("Ed", "Edd", "Eddy")
    )

    sessions.createSession(session1)
    sessions.createSession(session2)
}
