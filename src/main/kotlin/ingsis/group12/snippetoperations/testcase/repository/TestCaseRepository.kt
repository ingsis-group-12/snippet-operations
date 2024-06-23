package ingsis.group12.snippetoperations.testcase.repository

import ingsis.group12.snippetoperations.testcase.model.TestCase
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TestCaseRepository : JpaRepository<TestCase, UUID> {
    fun getTestCasesBySnippetId(snippetId: UUID): List<TestCase>
}
