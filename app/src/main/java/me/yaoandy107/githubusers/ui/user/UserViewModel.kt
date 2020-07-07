package me.yaoandy107.githubusers.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import me.yaoandy107.githubusers.data.ResultData
import me.yaoandy107.githubusers.model.User
import me.yaoandy107.githubusers.repository.UserRepository

class UserViewModel : ViewModel() {

    private val repository: UserRepository = UserRepository()

    fun getQueryUsers(
        query: String,
        sort: String? = null,
        order: String = "desc"
    ): LiveData<ResultData<List<User>>> = repository.getQueryUsers(query, sort, order).asLiveData()
}