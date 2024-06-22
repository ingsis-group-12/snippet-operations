package ingsis.group12.snippetoperations.testcase.model

import ingsis.group12.snippetoperations.asset.model.Snippet
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.util.UUID

@Entity
data class TestCase(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),
    @Column(name = "name", nullable = false)
    val name: String = "",
    @ManyToOne
    @JoinColumn(name = "snippet_id", nullable = false)
    val snippet: Snippet? = null,
    val inputs: String = "",
    val outputs: String = "",
    val environmentVariables: String = "",
)
