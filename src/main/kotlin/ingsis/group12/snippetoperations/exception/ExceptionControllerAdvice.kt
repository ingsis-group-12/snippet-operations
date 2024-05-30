package ingsis.group12.snippetoperations.exception

import ingsis.group12.snippetoperations.model.ErrorMessage
import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
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
}
