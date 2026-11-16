package com.example.studyapp.data

interface UserRepository {
    fun createAccount(account: User): Boolean
    fun logIn(email: String, password: String): User?
}

class UserRepositoryImpl : UserRepository{
    private val users = mutableMapOf<String, User>()

    override fun createAccount(account: User): Boolean{
        if (users.containsKey(account.email)) return false
        users[account.email] = account
        return true
    }

    override fun logIn(email: String, password: String): User? {
        val user = users[email]
        return if (user != null && user.password == password) user else null
    }
}
