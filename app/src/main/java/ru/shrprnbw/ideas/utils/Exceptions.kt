package ru.shrprnbw.ideas.utils

data class OperationCancelledException(
    override val message: String? = "Operation was cancelled by user"
) : Exception(message)