package org.openntf.todo.todo.cloudant.provider;

import com.google.common.base.Optional;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class TokenAuthenticationProvider implements AuthenticationProvider {

  public TokenAuthenticationProvider() {

  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    Optional<String> token = (Optional) authentication.getPrincipal();
    if (!token.isPresent() || token.get().isEmpty()) {
      throw new BadCredentialsException("Invalid token");
    }
    return authentication;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(PreAuthenticatedAuthenticationToken.class);
  }
}
