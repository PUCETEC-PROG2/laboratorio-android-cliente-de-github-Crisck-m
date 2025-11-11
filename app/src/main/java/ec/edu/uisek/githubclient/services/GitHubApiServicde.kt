package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.modeis.Repo
import retrofit2.Call
import retrofit2.http.GET

interface GitHubApiServicde {
    @GET(value = "/user/repos")
    fun getRepos(): Call<List<Repo>>
}