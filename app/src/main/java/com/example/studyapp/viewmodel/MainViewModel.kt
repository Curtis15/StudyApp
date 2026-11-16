package com.example.studyapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.studyapp.data.StudySession
import com.example.studyapp.data.User
import com.example.studyapp.data.UserRepository
import com.example.studyapp.data.UserRepositoryImpl
import kotlin.collections.toMutableList
import com.example.studyapp.mockbackend.mockBackend
import com.example.studyapp.mockbackend.sessions as repo

class MainViewModel : ViewModel(){
    private val userRepo: UserRepository = UserRepositoryImpl()
    var currentUser by mutableStateOf<User?>(null)
        private set
    var sessions by mutableStateOf(listOf<StudySession>())
        private set
    var courseFilter by mutableStateOf("")
        private set
    var buildingFilter by mutableStateOf("")
        private set

    val filteredSessions get() = sessions.filter{s->
        !s.isEnded &&
        (courseFilter.isBlank() || s.course.contains(courseFilter, ignoreCase = true)) &&
        (buildingFilter.isBlank() || s.building == buildingFilter)
    }
    val mySessions get() = sessions.filter(){s ->
        val name = currentUser?.name
        !s.isEnded && name != null && (s.hostName == name || name in s.memberNames)
    }

    fun register(name: String, email: String, password: String): Boolean {
        val user = User(name, email, password)
        if (!userRepo.createAccount(user)) return false
        currentUser = user
        return true
    }

    fun emailLogin(email: String, password: String): Boolean {
        val user = userRepo.logIn(email, password) ?: return false   // no match
        currentUser = user
        return true
    }
    fun login(name: String) {
        currentUser = User(name = name, email = "", password = "")
    }

    fun logout() {
        currentUser = null
    }
    fun updateCourseFilter(text: String){courseFilter = text}
    fun updateBuildingFilter(text: String){buildingFilter = text}
    fun getSession(id: String): StudySession? = sessions.find{it.id == id}
    fun create(session : StudySession){
        sessions = sessions + session
    }
    private fun update(id: String, change: (StudySession) -> StudySession) {
        sessions = sessions.map{
            if (it.id == id)
                change(it)
            else it
        }
    }

    fun join(id: String): Boolean {
        val name = currentUser?.name ?: return false
        val s = getSession(id) ?: return false
        if (s.isEnded || name in s.memberNames || s.memberNames.size >= s.maxStudents)
            return false
        update(id){it.copy(memberNames = (it.memberNames + name).toMutableList())}
        return true
    }

    fun leave(id: String) {
        val name = currentUser?.name ?: return
        update(id) { it.copy(memberNames = (it.memberNames - name).toMutableList())}
    }

    fun end(id: String) {
        update(id) { it.copy(isEnded = true) }
    }

    init {
        mockBackend()
        sessions = repo.getSessions().toList()
        userRepo.createAccount(User("Ed", "ed@astate.edu", "1234"))
    }
}