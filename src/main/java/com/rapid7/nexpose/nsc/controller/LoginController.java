package com.rapid7.nexpose.nsc.controller;

import com.rapid7.nexpose.console.domain.User;
import com.rapid7.nexpose.console.exception.AuthenticationException;
import com.rapid7.nexpose.console.exception.LdapConnectionException;
import com.rapid7.nexpose.nsc.config.AuthInterceptor;
import com.rapid7.nexpose.nsc.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Login / logout. Directory users exercise the LDAP path (NEX-3108). */
@Controller
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("productName", "Nexpose Security Console");
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpServletRequest request,
                          Model model) {
        try {
            User user = authService.login(username, password);
            HttpSession session = request.getSession(true);
            session.setAttribute(AuthInterceptor.SESSION_USER, user);
            log.info("User '{}' logged in", user.getUsername());
            return "redirect:/dashboard";
        } catch (LdapConnectionException e) {
            // NEX-3108: LDAP misconfiguration -> directory login cannot complete.
            log.error("LDAP login failed for '{}': {}", username, e.getMessage(), e);
            model.addAttribute("error", "Directory login is unavailable: " + e.getMessage());
            model.addAttribute("errorCode", e.getErrorCode());
            return "login";
        } catch (AuthenticationException e) {
            log.warn("Login rejected for '{}': {}", username, e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }
}
