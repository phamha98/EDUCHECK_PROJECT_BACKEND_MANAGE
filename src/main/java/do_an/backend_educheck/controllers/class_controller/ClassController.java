package do_an.backend_educheck.controllers.class_controller;

import do_an.backend_educheck.controllers.ApiV1Controller;
import do_an.backend_educheck.dtos.auth_dto.ResponseToUser;
import do_an.backend_educheck.dtos.class_dto.PageClass;
import do_an.backend_educheck.dtos.quiz_dto.QuizDto;
import do_an.backend_educheck.exceptions.ArgumentException;
import do_an.backend_educheck.models.class_entity.ClassEntity;
import do_an.backend_educheck.models.class_entity.ClassUserEntity;
import do_an.backend_educheck.models.user_entity.UserEntity;
import do_an.backend_educheck.services.ApiService;
import do_an.backend_educheck.services.auth_service.IAuthService;
import do_an.backend_educheck.services.class_service.IClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@ApiV1Controller
@RequestMapping(value = "/", produces = "application/json")
@Validated
public class ClassController {
    private final IClassService iClassService;
    private final ApiService apiService;

    @GetMapping("class/{classId}")
    public ClassEntity getClassById(@PathVariable Long classId, @RequestParam Long userId) {
        return iClassService.findClassById(classId, userId);
    }

    @GetMapping("classes/registered/{userId}")
    public List<ClassEntity> getClassRegisteredByUserId(@PathVariable Long userId) {
        return iClassService.findClassRegisteredByUserId(userId);
    }

    @GetMapping("classes/teaching/{userId}")
    public List<ClassEntity> getClassByUserId(@PathVariable Long userId) {
        return iClassService.findClassByUserId(userId);
    }

    @GetMapping("classes/ids/{studentId}")
    public List<Long> getClassIdsByStudentId(@PathVariable Long studentId) {
        return iClassService.findClassIdsByStudentId(studentId);
    }

    @GetMapping("search-classes/{userId}")
    public PageClass getSearchClassByUserId(@RequestParam String searchText, @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "1") int size, @PathVariable Long userId) {
        if (userId == null) {
            throw new ArgumentException("userId không tồn tại");
        }
        Page<ClassEntity> classEntityPage = iClassService.findSearchClassByUserId(searchText, userId, page, size);

        List<QuizDto> quizes = apiService.getAllQuizzesByClassAndUser(classEntityPage.getContent().stream().map(ClassEntity::getId).toList(), userId);
        return new PageClass(classEntityPage.getContent(), classEntityPage.getTotalPages(), quizes);
    }

    @GetMapping("class/partners")
    public List<Long> getPartnerIdInClass(@RequestParam Long classId, @RequestParam Long userId) {
        return iClassService.findPartnerIdInClass(classId, userId);
    }

    @GetMapping("classes/statistics")
    public HashMap<Integer, Long> getClassStatistics(@RequestParam int year, @RequestParam(required = false) Long userId) {
        return iClassService.findClassCountByMonth(year, userId);
    }

    @PostMapping("save-class")
    public ResponseToUser createClass(@RequestBody ClassEntity classEntity) {
        boolean isSaveSuccess = iClassService.saveClass(classEntity);
        if (isSaveSuccess) {
            return new ResponseToUser(true, "Tạo thành công");
        }
        return new ResponseToUser(false, "Tạo thất bại, hãy thử lại");
    }

    @PostMapping("edit-class")
    public ResponseToUser editClass(@RequestBody ClassEntity classEntity) {
        boolean isSaveSuccess = iClassService.editClass(classEntity);
        if (isSaveSuccess) {
            return new ResponseToUser(true, "Lưu thành công");
        }
        return new ResponseToUser(false, "Lưu thất bại");
    }

    @DeleteMapping("classes/teaching/{id}")
    public ResponseToUser deleteClass(@PathVariable Long id) {
        boolean isDelSuccess = iClassService.deleteClassById(id);
        if (isDelSuccess) {
            return new ResponseToUser(true, "Xóa thành công");
        }
        return new ResponseToUser(false, "Xóa thất bại");
    }

    @PostMapping("join-class")
    public ResponseToUser joinClass(@RequestBody ClassUserEntity classUserEntity) {
        boolean isSaveSuccess = iClassService.joinClass(classUserEntity);
        if (isSaveSuccess) {
            return new ResponseToUser(true, "Tham gia thành công");
        }
        return new ResponseToUser(false, "Tham gia thất bại, hãy thử lại hoặc không tồn tại phòng");
    }

    @PostMapping("leave-class")
    public ResponseToUser leaveClass(@RequestBody ClassUserEntity classUserEntity) {
        boolean isDelSuccess = iClassService.leaveClass(classUserEntity);
        if (isDelSuccess) {
            return new ResponseToUser(true, "Rời thành công");
        }
        return new ResponseToUser(false, "Rời thất bại, hãy thử lại");
    }

    @PutMapping("update-class-thumbnail")
    public ResponseToUser updateClassThumbnail(
            @RequestParam(value = "fileImage", required = false) MultipartFile fileImage,
            @RequestParam("classId") Long classId, @RequestParam("userId") Long userId) {
        return iClassService.updateClassThumbnail(fileImage, classId, userId);
    }
}
