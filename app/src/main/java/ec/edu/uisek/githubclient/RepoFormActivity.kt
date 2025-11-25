package ec.edu.uisek.githubclient

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.models.RepoRequest
import ec.edu.uisek.githubclient.services.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RepoFormActivity : AppCompatActivity() {

    private lateinit var etRepoName: EditText
    private lateinit var etRepoDescription: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    // Reemplazar con un token válido (y seguro) de GitHub si se va a probar la escritura
    private val githubToken = "Bearer YOUR_GITHUB_TOKEN_HERE"
    private val githubUser = "Crisck-m" // Asegúrate de usar tu usuario correcto

    private var isEditMode = false
    private var repoOwner: String? = null
    private var repoName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repo_form)

        etRepoName = findViewById(R.id.etRepoName)
        etRepoDescription = findViewById(R.id.etRepoDescription)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        // Verificar si es edición
        if (intent.hasExtra("REPO_NAME")) {
            isEditMode = true
            repoName = intent.getStringExtra("REPO_NAME")
            repoOwner = intent.getStringExtra("REPO_OWNER")
            etRepoName.setText(repoName)
            etRepoDescription.setText(intent.getStringExtra("REPO_DESC"))
            // En edición, el nombre no debería cambiar porque es parte de la URL, o si cambia es complejo
            // GitHub permite renombrar, pero para simplificar podrías deshabilitarlo
            // etRepoName.isEnabled = false 
        }

        btnSave.setOnClickListener {
            val name = etRepoName.text.toString()
            val description = etRepoDescription.text.toString()

            if (name.isNotEmpty()) {
                val repoRequest = RepoRequest(name, description)
                if (isEditMode) {
                    updateRepo(repoRequest)
                } else {
                    createRepo(repoRequest)
                }
            } else {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun createRepo(repoRequest: RepoRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.service.createRepo(githubToken, repoRequest)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RepoFormActivity, "Repositorio creado", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@RepoFormActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RepoFormActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateRepo(repoRequest: RepoRequest) {
        if (repoOwner == null || repoName == null) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.service.updateRepo(githubToken, repoOwner!!, repoName!!, repoRequest)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RepoFormActivity, "Repositorio actualizado", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@RepoFormActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RepoFormActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
