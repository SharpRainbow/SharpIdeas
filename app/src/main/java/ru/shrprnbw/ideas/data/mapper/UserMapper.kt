package ru.shrprnbw.ideas.data.mapper

import ru.shrprnbw.ideas.data.remote.dto.response.UserDto
import ru.shrprnbw.ideas.domain.entity.User

fun UserDto.toEntity(): User {
    return User(
        id = this.id,
        username = this.username,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
    )
}