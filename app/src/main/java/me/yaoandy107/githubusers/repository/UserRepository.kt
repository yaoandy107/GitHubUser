package me.yaoandy107.githubusers.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import me.yaoandy107.githubusers.data.ResultData
import me.yaoandy107.githubusers.data.RetrofitService
import me.yaoandy107.githubusers.data.UserApi
import me.yaoandy107.githubusers.model.User
import me.yaoandy107.githubusers.model.UserDto

class UserRepository {

    private val userApi: UserApi = RetrofitService.createService(UserApi::class.java)

    fun getQueryUsers(
        query: String,
        sort: String?,
        order: String = "desc"
    ): Flow<ResultData<List<User>>> =
        flow {
            emit(ResultData.Loading)
            try {
                val response = userApi.getQueryUsers(query, sort, order)
                if (response.isSuccessful) {
                    val users: List<User> = UserDto.fromResponse(response.body()!!)
                    emit(ResultData.Success(users))
                }
                emit(ResultData.Error("[${response.code()}] ${response.errorBody()}"))
            } catch (e: Exception) {
                emit(ResultData.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.Default).conflate()
}