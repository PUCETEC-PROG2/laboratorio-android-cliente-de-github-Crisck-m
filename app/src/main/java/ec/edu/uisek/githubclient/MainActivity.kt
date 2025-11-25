package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: RepoAdapter

    // CAMBIAR POR EL USUARIO CORRECTO PARA VER LOS CAMBIOS
    private val githubUser = "Crisck-m"
    // CAMBIAR POR UN TOKEN VÁLIDO PARA DELETE
    private val githubToken = "Bearer YOUR_GITHUB_TOKEN_HERE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecycleView()

        binding.fab.setOnClickListener {
            val intent = Intent(this, RepoFormActivity::class.java)
            startActivity(intent)
        }

        // Llamada a la API
        fetchRepositories()
    }

    override fun onResume() {
        super.onResume()
        // Refrescar la lista al volver de la actividad de formulario
        fetchRepositories()
    }

    private fun setupRecycleView() {
        reposAdapter = RepoAdapter(
            onEditClick = { repo ->
                val intent = Intent(this, RepoFormActivity::class.java).apply {
                    putExtra("REPO_NAME", repo.name)
                    putExtra("REPO_DESC", repo.description)
                    putExtra("REPO_OWNER", repo.owner.login) // Asumimos que RepoOwner tiene login
                }
                startActivity(intent)
            },
            onDeleteClick = { repo ->
                confirmDelete(repo)
            }
        )
        binding.repoRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = reposAdapter
        }
    }

    private fun confirmDelete(repo: Repo) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Repositorio")
            .setMessage("¿Estás seguro de eliminar ${repo.name}?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteRepo(repo)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteRepo(repo: Repo) {
         // RepoOwner necesita tener el campo 'login' para saber el dueño
         // O usar githubUser si siempre es el mismo usuario logueado
         val owner = repo.owner.login 

         CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.service.deleteRepo(githubToken, owner, repo.name)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MainActivity, "Repositorio eliminado", Toast.LENGTH_SHORT).show()
                        fetchRepositories()
                    } else {
                        Toast.makeText(this@MainActivity, "Error al eliminar: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchRepositories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Llamada a la API
                val response = RetrofitClient.service.listRepos(githubUser)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val repos = response.body()!!
                        if (repos.isEmpty()) {
                            Toast.makeText(this@MainActivity, "El usuario no tiene repositorios públicos", Toast.LENGTH_LONG).show()
                        } else {
                            // Actualizamos la lista
                            reposAdapter.updateData(repos)
                            
                            // Mensaje opcional para confirmar de quién son los datos
                            // Toast.makeText(this@MainActivity, "Cargados ${repos.size} repositorios", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Error ${response.code()}: No se pudo obtener la lista",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Fallo de conexión: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
