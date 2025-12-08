package ru.shrprnbw.ideas.data.mapper

import ru.shrprnbw.ideas.data.remote.dto.response.UserInfoDto
import ru.shrprnbw.ideas.domain.entity.UserInfo

fun UserInfoDto.toEntity(): UserInfo {
    return UserInfo(
        id = this.id,
        username = this.username,
        name = this.name,
        surname = this.surname,
        email = this.email,
    )
}