package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import retrofit2.Response
import retrofit2.http.*

interface GitHubApiService {
    @GET("users/{user}/repos")
    suspend fun listRepos(@Path("user") user: String): Response<List<Repo>>

    @POST("user/repos")
    suspend fun createRepo(
        @Header("Authorization") token: String,
        @Body repo: RepoRequest
    ): Response<Repo>

    @PATCH("repos/{owner}/{repo}")
    suspend fun updateRepo(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body body: RepoRequest
    ): Response<Repo>

    @DELETE("repos/{owner}/{repo}")
    suspend fun deleteRepo(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<Unit>
}
