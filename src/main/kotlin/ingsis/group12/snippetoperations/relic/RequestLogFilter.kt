package ingsis.group12.snippetoperations.relic

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
@Order(2)
class RequestLogFilter : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val uri = request.requestURI
        val method = request.method
        val prefix = "$method $uri"
        try {
            filterChain.doFilter(request, response)
        } finally {
            val statusCode = response.status
            logger.info("$prefix - $statusCode")
        }
    }
}
