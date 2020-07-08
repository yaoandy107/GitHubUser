package me.yaoandy107.githubusers.data

import androidx.paging.PagingSource
import me.yaoandy107.githubusers.model.User
import me.yaoandy107.githubusers.model.UserDto
import retrofit2.HttpException
import java.io.IOException


class UserPageSource(
    private val api: UserApi,
    private val query: String
) : PagingSource<Int, User>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        val position = params.key ?: GITHUB_STARTING_PAGE_INDEX
        return try {
            val response = api.getQueryUsers(query, position, params.loadSize)
            val users = UserDto.fromResponse(response.body()!!)
            LoadResult.Page(
                data = (users),
                prevKey = if (position == GITHUB_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (users.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    companion object {
        private const val GITHUB_STARTING_PAGE_INDEX = 1
    }
}