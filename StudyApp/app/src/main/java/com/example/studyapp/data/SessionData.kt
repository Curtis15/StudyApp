package com.example.studyapp.data

var sessionIdCounter: Int = 0

interface SessionRepository {
    fun getSessions(): List<StudySession> // returns sessions
    fun createSession(session: StudySession) // creates the sessions
    fun joinSession(sessionId: String, userName: String): Boolean // adds name to session members
    fun leaveSession(sessionId: String, userName: String) // removes the names from members
    fun endSession(sessionId: String) // ends the session

    //Filter methods
}

class SessionRepositoryImpl : SessionRepository{
    private val sessions = mutableListOf<StudySession>()

    override fun getSessions(): List<StudySession>{
        return sessions
    }

    override fun createSession(session: StudySession){
        sessions.add(session)
        sessionIdCounter++
    }

    override fun joinSession(sessionId: String, userName: String): Boolean{
        for (session in sessions){
            if (session.id == sessionId){
                session.memberNames.add(userName)
                return true
            }
        }
        return false
    }

    override fun leaveSession(sessionId: String, userName: String){
        for (session in sessions){
            if (session.id == sessionId){
                session.memberNames.remove(userName)
            }
        }
    }

    override fun endSession(sessionId: String){
        for (session in sessions){
            if (session.id == sessionId){
                sessions.remove(session)
            }
        }
    }
}
