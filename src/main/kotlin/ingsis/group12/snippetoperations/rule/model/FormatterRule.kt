package ingsis.group12.snippetoperations.rule.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "formatter_rule")
data class FormatterRule(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID? = null,
    @Column(name = "user_id", nullable = false)
    val userId: String? = null,
)
