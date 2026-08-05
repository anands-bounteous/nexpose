package com.rapid7.nexpose.nsc.service;

import com.rapid7.nexpose.console.auth.AuthResult;
import com.rapid7.nexpose.console.auth.LdapAuthenticator;
import com.rapid7.nexpose.console.auth.LocalAuthenticator;
import com.rapid7.nexpose.console.domain.User;
import com.rapid7.nexpose.console.exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Front door for authentication. Local users (the built-in {@code nxadmin}) are
 * checked first; anything else is treated as a directory user and delegated to
 * the {@link LdapAuthenticator} — which is where config defect NEX-3108 surfaces.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final LocalAuthenticator localAuthenticator;
    private final LdapAuthenticator ldapAuthenticator;

    public AuthService(LocalAuthenticator localAuthenticator, LdapAuthenticator ldapAuthenticator) {
        this.localAuthenticator = localAuthenticator;
        this.ldapAuthenticator = ldapAuthenticator;
    }

    public User login(String username, String password) {
        log.info("Authentication attempt for '{}'", username);
        if (username == null || username.isBlank()) {
            throw new AuthenticationException("Username is required");
        }
        if (localAuthenticator.supports(username)) {
            AuthResult result = localAuthenticator.authenticate(username, password);
            if (!result.isSuccess()) {
                throw new AuthenticationException(result.getMessage());
            }
            return result.getUser();
        }
        // Directory user -> LDAP (NEX-3108: misconfigured host/base DN -> LdapConnectionException).
        AuthResult result = ldapAuthenticator.authenticate(username, password);
        if (!result.isSuccess()) {
            throw new AuthenticationException(result.getMessage());
        }
        return result.getUser();
    }
}
