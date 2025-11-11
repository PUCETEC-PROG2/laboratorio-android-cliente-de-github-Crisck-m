package ec.edu.uisek.githubclient


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoItemBinding
import ec.edu.uisek.githubclient.modeis.Repo
import java.io.File

private val Any.context: Context
private val FragmentRepoItemBinding.root: Any
private val FragmentRepoItemBinding.repoLang: Any
private val FragmentRepoItemBinding.repoDescription: Any
private var Any.text: String
private val FragmentRepoItemBinding.repoName: Any
private val FragmentRepoItemBinding.repoOwnerImage: Any
private val File.context: Context

// 1. Clase ViewHolder: Contiene las referencias a las vistas de un solo ítem.
// Usa la clase de ViewBinding generada para fragment_repo_item.xml.
class RepoViewHolder(private val binding: FragmentRepoItemBinding) : RecyclerView.ViewHolder(binding.root) {

    // 2. Función para vincular datos a las vistas del ítem.
    // Por ahora, usaremos datos de ejemplo.
    fun bind(repo: Repo) {
        binding.repoName.text = repo.name
        binding.repoDescription.text = repo.description ?: "El repositorio no tiene descripción"
        binding.repoLang.text = repo.language ?:"Lenguaje no especificado"
        Glide.with(context = binding.root.context)
            .load(string = repo.owner.avatarUrl)
            .placeholder(resourceld = R.mipmap.ic_launcher)
            .error(resourceld = R.mipmap.ic_launcher)
            .circleCrop()
            .into(view= binding.repoOwnerImage)
    }
}

// 3. Clase Adapter: Gestiona la creación y actualización de los ViewHolders.
class ReposAdapter : RecyclerView.Adapter<RepoViewHolder>() {

    private var repositories : List<Repo> = emptyList()
    override fun getItemCount(): Int = repositories.size

    // Por ahora, simplemente le diremos que muestre 3 ítems.
    override fun getItemCount(): Int = 3

    // Se llama para crear un nuevo ViewHolder cuando el RecyclerView lo necesita.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        // Infla la vista del ítem usando ViewBinding
        val binding = FragmentRepoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RepoViewHolder(binding)
    }

    // Se llama para vincular los datos a un ViewHolder en una posición específica.
    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(repositories[position])
    }
}

fun updateRepositories(newRepos: List<Repo>){
    repositories = newRepos
    notifyDataSetChanged()
}