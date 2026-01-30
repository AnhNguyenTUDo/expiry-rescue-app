package practice.expiry_rescue_app.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        // Log the failure
        log.error("OAuth2 authentication failed: {}", exception.getMessage());

        // Determine error message
        String errorMessage = getErrorMessage(exception);

        // Build redirect URL with error parameter
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/auth/callback")
                .queryParam("error", errorMessage)
                .build()
                .toUriString();

        // Clear any authentication cookies if they exist
        clearAuthenticationAttributes(request, response);

        // Redirect to frontend with error
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * Determines appropriate error message based on exception type
     */
    private String getErrorMessage(AuthenticationException exception) {
        String message = exception.getMessage();

        // Common OAuth2 errors
        if (message.contains("access_denied")) {
            return "access_denied";
        } else if (message.contains("invalid_token")) {
            return "invalid_token";
        } else if (message.contains("unauthorized_client")) {
            return "unauthorized_client";
        } else if (message.contains("unsupported_provider")) {
            return "unsupported_provider";
        } else if (message.contains("email_not_found")) {
            return "email_not_found";
        } else if (message.contains("server_error")) {
            return "server_error";
        }

        // Default error
        return "authentication_failed";
    }

    /**
     * Clear authentication-related cookies and attributes
     */
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // Clear OAuth2-related cookies
                if (cookie.getName().startsWith("OAUTH2_") ||
                        cookie.getName().equals("JSESSIONID")) {
                    Cookie deleteCookie = new Cookie(cookie.getName(), null);
                    deleteCookie.setPath("/");
                    deleteCookie.setMaxAge(0);
                    deleteCookie.setHttpOnly(true);
                    response.addCookie(deleteCookie);
                }
            }
        }
    }
}
