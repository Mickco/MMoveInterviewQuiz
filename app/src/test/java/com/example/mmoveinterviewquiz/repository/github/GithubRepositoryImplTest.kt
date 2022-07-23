package com.example.mmoveinterviewquiz.repository.github

import app.cash.turbine.test
import com.example.mmoveinterviewquiz.common.BaseUnitTest
import com.example.mmoveinterviewquiz.data.local.dao.FavoriteDao
import com.example.mmoveinterviewquiz.data.local.entity.Favorite
import com.example.mmoveinterviewquiz.data.network.model.File
import com.example.mmoveinterviewquiz.data.network.model.FileType
import com.example.mmoveinterviewquiz.data.network.model.GetGistsResponseItem
import com.example.mmoveinterviewquiz.data.network.model.Owner
import com.example.mmoveinterviewquiz.data.network.service.GithubApiService
import com.example.mmoveinterviewquiz.repository.model.ErrorCode
import com.example.mmoveinterviewquiz.repository.model.Gist
import com.example.mmoveinterviewquiz.repository.model.RepositoryResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.UnknownHostException

@ExperimentalCoroutinesApi
class GithubRepositoryImplTest : BaseUnitTest() {

    val csvFile = File(
        filename = "csvfilename",
        language = "ab",
        raw_url = "s",
        size = 123,
        type = FileType.CSV
    )

    val plainTextFile = File(
        filename = "csvfilename",
        language = "ab",
        raw_url = "s",
        size = 123,
        type = FileType.Plain
    )

    val getGistsResponse = GetGistsResponseItem(
            comments = 0,
            comments_url = "https://api.github.com/gists/59ababeb48aeae7c891192a529c883e4/comments",
            commits_url = "https://api.github.com/gists/59ababeb48aeae7c891192a529c883e4/commits",
            created_at = "2022-07-23T06:58:33Z",
            description = "",
            forks_url = "https://api.github.com/gists/59ababeb48aeae7c891192a529c883e4/forks",
            git_pull_url = "https://gist.github.com/59ababeb48aeae7c891192a529c883e4.git",
            git_push_url = "https://gist.github.com/59ababeb48aeae7c891192a529c883e4.git",
            html_url = "https://gist.github.com/59ababeb48aeae7c891192a529c883e4",
            id = "59ababeb48aeae7c891192a529c883e4",
            node_id = "G_kwDOBkxuYdoAIDU5YWJhYmViNDhhZWFlN2M4OTExOTJhNTI5Yzg4M2U0",
            owner = Owner(
                avatar_url = "https://avatars.githubusercontent.com/u/105672289?v=4",
                events_url = "https://api.github.com/users/DanielHerzog100/events{/privacy}",
                followers_url = "https://api.github.com/users/DanielHerzog100/followers",
                following_url = "https://api.github.com/users/DanielHerzog100/following{/other_user}",
                gists_url = "https://api.github.com/users/DanielHerzog100/gists{/gist_id}",
                gravatar_id = "",
                html_url = "https://github.com/DanielHerzog100",
                id = 105672289,
                login = "DanielHerzog100",
                node_id = "U_kgDOBkxuYQ",
                organizations_url = "https://api.github.com/users/DanielHerzog100/orgs",
                received_events_url = "https://api.github.com/users/DanielHerzog100/received_events",
                repos_url = "https://api.github.com/users/DanielHerzog100/repos",
                site_admin = false,
                starred_url = "https://api.github.com/users/DanielHerzog100/starred{/owner}{/repo}",
                subscriptions_url = "https://api.github.com/users/DanielHerzog100/subscriptions",
                type = "User",
                url = "https://api.github.com/users/DanielHerzog100)"
            ),
            public = true,
            truncated = false,
            updated_at = "2022-07-23T06:58:33Z",
            url = "https://api.github.com/gists/59ababeb48aeae7c891192a529c883e4",
            files = mapOf("123" to plainTextFile)
    )



    @RelaxedMockK
    lateinit var service: GithubApiService

    @RelaxedMockK
    lateinit var favoriteDao: FavoriteDao



    lateinit var repository: GithubRepository

    override fun setup() {
        super.setup()

        repository = GithubRepositoryImpl(service, favoriteDao)
    }


    @Test
    fun fetchGistsAsync_nonCsv() = runTest {
        coEvery { service.getGists() } returns listOf(getGistsResponse)
        val result = repository.fetchGistsAsync(this).await()

        val expected = RepositoryResult.Success(listOf(Gist(id = getGistsResponse.id, url = getGistsResponse.url, username = getGistsResponse.owner.login, csvFilename = null)))
        assertEquals(expected, result)
    }

    @Test
    fun fetchGistsAsync_csvFile() = runTest {
        coEvery { service.getGists() } returns listOf(getGistsResponse.copy(files = mapOf("asdf" to csvFile)))
        val result = repository.fetchGistsAsync(this).await()

        val expected = RepositoryResult.Success(listOf(Gist(id = getGistsResponse.id, url = getGistsResponse.url, username = getGistsResponse.owner.login, csvFilename = csvFile.filename)))
        assertEquals(expected, result)
    }

    @Test
    fun fetchUserGistsAsync_noncsvFile() = runTest{
        val username = "username123"
        coEvery { service.getUserGists(username) } returns listOf(getGistsResponse)
        val result = repository.fetchUserGistsAsync(this, username).await()

        val expected = RepositoryResult.Success(listOf(Gist(id = getGistsResponse.id, url = getGistsResponse.url, username = getGistsResponse.owner.login, csvFilename = null)))
        assertEquals(expected, result)
    }

    @Test
    fun fetchUserGistsAsync_csvFile() = runTest{
        val username = "username123"
        coEvery { service.getUserGists(username) } returns listOf(getGistsResponse.copy(files = mapOf("asdf" to csvFile)))
        val result = repository.fetchUserGistsAsync(this, username).await()

        val expected = RepositoryResult.Success(listOf(Gist(id = getGistsResponse.id, url = getGistsResponse.url, username = getGistsResponse.owner.login, csvFilename = csvFile.filename)))
        assertEquals(expected, result)
    }

    @Test
    fun addFavoriteAsync()  = runTest{
        val gistID = "gistID123"

        val result = repository.addFavoriteAsync(this, gistID).await()
        assertEquals(RepositoryResult.Success(Unit),result)

        coVerify {
            favoriteDao.insert(Favorite(gistID))
        }
    }

    @Test
    fun deleteFavoriteAsync()  = runTest{
        val gistID = "gistID123"

        val result = repository.deleteFavoriteAsync(this, gistID).await()

        assertEquals(RepositoryResult.Success(Unit),result)

        coVerify {
            favoriteDao.delete(Favorite(gistID))
        }
    }

    @Test
    fun favoriteListFlow() = runTest {
        val fakeFavListFlow = MutableSharedFlow<List<Favorite>>(replay = 0)

        coEvery { favoriteDao.getAll() } returns fakeFavListFlow

        val gistID = "gistID123"


        repository.favoriteListFlow.test {
            fakeFavListFlow.emit(listOf(Favorite(gistID)))
            assertEquals(RepositoryResult.Success(listOf(gistID)), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun favoriteListFlow_fail() = runTest {
        val fakeFavListFlow = flow<List<Favorite>> {
            throw  UnknownHostException()
        }
        coEvery { favoriteDao.getAll() } returns fakeFavListFlow

        repository.favoriteListFlow.test {
            assertEquals(RepositoryResult.Fail(errorCode = ErrorCode.ConnectionError), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

}