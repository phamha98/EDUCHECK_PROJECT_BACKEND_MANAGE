package do_an.backend_educheck.dtos.auth_dto;

import do_an.backend_educheck.models.user_entity.UserEntity;

public record UserResponse(Long id, String username, String email, String address, String gender, String avatar,
                           String sdt, String role, Boolean favorite) {
    public UserResponse(UserEntity clientEntity) {
        this(clientEntity.getId(), clientEntity.getUsername(), clientEntity.getEmail(), clientEntity.getAddress(), clientEntity.getGender(),
                clientEntity.getAvatar(), clientEntity.getSdt(), clientEntity.getRole(), clientEntity.getFavorite());
    }
}