package ingsis.group12.snippetoperations.snippet.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "snippet")
data class Snippet(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    var id: String? = null,
    @Column(name = "name", nullable = false)
    var name: String = "",
    @Column(name = "content", nullable = false)
    var content: String = "",
    @Column(name = "language", nullable = false)
    var language: String = "",
    @Column(name = "createdAt", nullable = false)
    var createdAt: Date? = null,
    @Column(name = "updatedAt", nullable = true)
    var updatedAt: Date? = null,
)
