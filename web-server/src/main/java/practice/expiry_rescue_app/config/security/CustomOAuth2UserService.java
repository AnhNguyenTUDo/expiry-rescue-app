package practice.expiry_rescue_app.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import practice.expiry_rescue_app.entity.User;
import practice.expiry_rescue_app.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        return processOAuth2User(userRequest, oauth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId;
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String pictureUrl = oauth2User.getAttribute("picture");  // ✅ ADD: Extract profile picture

        // Handle different providers
        if ("google".equals(provider)) {
            providerId = oauth2User.getAttribute("sub");
        } else if ("facebook".equals(provider)) {
            providerId = oauth2User.getAttribute("id");
        } else {
            throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
        }

        // Find existing user by provider+providerId OR by email
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> userRepository.findByEmail(email).orElse(null));

        if (user == null) {
            // Create new user
            user = new User();
            user.setEmail(email);
            user.setUsername(email);  // Use email as username (unique)
            user.setProvider(provider);
            user.setProviderId(providerId);
            user.setIsActive(true);
        }

        // ✅ ADD: Always update profile info (may have changed)
        user.setFullName(name);
        user.setProfilePictureUrl(pictureUrl);
        user = userRepository.save(user);

        return new CustomOAuth2User(oauth2User, user);
    }
}
