package com.mobile.pomodoro.config;

import com.mobile.pomodoro.CustomException.UserNotFoundException;
import com.mobile.pomodoro.entities.User;
import com.mobile.pomodoro.services.impl.UserServiceImpl;
import com.mobile.pomodoro.utils.LogObj;
import jakarta.servlet.http.HttpFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends HttpFilter {

    private final UserServiceImpl userService;
    private final LogObj log = new LogObj("JwtAuthFilter");

//    @Qualifier("handlerExceptionResolver")
//    public void setResolver(HandlerExceptionResolver resolver) {
//        this.resolver = resolver;
//    }

    @Override
    public void doFilter(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException{
        // Log the request URL and method
        log.info("======================");
        log.info("Request URL: " + request.getRequestURL());
        log.info("Request Method: " + request.getMethod());
        log.info("Running through JwtAuthFilter");

        // Implement JWT authentication logic here
        // For example, extract the token from the request header and validate it
        // If valid, set the authentication in the security context
        // If invalid, send an error response

        String authHeader = request.getHeader("username");
        String username = null;

        try{
            if (authHeader != null) {
                username = authHeader;
            }
            else {
                log.warn("username" + username);
                throw new Exception("Null or invalid token");
            }

            User user = userService.getUserByUsername(username);

            log.info("User found: " + user);
            request.setAttribute("user", user);

        }
        catch (UserNotFoundException ex){
            log.error("User not found: " + ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }
        catch (Exception e){
            log.error("Error extracting username from header: " + e.getMessage());
        }

        // You can also add custom logic here, such as logging or modifying the request/response

        filterChain.doFilter(request, response);
    }
}
