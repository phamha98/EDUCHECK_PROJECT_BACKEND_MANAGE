package do_an.backend_educheck.services.class_service;


import do_an.backend_educheck.dtos.auth_dto.ResponseToUser;
import do_an.backend_educheck.models.class_entity.ClassEntity;
import do_an.backend_educheck.models.class_entity.ClassUserEntity;
import do_an.backend_educheck.models.user_entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

public interface IClassService {
    List<ClassEntity> findClassByUserId(Long userId);
    List<Long> findPartnerIdInClass(Long classId, Long userId);

    List<Long> findClassIdsByStudentId(Long studentId);

    HashMap<Integer, Long> findClassCountByMonth(int year, Long userId);
    Page<ClassEntity> findSearchClassByUserId(String searchText, Long userId, int page, int size);

    ClassEntity findClassById(Long Id, Long userId);

    ResponseToUser updateClassThumbnail(MultipartFile file, Long classId, Long userId);

    Boolean saveClass(ClassEntity classEntity);

    Boolean editClass(ClassEntity classEntity);

    Boolean deleteClassById(Long Id);

    Boolean joinClass(ClassUserEntity classUserEntity);

    Boolean leaveClass(ClassUserEntity classUserEntity);

    List<ClassEntity> findClassRegisteredByUserId(Long userId);
}
