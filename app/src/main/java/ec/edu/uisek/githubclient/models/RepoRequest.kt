package ec.edu.uisek.githubclient.models

data class RepoRequest(
    val name: String,
    val description: String?,
    val private: Boolean = false
)
