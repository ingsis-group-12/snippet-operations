package ingsis.group12.snippetoperations.exception

import ingsis.group12.snippetoperations.util.ErrorMessage
import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.HttpClientErrorException
import java.util.stream.Collectors

@ControllerAdvice
class ExceptionControllerAdvice {
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(exception: HttpMessageNotReadableException): ResponseEntity<ErrorMessage> {
        val error = ErrorMessage("Request body is empty", HttpStatus.BAD_REQUEST.value())
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<MutableMap<String, MutableList<String?>?>> {
        val body: MutableMap<String, MutableList<String?>?> = HashMap()

        val errors: MutableList<String?>? =
            ex.bindingResult
                .fieldErrors
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList())

        body["errors"] = errors

        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(SnippetCreationError::class)
    fun handleSnippetCreationError(exception: SnippetCreationError): ResponseEntity<ErrorMessage> {
        val error = ErrorMessage(exception.message, HttpStatus.CONFLICT.value())
        return ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(SnippetNotFoundError::class)
    fun handleSnippetNotFoundError(exception: SnippetNotFoundError): ResponseEntity<ErrorMessage> {
        val error = ErrorMessage(exception.message, HttpStatus.NOT_FOUND.value())
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(SnippetDeleteError::class)
    fun handleSnippetDeleteError(exception: SnippetDeleteError): ResponseEntity<ErrorMessage> {
        val error = ErrorMessage(exception.message, HttpStatus.CONFLICT.value())
        return ResponseEntity(error, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(HttpClientErrorException.NotFound::class)
    fun handleHttpClientErrorExceptionNotFound(exception: HttpClientErrorException.NotFound): ResponseEntity<ErrorMessage> {
        val responseBody = exception.responseBodyAsString
        val messageRegex = "\"message\":\\s*\"(.*?)\"".toRegex()
        val matchResult = messageRegex.find(responseBody)
        val error = ErrorMessage(removeUselessCharacters(matchResult), HttpStatus.NOT_FOUND.value())
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }

    private fun removeUselessCharacters(matchResult: MatchResult?) =
        matchResult!!.value.replace("\\\"", "").replace("\"message\":\"", "").replace("\"", "")
}
