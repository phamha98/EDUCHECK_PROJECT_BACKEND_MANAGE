package do_an.backend_educheck.controllers.user_controller;

import do_an.backend_educheck.controllers.ApiV1Controller;
import do_an.backend_educheck.dtos.auth_dto.ResponseToUser;
import do_an.backend_educheck.dtos.auth_dto.UserResponse;
import do_an.backend_educheck.dtos.user_dto.MembersInClass;
import do_an.backend_educheck.dtos.user_dto.PasswordRequest;
import do_an.backend_educheck.models.feedback_entity.FeedbackEntity;
import do_an.backend_educheck.models.user_entity.UserEntity;
import do_an.backend_educheck.services.user_service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
@RequiredArgsConstructor
@ApiV1Controller
@RequestMapping(value = "/", produces = "application/json")
public class UserController {
    private final IUserService iUserService;

    @GetMapping("user-info")
    public UserEntity getUserInfo(@RequestParam Long userId) {
        return iUserService.findUserInfoById(userId);
    }

    @PostMapping("update-password")
    public ResponseToUser updatePassword(@RequestBody @Valid PasswordRequest passwordRequest) {
        return iUserService.updatePassword(passwordRequest);
    }

    @PostMapping("reset-password/{userId}")
    public ResponseToUser resetPassword(@PathVariable Long userId) {
        return iUserService.resetUserPassword(userId);
    }

    @GetMapping("members/{classId}")
    public MembersInClass getMembersInClass(@PathVariable Long classId, @RequestParam(defaultValue = "") String searchText) {
        return iUserService.findUsersInClass(classId, searchText);
    }

    @GetMapping("users/statistics")
    public HashMap<Integer, Long> getUserStatistics(@RequestParam int year, @RequestParam(required = false) Long userId) {
        return iUserService.findUsersCountByMonth(year,userId);
    }

    @PostMapping("update-user-info")
    public ResponseToUser updateUserInfo(@RequestBody @Valid UserResponse userResponse) {
        return iUserService.updateUserInfo(userResponse);
    }

    @PostMapping("update-avatar")
    public ResponseToUser updateAvatar(@RequestParam(value = "fileImage", required = false) MultipartFile fileImage,
                                       @RequestParam("id") Long id) {
        return iUserService.updateAvatar(fileImage, id);
    }

    @PostMapping("delete-users")
    public ResponseToUser deleteUsers(@RequestParam String ids) {
        String[] splitIds = ids.split(",");
        List<Long> longIds = Arrays.stream(splitIds).map(Long::valueOf).collect(Collectors.toList());
        return iUserService.deleteUsersByIds(longIds);
    }

    @GetMapping("user/pagination")
    public List<UserResponse> getAllUsers(@RequestParam String username, @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        Page<UserEntity> userEntityPage = iUserService.searchAllUsers(username, page, size);
        List<UserResponse> users = userEntityPage.getContent().stream().map(UserResponse::new).collect(
                Collectors.toList());
        return users;
    }

    @GetMapping("sync-user-system")
    public Page<UserEntity> syncUserSystem(@RequestParam(defaultValue = "0") int pageNumber,
                                           @RequestParam(defaultValue = "0") Long lastId,
                                           @RequestParam(defaultValue = "1000") int batchSize,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastUpdated) {

        return iUserService.getUpdatedUsers(lastId, lastUpdated, PageRequest.of(pageNumber, batchSize));
    }
}
