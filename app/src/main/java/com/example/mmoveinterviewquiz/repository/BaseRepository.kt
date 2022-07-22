package com.example.mmoveinterviewquiz.repository

import com.example.mmoveinterviewquiz.repository.model.ErrorCode
import com.example.mmoveinterviewquiz.repository.model.ErrorMessage
import com.example.mmoveinterviewquiz.repository.model.RepositoryResult
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

abstract class BaseRepository {
    suspend fun <T> executeApiCall(apiCall: suspend () -> T ): RepositoryResult<T> {
        return try {
            RepositoryResult.Success(apiCall())
        }catch (e: HttpException) {
            val res = e.response()
            RepositoryResult.Fail(
                ErrorCode.HTTPError,
                ErrorMessage(
                    code = res?.code() ?: DEFAULT_ERROR_MESSAGE_CODE,
                    message = res?.message().orEmpty()
                )
            )
        }catch (e: UnknownHostException) {
            RepositoryResult.Fail(ErrorCode.ConnectionError)
        }catch (e: IOException) {
            RepositoryResult.Fail(ErrorCode.IOError)
        }catch (e: Throwable) {
            RepositoryResult.Fail(ErrorCode.UnknownError)
        }
    }



    companion object {
        const val DEFAULT_ERROR_MESSAGE_CODE = -1
    }
}



