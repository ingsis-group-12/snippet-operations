package ingsis.group12.snippetoperations.rule.controller

import ingsis.group12.snippetoperations.rule.dto.FormatterRules
import ingsis.group12.snippetoperations.rule.dto.LinterRuleInput
import ingsis.group12.snippetoperations.rule.dto.RunRuleDTO
import ingsis.group12.snippetoperations.rule.service.FormatterRuleService
import ingsis.group12.snippetoperations.rule.service.LinterRuleService
import ingsis.group12.snippetoperations.runner.output.FormatterOutput
import ingsis.group12.snippetoperations.runner.output.LinterOutput
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rule")
@Tag(name = "Rule")
@CrossOrigin("*")
class RuleController(
    private val formatterRuleService: FormatterRuleService,
    private val linterRuleService: LinterRuleService,
) : RuleControllerSpec {
    @PostMapping("/linter")
    override fun createOrGetLinterRules(jwt: Jwt): ResponseEntity<LinterRuleInput> {
        return ResponseEntity.ok(linterRuleService.createOrGetRules(jwt.subject))
    }

    @PutMapping("/linter")
    override suspend fun updateLinterRules(
        jwt: Jwt,
        linterRuleInput: LinterRuleInput,
    ): ResponseEntity<LinterRuleInput> {
        return ResponseEntity.ok(linterRuleService.updateRules(jwt.subject, linterRuleInput))
    }

    @PostMapping("/formatter")
    override fun createOrGetFormatterRules(jwt: Jwt): ResponseEntity<FormatterRules> {
        return ResponseEntity.ok(formatterRuleService.createOrGetRules(jwt.subject))
    }

    @PutMapping("/formatter")
    override suspend fun updateFormatterRules(
        jwt: Jwt,
        formatterRules: FormatterRules,
    ): ResponseEntity<FormatterRules> {
        return ResponseEntity.ok(formatterRuleService.updateRules(jwt.subject, formatterRules))
    }

    @PostMapping("/linter/run")
    override fun runLinterRules(
        jwt: Jwt,
        runRuleDTO: RunRuleDTO,
    ): ResponseEntity<LinterOutput> {
        return ResponseEntity.ok(linterRuleService.runRules(jwt.subject, runRuleDTO))
    }

    @PostMapping("/formatter/run")
    override fun runFormatterRules(
        jwt: Jwt,
        runRuleDTO: RunRuleDTO,
    ): ResponseEntity<FormatterOutput> {
        return ResponseEntity.ok(formatterRuleService.runRules(jwt.subject, runRuleDTO))
    }
}
