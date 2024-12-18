package do_an.backend_educheck.services.user_service;

import do_an.backend_educheck.dtos.auth_dto.ResponseToUser;
import do_an.backend_educheck.dtos.auth_dto.UserResponse;
import do_an.backend_educheck.dtos.user_dto.MembersInClass;
import do_an.backend_educheck.dtos.user_dto.PasswordRequest;
import do_an.backend_educheck.models.user_entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public interface IUserService {
    UserEntity findUserInfoById(Long id);
    ResponseToUser updatePassword(PasswordRequest passwordRequest);
    ResponseToUser updateUserInfo(UserResponse userResponse);
    ResponseToUser updateAvatar(MultipartFile file, Long id);

    HashMap<Integer, Long> findUsersCountByMonth(int year, Long userId);

    MembersInClass findUsersInClass(Long id, String searchText);

    Page<UserEntity> getUpdatedUsers(Long lastId, LocalDateTime lastUpdated, Pageable pageable);

    ResponseToUser deleteUsersByIds(List<Long> ids);

    Page<UserEntity> searchAllUsers(String username, int page, int size);

    ResponseToUser resetUserPassword(Long userId);
}
