package me.yaoandy107.githubusers.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import me.yaoandy107.githubusers.data.UserApi
import me.yaoandy107.githubusers.data.UserPageSource
import me.yaoandy107.githubusers.model.User

class UserRepository(private val api: UserApi) {

    fun getQueryUsers(
        query: String
    ): Flow<PagingData<User>> =
        Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { UserPageSource(api, query) }
        ).flow

    companion object {
        private const val NETWORK_PAGE_SIZE = 10
    }
}