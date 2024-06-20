package ingsis.group12.snippetoperations.asset.repository

import ingsis.group12.snippetoperations.asset.model.Snippet
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SnippetRepository : JpaRepository<Snippet, UUID>
