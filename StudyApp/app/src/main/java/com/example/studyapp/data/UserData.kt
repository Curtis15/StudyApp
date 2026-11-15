package com.example.studyapp.data

interface UserRepository {
    fun createAccount(account: User)
    fun logIn(account: User)
}

class UserRepositoryImpl : UserRepository{
    private val users = mutableMapOf<String, String>()

    override fun createAccount(account: User){
        users[account.password] = account.name
    }

    override fun logIn(account: User){
        account.loggedIn = true
        users[account.password] = account.name
    }
}
