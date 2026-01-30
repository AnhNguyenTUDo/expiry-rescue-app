package practice.expiry_rescue_app.controller.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practice.expiry_rescue_app.model.auth.UserInfoDto;
import practice.expiry_rescue_app.entity.User;
import practice.expiry_rescue_app.mapper.UserMapper;
import practice.expiry_rescue_app.repository.UserRepository;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserInfoDto> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String email = (String) authentication.getPrincipal();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserInfoDto userInfoDto = UserMapper.toDto(user);
        return ResponseEntity.ok(userInfoDto);
    }
}
