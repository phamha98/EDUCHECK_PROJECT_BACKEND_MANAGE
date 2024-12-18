package do_an.backend_educheck.dtos.auth_dto;

import do_an.backend_educheck.models.user_entity.UserEntity;

public record AuthResponse(Long id, String username, String email, String address, String gender, String avatar,
                           String sdt, Boolean favorite, String role, String token) {
    public AuthResponse(UserEntity userEntity, String token) {
        this(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail(), userEntity.getAddress(),
             userEntity.getGender(), userEntity.getAvatar(), userEntity.getSdt(), userEntity.getFavorite(),
             userEntity.getRole(), token);
    }
}
