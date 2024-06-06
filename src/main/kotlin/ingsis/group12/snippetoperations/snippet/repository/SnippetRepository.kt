package ingsis.group12.snippetoperations.snippet.repository

import ingsis.group12.snippetoperations.snippet.model.Snippet
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SnippetRepository : JpaRepository<Snippet, UUID>
