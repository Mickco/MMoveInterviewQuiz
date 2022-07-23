package com.example.mmoveinterviewquiz

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

        val list = listOf(1,2,3,4,3,5)
        val result = list.distinct().chunked(2)
        assertEquals(4, result)
    }
}