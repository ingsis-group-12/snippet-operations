package ingsis.group12.snippetoperations.snippet.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "snippet")
data class Snippet(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    val id: String? = null,
    @Column(name = "name", nullable = false)
    val name: String? = null,
    @Column(name = "language", nullable = false)
    val language: String? = null,
    @Column(name = "extension", nullable = false)
    val extension: String? = null,
    @Column(name = "createdAt", nullable = false, updatable = false)
    val createdAt: Date? = Date(),
    @Column(name = "updatedAt", nullable = true)
    val updatedAt: Date? = null,
)
