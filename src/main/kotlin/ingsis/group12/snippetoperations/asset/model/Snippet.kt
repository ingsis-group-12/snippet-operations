package ingsis.group12.snippetoperations.asset.model

import ingsis.group12.snippetoperations.testcase.model.TestCase
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "snippet")
data class Snippet(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID? = null,
    @Column(name = "name", nullable = false)
    val name: String? = null,
    @Column(name = "language", nullable = false)
    val language: String? = null,
    @Column(name = "extension", nullable = false)
    val extension: String? = null,
    @OneToMany(mappedBy = "snippet", orphanRemoval = true)
    val testCases: List<TestCase> = emptyList(),
    @Column(name = "createdAt", nullable = false, updatable = false)
    val createdAt: Date? = Date(),
    @Column(name = "updatedAt", nullable = true)
    var updatedAt: Date? = null,
)
