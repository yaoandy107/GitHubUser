package me.yaoandy107.githubusers.model

object UserDto {
    fun fromResponse(response: UserResponse): List<User> {
        val userItems = response.items
        val users: MutableList<User> = ArrayList()
        for (userItem in userItems) {
            val user = User(
                id = userItem.id,
                name = userItem.login,
                avatarUrl = userItem.avatarUrl
            )
            users.add(user)
        }
        return users
    }

}