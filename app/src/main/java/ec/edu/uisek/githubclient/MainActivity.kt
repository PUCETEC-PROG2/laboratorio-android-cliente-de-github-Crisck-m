package ec.edu.uisek.githubclient

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.services.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: RepoAdapter

    private val githubUser = "google"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecycleView()

        // Llamada a la API
        fetchRepositories()
    }

    private fun setupRecycleView() {
        reposAdapter = RepoAdapter()
        binding.repoRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = reposAdapter
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
                            Toast.makeText(this@MainActivity, "Cargados ${repos.size} repositorios de $githubUser", Toast.LENGTH_SHORT).show()
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