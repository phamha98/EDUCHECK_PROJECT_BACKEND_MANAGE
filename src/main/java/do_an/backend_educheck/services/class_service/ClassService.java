package do_an.backend_educheck.services.class_service;

import do_an.backend_educheck.dtos.auth_dto.ResponseToUser;
import do_an.backend_educheck.exceptions.ArgumentException;
import do_an.backend_educheck.models.avatar_entity.AvatarEntity;
import do_an.backend_educheck.models.class_entity.ClassEntity;
import do_an.backend_educheck.models.class_entity.ClassUserEntity;
import do_an.backend_educheck.models.user_entity.UserEntity;
import do_an.backend_educheck.repositories.IAvatarsRepo;
import do_an.backend_educheck.repositories.IClassRepo;
import do_an.backend_educheck.repositories.IClassUserRepo;
import do_an.backend_educheck.services.cloudinary_service.ICloudinaryService;
import do_an.backend_educheck.services.user_service.IUserService;
import do_an.backend_educheck.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassService implements IClassService {
    private final IClassRepo iClassRepo;
    private final IClassUserRepo iClassUserRepo;
    private final IUserService iUserService;
    private final ICloudinaryService iCloudinaryService;

    @Override
    public Boolean saveClass(ClassEntity classEntity) {
        try {
            iClassRepo.save(classEntity);
            return true;
        } catch (Exception ex) {
            throw new ArgumentException("Vui lòng điền đẩy đủ thông tin");
        }
    }

    @Override
    public Boolean editClass(ClassEntity classEntity) {
        try {
            return iClassRepo.findById(classEntity.getId()).map(oldClass -> {
                BeanUtils.copyProperties(classEntity, oldClass, "id");
                iClassRepo.save(oldClass);
                return true;
            }).orElse(false);

        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public Boolean deleteClassById(Long id) {
        if (id == null) {
            return false;
        }
        try {
            iClassRepo.deleteById(id);
            iClassUserRepo.deleteAllByClassId(id);
            iCloudinaryService.deleteFileClassThumbnail(id);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public Boolean joinClass(ClassUserEntity classUserEntity) {
        try {
            Optional<ClassEntity> classEntity = iClassRepo.findById(classUserEntity.getClassId());

            if (!classEntity.isPresent()) {
                throw new ArgumentException("Mã phòng không hợp lệ");
            }

            if (classUserEntity.getStudentId() == classEntity.get().getUserId()) {
                return true;
            }

            Optional<ClassUserEntity> classUser = iClassUserRepo.findByClassIdAndStudentId(classUserEntity.getClassId(),
                    classUserEntity.getStudentId());

            if (!classUser.isPresent()) {
                iClassUserRepo.save(classUserEntity);
            }

            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    @Transactional
    @Override
    public Boolean leaveClass(ClassUserEntity classUserEntity) {
        try {
            iClassUserRepo.deleteByClassIdAndStudentId(classUserEntity.getClassId(), classUserEntity.getStudentId());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public List<ClassEntity> findClassRegisteredByUserId(Long userId) {
        List<Long> classIds = iClassUserRepo.findByStudentId(userId).stream().map(ClassUserEntity::getClassId).collect(
                Collectors.toList());

        return iClassRepo.findAllClassByIds(classIds);
    }

    @Override
    public List<ClassEntity> findClassByUserId(Long userId) {
        return iClassRepo.findByUserId(userId);
    }

    @Override
    public List<Long> findPartnerIdInClass(Long classId, Long userId) {
        return iClassUserRepo.findUsersInClass(classId, userId);
    }

    @Override
    public List<Long> findClassIdsByStudentId(Long studentId) {
        List<Long> classIds = iClassUserRepo.findByStudentId(studentId).stream().map(ClassUserEntity::getClassId).collect(
                Collectors.toList());
        return classIds;
    }

    @Override
    public HashMap<Integer, Long> findClassCountByMonth(int year, Long userId) {
        List<Object[]> results = iClassRepo.countClassesByMonth(year);
        if (userId != null) {
            results = iClassRepo.countClassesByMonthAndUserId(year, userId);
        }

        HashMap<Integer, Long> classCountByMonth = new HashMap<>();

        for (int i = 1; i <= 12; i++) {
            classCountByMonth.put(i, 0L);
        }

        for (Object[] result : results) {
            Integer month = (Integer) result[0];
            Long count = (Long) result[1];
            classCountByMonth.put(month, count);
        }

        return classCountByMonth;
    }

    @Override
    public Page<ClassEntity> findSearchClassByUserId(String searchText, Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        if (searchText.isBlank()) {
            return iClassRepo.findAllByUserId(userId, pageable);
        }

        return iClassRepo.findClassByUserIdAndSearchText(searchText, userId, pageable);
    }

    @Override
    public ClassEntity findClassById(Long Id, Long userId) {
        Optional<ClassEntity> classEntity = iClassRepo.findById(Id);

        if (!classEntity.isPresent()) {
            throw new ArgumentException("Lớp học không tồn tại");
        }

        ClassEntity classData = classEntity.get();

        if (classData.getUserId().equals(userId)) {
            return classData;
        }

        Optional<ClassUserEntity> classUser = iClassUserRepo.findByClassIdAndStudentId(Id, userId);

        if (!classUser.isPresent()) {
            throw new ArgumentException("Bạn chưa tham gia lớp học");
        }

        return classData;
    }

    @Override
    public ResponseToUser updateClassThumbnail(MultipartFile file, Long classId, Long userId) {
        UserEntity userEntity = iUserService.findUserInfoById(userId);
        if (userEntity == null) {
            return new ResponseToUser(false, "User không dược tìm thấy");
        }

        Optional<ClassEntity> classEntity = iClassRepo.findById(classId);

        if (!classEntity.isPresent()) {
            throw new ArgumentException("Lớp học không tồn tại");
        }

        ClassEntity classData = classEntity.get();

        if (!classData.getUserId().equals(userId)) {
            throw new ArgumentException("Bạn không có quyền chỉnh sửa");
        }

        AvatarEntity response = null;
        //set image as default
        if (file == null || file.isEmpty()) {
            iCloudinaryService.deleteFileClassThumbnail(classId);
            classData.setThumbnail(null);
        } else {
            FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
            response = iCloudinaryService.uploadFileClassThumbnail(file, classId);
            classData.setThumbnail(response.getUrl());
        }
        iClassRepo.save(classData);

        return new ResponseToUser(true, "Cập nhật thành công");
    }


}
