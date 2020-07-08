package me.yaoandy107.githubusers.data

import me.yaoandy107.githubusers.model.UserResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface UserApi {

    @Headers("Accept: application/json")
    @GET("search/users")
    suspend fun getQueryUsers(
        @Query("q") queryString: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): UserResponse
}