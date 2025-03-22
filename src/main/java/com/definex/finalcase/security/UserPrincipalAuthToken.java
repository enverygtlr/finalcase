package com.definex.finalcase.security;

import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@EqualsAndHashCode(callSuper = false)
public class UserPrincipalAuthToken extends AbstractAuthenticationToken {
    private final UserPrincipal principal;

    public UserPrincipalAuthToken(UserPrincipal principal) {
        super(principal.getAuthorities());
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public UserPrincipal getPrincipal() {
        return this.principal;
    }
}
