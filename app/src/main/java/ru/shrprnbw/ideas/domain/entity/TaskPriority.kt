package ru.shrprnbw.ideas.domain.entity

enum class TaskPriority {
    LOW, MEDIUM, HIGH, CRITICAL;

    companion object {
        fun fromString(value: String): TaskPriority {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: MEDIUM
        }
    }
}
