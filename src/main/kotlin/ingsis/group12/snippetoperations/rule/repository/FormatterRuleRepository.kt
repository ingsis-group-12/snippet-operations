package ingsis.group12.snippetoperations.rule.repository

import ingsis.group12.snippetoperations.rule.model.FormatterRule
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface FormatterRuleRepository : JpaRepository<FormatterRule, UUID> {
    fun findByUserId(userId: String): Optional<FormatterRule>
}
