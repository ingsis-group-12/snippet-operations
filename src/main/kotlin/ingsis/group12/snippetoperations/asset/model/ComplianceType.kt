package ingsis.group12.snippetoperations.asset.model

import com.fasterxml.jackson.annotation.JsonValue

enum class ComplianceType(val value: String) {
    PENDING("pending"),
    FAILED("failed"),
    NOT_COMPLIANT("not-compliant"),
    COMPLIANT("compliant"),
    ;

    @JsonValue
    fun toValue(): String {
        return this.value
    }
}
