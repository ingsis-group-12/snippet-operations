package ingsis.group12.snippetoperations.rule.repository

import ingsis.group12.snippetoperations.rule.model.LinterRule
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface LinterRuleRepository : JpaRepository<LinterRule, UUID> {
    fun findByUserId(userId: String): Optional<LinterRule>
}
