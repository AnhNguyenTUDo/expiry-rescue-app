package practice.expiry_rescue_app.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // Extract JWT from request
            String jwt = getJwtFromRequest(request);

            // Validate and authenticate
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // Get user email from token
                String email = tokenProvider.getEmailFromToken(jwt);

                // Create authentication token
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());

                // Set additional details
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Set authentication for user: {}", email);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
