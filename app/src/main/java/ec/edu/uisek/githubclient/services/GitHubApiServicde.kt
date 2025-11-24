package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.models.Repo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubApiService {
    @GET("users/{user}/repos")
    // El método debe ser 'suspend' porque lo llamaremos desde Coroutines
    suspend fun listRepos(@Path("user") user: String): Response<List<Repo>>
}