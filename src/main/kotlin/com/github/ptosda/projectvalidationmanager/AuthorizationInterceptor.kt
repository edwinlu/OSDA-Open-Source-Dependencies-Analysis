package com.github.ptosda.projectvalidationmanager

import com.github.ptosda.projectvalidationmanager.database.repositories.TokenRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerMapping
import java.util.*

@Component
data class AuthorizationInterceptor (val tokenRepo : TokenRepository) : HandlerInterceptor{
    val logger : Logger = LoggerFactory.getLogger(AuthorizationInterceptor::class.java)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val pattern = Optional.ofNullable(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE))
                .orElse("[unknown]") as String
        logger.info("on preHandle for {}", pattern)

        val authorizationHeader = request.getHeader("Authorization")
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer")){
            response.status = 401
            return false
        }
        val token = authorizationHeader.split(" ")[1]
        if(tokenRepo.existsById(token)){
            return true
        }
        response.status = 401
        return false
    }
}