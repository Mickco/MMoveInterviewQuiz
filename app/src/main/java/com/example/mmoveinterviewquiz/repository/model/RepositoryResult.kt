package com.example.mmoveinterviewquiz.repository.model

sealed class RepositoryResult<out DATA>{
    data class Success<out DATA>(val data: DATA): RepositoryResult<DATA>()
    data class Fail(val errorCode: ErrorCode, val errorMessage: ErrorMessage? = null): RepositoryResult<Nothing>()
}