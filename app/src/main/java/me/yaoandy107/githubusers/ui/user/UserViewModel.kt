package me.yaoandy107.githubusers.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import me.yaoandy107.githubusers.model.User
import me.yaoandy107.githubusers.repository.UserRepository

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private var currentQueryValue: String? = null
    private var currentSearchResult: Flow<PagingData<User>>? = null

    fun searchUsers(query: String): Flow<PagingData<User>> {
        val lastResult = currentSearchResult
        if (query == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = query
        val newResult: Flow<PagingData<User>> =
            repository.getQueryUsers(query).cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }
}