package ru.shrprnbw.ideas.data.mapper

import ru.shrprnbw.ideas.data.remote.dto.response.GroupDto
import ru.shrprnbw.ideas.data.remote.dto.response.UserDto
import ru.shrprnbw.ideas.domain.entity.Group
import ru.shrprnbw.ideas.domain.entity.GroupUser

fun GroupDto.toEntity() = Group(
    id = id,
    name = name
)

fun UserDto.toGroupUser() = GroupUser(
    id = id,
    username = username,
    firstName = firstName,
    lastName = lastName,
    email = email
)
