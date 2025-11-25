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
        recyclerView = view.findViewById(R.id.repositoriesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 2. Inicializar adaptador y asignarlo
        // Se pasan lambdas vacías o con mensajes simples ya que la lógica principal está en MainActivity
        adapter = RepoAdapter(
            repoItems = emptyList(),
            onEditClick = { Toast.makeText(context, "Editar desde Fragment no implementado", Toast.LENGTH_SHORT).show() },
            onDeleteClick = { Toast.makeText(context, "Eliminar desde Fragment no implementado", Toast.LENGTH_SHORT).show() }
        )
        recyclerView.adapter = adapter

        // 3. Obtener datos
        fetchRepositories()

        return view
    }

    private fun fetchRepositories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.service.listRepos("google")

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        adapter.updateData(response.body()!!)
                    } else {
                        // Verificar si context es nulo antes de usarlo
                        context?.let {
                            Toast.makeText(it, "Error ${response.code()}: ${response.message()}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    context?.let {
                        Toast.makeText(it, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
