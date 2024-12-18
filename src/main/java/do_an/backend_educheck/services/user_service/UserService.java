package do_an.backend_educheck.services.user_service;

import do_an.backend_educheck.dtos.auth_dto.ResponseToUser;
import do_an.backend_educheck.dtos.auth_dto.UserResponse;
import do_an.backend_educheck.dtos.user_dto.MembersInClass;
import do_an.backend_educheck.dtos.user_dto.PasswordRequest;
import do_an.backend_educheck.exceptions.ArgumentException;
import do_an.backend_educheck.exceptions.EditProfileException;
import do_an.backend_educheck.models.avatar_entity.AvatarEntity;
import do_an.backend_educheck.models.class_entity.ClassEntity;
import do_an.backend_educheck.models.class_entity.ClassUserEntity;
import do_an.backend_educheck.models.feedback_entity.FeedbackEntity;
import do_an.backend_educheck.models.user_entity.UserEntity;
import do_an.backend_educheck.repositories.IClassRepo;
import do_an.backend_educheck.repositories.IClassUserRepo;
import do_an.backend_educheck.repositories.IUserRepo;
import do_an.backend_educheck.services.cloudinary_service.CloudinaryService;
import do_an.backend_educheck.services.cloudinary_service.ICloudinaryService;
import do_an.backend_educheck.utils.FileUploadUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IUserRepo iUserRepo;
    private final IClassRepo iClassRepo;
    private final IClassUserRepo iClassUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final ICloudinaryService iCloudinaryService;

    private final static String BASE_PASSWORD = "123456@Ok";

    @Override
    public UserEntity findUserInfoById(Long id) {
        Optional<UserEntity> user = iUserRepo.findById(id);
        return user.get();
    }

    @Override
    public ResponseToUser updatePassword(PasswordRequest passwordRequest) {
        if (passwordRequest.getOldPassword().equals(passwordRequest.getPassword())) {
            return new ResponseToUser(false, "Mật khẩu mới không được trùng với mật khẩu cũ");
        }

        final UserEntity userEntity = iUserRepo.findByEmail(passwordRequest.getEmail());
        if (userEntity == null) {
            throw new EditProfileException("");
        }

        boolean isMatcher = passwordEncoder.matches(passwordRequest.getOldPassword(), userEntity.getPassword());
        if (!isMatcher) {
            throw new EditProfileException("");
        } else {
            String newPassword = passwordRequest.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            userEntity.setPassword(encodedPassword);
            iUserRepo.save(userEntity);
        }
        return new ResponseToUser(true, "Cập nhật thành công");
    }

    @Override
    public ResponseToUser updateUserInfo(UserResponse userResponse) {
        if (userResponse.id() == null) {
            return new ResponseToUser(false, "error-id-param-not-provided");
        }

        Optional<UserEntity> userEntity = iUserRepo.findByUsernameOrEmailAndNotId(userResponse.username(),
                                                                                  userResponse.email(),
                                                                                  userResponse.id());
        if (userEntity.isPresent()) {
            return new ResponseToUser(false, "username hoặc email đã trùng với người khác");
        }
        UserEntity existingUser = iUserRepo.findById(userResponse.id())
                .orElseThrow(() -> new ArgumentException("Người dùng không tìm thấy"));

        UserEntity newUserInfo = new UserEntity(userResponse);
        newUserInfo.setPassword(existingUser.getPassword());

        iUserRepo.save(newUserInfo);

        return new ResponseToUser(true, "Cập nhật thành công");
    }

    @Override
    public ResponseToUser updateAvatar(MultipartFile file, Long userId) {
        UserEntity userEntity = this.findUserInfoById(userId);
        if (userEntity == null) {
            return new ResponseToUser(false, "User không dược tìm thấy");
        }

        AvatarEntity response = null;
        //set image as default
        if (file == null || file.isEmpty()) {
            iCloudinaryService.deleteFile(userId);
            userEntity.setAvatar(null);
        } else {
            FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
            //        String fileName = FileUploadUtil.getFileName(file.getOriginalFilename());
            response = iCloudinaryService.uploadFile(file, userId);
            userEntity.setAvatar(response.getUrl());
        }

        iUserRepo.save(userEntity);

        return new ResponseToUser(true, response != null ? response.getUrl() : null);
    }

    @Override
    public HashMap<Integer, Long> findUsersCountByMonth(int year, Long userId) {
        List<Object[]> results = iUserRepo.countUsersByMonth(year);
        if (userId != null) {
            List<Long> classIds = iClassRepo.findByUserId(userId).stream().map(ClassEntity::getId).toList();
            List<Long> userIds = iClassUserRepo.findClassUsers(classIds).stream().map(ClassUserEntity::getStudentId).toList();

            results = iUserRepo.countUsersByMonthAndUserIds(year, userIds);
        }

        HashMap<Integer, Long> userCountByMonth = new HashMap<>();

        for (int i = 1; i <= 12; i++) {
            userCountByMonth.put(i, 0L);
        }

        for (Object[] result : results) {
            Integer month = (Integer) result[0];
            Long count = (Long) result[1];
            userCountByMonth.put(month, count);
        }

        return userCountByMonth;
    }

    @Override
    public MembersInClass findUsersInClass(Long id, String searchText) {
        Optional<ClassEntity> classEntity = iClassRepo.findById(id);

        if (!classEntity.isPresent()) {
            throw new ArgumentException("Phòng " + id + " không tồn tại");
        }

        List<Long> userIds = iClassUserRepo.findUsersInClass(classEntity.get().getId(), classEntity.get().getUserId());

        UserResponse teacher = iUserRepo.findById(classEntity.get().getUserId()).map(UserResponse::new).get();
        List<UserEntity> users;
        if(searchText.isBlank()) {
            users = iUserRepo.findUsersInClass(userIds);
        } else {
            users = iUserRepo.findUsersInClassByUsername(userIds, searchText);
        }

        List<UserResponse> students = users.stream().map(UserResponse::new).collect(Collectors.toList());


        return new MembersInClass(teacher,students);
    }

    @Override
    public Page<UserEntity> getUpdatedUsers(Long lastId, LocalDateTime lastUpdated, Pageable pageable) {
        return iUserRepo.findUpdatedUsers(lastId, lastUpdated, pageable);
    }

    @Override
    @Transactional
    public ResponseToUser deleteUsersByIds(List<Long> ids) {

        try {
            if (ids != null) {

                for(Long userId: ids){
                    iCloudinaryService.deleteFile(userId);

                    List<ClassEntity> classes = iClassRepo.findByUserId(userId);
                    List<Long> classIds = classes.stream().map(ClassEntity::getId).collect(Collectors.toList());

                    iCloudinaryService.deleteMultipleFilesClass(classIds);
                }
                iUserRepo.deleteUsersById(ids);
                iUserRepo.deleteClassesByUserId(ids);
                iUserRepo.deleteClassUserByStudentId(ids);
            }
            return new ResponseToUser(true, "Xóa thành công");
        } catch (Exception ex) {
            System.out.println("ex-deleteUsersByIds::"+ex.getMessage());
            return new ResponseToUser(false, "Xóa thất bại");
        }
    }

    @Override
    public Page<UserEntity> searchAllUsers(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if (username.isBlank()) {
            return iUserRepo.findAll(pageable);
        }

        return iUserRepo.findByUsernameContainingIgnoreCase(username, pageable);
    }

    @Override
    public ResponseToUser resetUserPassword(Long userId) {
        Optional<UserEntity> userEntity = iUserRepo.findById(userId);
        if (!userEntity.isPresent()) {
            throw new EditProfileException("");
        }
        UserEntity userInfo = userEntity.get();
        String encodedPassword = passwordEncoder.encode(BASE_PASSWORD);
        userInfo.setPassword(encodedPassword);
        iUserRepo.save(userInfo);
        return new ResponseToUser(true, "Cập nhật thành công");
    }
}
