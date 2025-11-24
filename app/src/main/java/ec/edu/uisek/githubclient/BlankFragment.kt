package ec.edu.uisek.githubclient

import ec.edu.uisek.githubclient.services.RetrofitClient
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BlankFragment : Fragment() {

    private lateinit var adapter: RepoAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_blank, container, false)

        // 1. Inicializar vistas
        // Asegúrate de que 'repositoriesRecyclerView' sea el ID en tu fragment_blank.xml
        recyclerView = view.findViewById(R.id.repositoriesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 2. Inicializar adaptador y asignarlo
        adapter = RepoAdapter(emptyList())
        recyclerView.adapter = adapter

        // 3. Obtener datos
        fetchRepositories()

        return view
    }

    private fun fetchRepositories() {
        // Ejecutar la llamada de red en un hilo I/O (secundario)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Llama al servicio a través del cliente
                // Cambia "google" por el usuario que necesites
                val response = RetrofitClient.service.listRepos("google")

                // Volver al hilo principal para actualizar la UI
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        adapter.updateData(response.body()!!)
                    } else {
                        Toast.makeText(context, "Error ${response.code()}: ${response.message()}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                // Manejar errores de conexión
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // ... (El resto de tu código, como el companion object) ...
}