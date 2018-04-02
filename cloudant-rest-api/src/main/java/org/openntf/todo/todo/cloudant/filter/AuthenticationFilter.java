package org.openntf.todo.todo.cloudant.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@Slf4j
public class AuthenticationFilter extends GenericFilterBean {

  @Value("${api.token}")
  private String apiToken;

  @Value("${api.key}")
  private String apiKey;

  @Value("${api.user}")
  private String userName;

  private AuthenticationManager authenticationManager;

  public AuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpRequest = asHttp(request);
    HttpServletResponse httpResponse = asHttp(response);

    Optional<String> token = Optional.fromNullable(httpRequest.getHeader("X-TODO-API-KEY"));
    Optional<String> username = Optional.fromNullable(httpRequest.getHeader("X-TODO-USER-KEY"));

    String resourcePath = new UrlPathHelper().getPathWithinApplication(httpRequest);

    try {
      if(resourcePath.contains("/v1")) {
        if (!token.isPresent()) {
          SecurityContextHolder.clearContext();
          httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
          if (token.get().equals("i49chtnbea5h1dfolcqoh2qght")) {
            log.debug("Trying to authenticate user by X-TODO-API-KEY method. Token: {}", token);
            processTokenAuthentication(token, username);
            log.debug("AuthenticationFilter is passing request down the filter chain");
          } else {
            SecurityContextHolder.clearContext();
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
          }
        }
      }

      chain.doFilter(request, response);
    } catch (InternalAuthenticationServiceException internalAuthenticationServiceException) {
      SecurityContextHolder.clearContext();
      log.error("Internal authentication service exception", internalAuthenticationServiceException);
      httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } catch (AuthenticationException authenticationException) {
      SecurityContextHolder.clearContext();
      httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } finally {

    }
  }

  private HttpServletRequest asHttp(ServletRequest request) {
    return (HttpServletRequest) request;
  }

  private HttpServletResponse asHttp(ServletResponse response) {
    return (HttpServletResponse) response;
  }

  private void processTokenAuthentication(Optional<String> token, Optional<String> username) {
    Authentication resultOfAuthentication = tryToAuthenticateWithToken(token, username);
    SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
  }

  private Authentication tryToAuthenticateWithToken(Optional<String> token, Optional<String> username) {
    PreAuthenticatedAuthenticationToken requestAuthentication = new PreAuthenticatedAuthenticationToken(username, token);
    return tryToAuthenticate(requestAuthentication);
  }

  private Authentication tryToAuthenticate(Authentication requestAuthentication) {
    Authentication responseAuthentication = authenticationManager.authenticate(requestAuthentication);
    responseAuthentication.setAuthenticated(true);
    if (responseAuthentication == null || !responseAuthentication.isAuthenticated()) {
      throw new InternalAuthenticationServiceException("Unable to authenticate User for provided token");
    }
    log.debug("User successfully authenticated");
    return responseAuthentication;
  }

}
