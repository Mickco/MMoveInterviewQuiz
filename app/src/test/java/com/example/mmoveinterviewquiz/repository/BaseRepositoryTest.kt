package com.example.mmoveinterviewquiz.repository

import app.cash.turbine.test
import com.example.mmoveinterviewquiz.common.BaseUnitTest
import com.example.mmoveinterviewquiz.repository.model.ErrorCode
import com.example.mmoveinterviewquiz.repository.model.ErrorMessage
import com.example.mmoveinterviewquiz.repository.model.RepositoryResult
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.UnknownHostException

@ExperimentalCoroutinesApi
class BaseRepositoryTest : BaseUnitTest() {


    val repository = object : BaseRepository() {}

    @Test
    fun executeAsyncCall_success() = runTest {
        val result = repository.executeAsyncCall(this) {
            "abc"
        }.await()

        assertEquals(RepositoryResult.Success("abc"), result )
    }

    @Test
    fun executeAsyncCall_httpException() = runTest {
        val e = HttpException(Response.error<ResponseBody>(500 ,ResponseBody.create("plain/text".toMediaTypeOrNull(),"some content")))
        val result = repository.executeAsyncCall(this) {
            throw e
        }.await()

        assertEquals(RepositoryResult.Fail(ErrorCode.HTTPError, ErrorMessage(500, e.response()?.message().orEmpty())), result )

    }

    @Test
    fun executeAsyncCall_UnknownhostException() = runTest {

        val result = repository.executeAsyncCall(this) {
            throw UnknownHostException()
        }.await()

        assertEquals(RepositoryResult.Fail(ErrorCode.ConnectionError), result )
    }

    @Test
    fun executeAsyncCall_IOException() = runTest {
        val result = repository.executeAsyncCall(this) {
            throw IOException()
        }.await()

        assertEquals(RepositoryResult.Fail(ErrorCode.IOError), result )

    }

    @Test
    fun executeAsyncCall_Throwable() = runTest {
        val result = repository.executeAsyncCall(this) {
            throw Throwable()
        }.await()

        assertEquals(RepositoryResult.Fail(ErrorCode.UnknownError), result )

    }

    @Test
    fun test123() = runTest {
        flow<String> { throw RuntimeException("broken!") }
            .catch { e ->
                emit("123")
            }.test {
            assertEquals("broken!", awaitItem())
        }
    }

}