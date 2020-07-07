package me.yaoandy107.githubusers.data

sealed class ResultData<out R> {

    data class Success<out T>(val data: T) : ResultData<T>()
    data class Error(val message: String) : ResultData<Nothing>()
    object Loading : ResultData<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$message]"
            Loading -> "Loading"
        }
    }
}