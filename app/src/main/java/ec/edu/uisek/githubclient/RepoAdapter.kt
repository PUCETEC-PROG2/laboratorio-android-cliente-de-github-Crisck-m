package ec.edu.uisek.githubclient

import ec.edu.uisek.githubclient.models.Repo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RepoAdapter(
    private var repoItems: List<Repo> = emptyList(),
    private val onEditClick: (Repo) -> Unit,
    private val onDeleteClick: (Repo) -> Unit
) : RecyclerView.Adapter<RepoAdapter.RepoViewHolder>() {

    inner class RepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val repoNameTextView: TextView = itemView.findViewById(R.id.repoNameTextView)
        val repoDescriptionTextView: TextView = itemView.findViewById(R.id.repoDescriptionTextView)
        val repoLanguageTextView: TextView = itemView.findViewById(R.id.repoLanguageTextView)
        val repoOwnerImage: ImageView = itemView.findViewById(R.id.repoOwnerImage)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_repo, parent, false)
        return RepoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val item = repoItems[position]
        holder.repoNameTextView.text = item.name
        
        holder.repoDescriptionTextView.text = if (item.description.isNullOrEmpty()) "No especificado" else item.description
        holder.repoLanguageTextView.text = if (item.language.isNullOrEmpty()) "No especificado" else item.language

        Glide.with(holder.itemView.context)
            .load(item.owner.avatarUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .circleCrop()
            .into(holder.repoOwnerImage)

        holder.btnEdit.setOnClickListener { onEditClick(item) }
        holder.btnDelete.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount(): Int = repoItems.size

    fun updateData(newItems: List<Repo>) {
        repoItems = newItems
        notifyDataSetChanged()
    }
}
