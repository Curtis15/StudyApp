package com.example.studyapp.data

interface SessionStore {
    fun getSessions(): List<StudySession> // returns sessions
    fun createSession(session: StudySession) // creates the sessions
    fun joinSession(sessionId: String, userName: String): Boolean // adds name to session members
    fun leaveSession(sessionId: String, userName: String) // removes the names from members
    fun endSession(sessionId: String) // ends the session
}