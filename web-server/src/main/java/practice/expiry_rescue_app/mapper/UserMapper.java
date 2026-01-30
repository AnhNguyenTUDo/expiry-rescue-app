package practice.expiry_rescue_app.mapper;

import practice.expiry_rescue_app.entity.User;
import practice.expiry_rescue_app.model.auth.UserInfoDto;

public class UserMapper {

    /**
     * Convert User entity to UserInfoDto
     */
    public static UserInfoDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserInfoDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .provider(user.getProvider())
                .profilePictureUrl(user.getProfilePictureUrl())
                .isActive(user.getIsActive())
                .build();
    }

    /**
     * Update User entity from UserInfoDto (for profile updates)
     */
    public static void updateFromDto(User user, UserInfoDto dto) {
        if (user == null || dto == null) {
            return;
        }

        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        // Note: email, username, provider, and providerId should not be updated
    }
}
