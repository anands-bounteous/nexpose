package com.rapid7.nexpose.nsc.config;

import com.rapid7.nexpose.console.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Redirects unauthenticated requests to the login page. Session attribute
 * {@code CURRENT_USER} holds the logged-in {@link User}.
 */
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);
    public static final String SESSION_USER = "CURRENT_USER";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        boolean authenticated = session != null && session.getAttribute(SESSION_USER) != null;
        if (!authenticated) {
            log.debug("Unauthenticated request to {} - redirecting to /login", request.getRequestURI());
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }
}
