package ingsis.group12.snippetoperations.exception

class SnippetCreationError(override val message: String) : Exception()

class SnippetNotFoundError(override val message: String) : Exception()

class SnippetDeleteError(override val message: String) : Exception()

class SnippetShareError(override val message: String) : Exception()

class SnippetPermissionError(override val message: String) : Exception()

class SnippetRuleError(override val message: String) : Exception()
