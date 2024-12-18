package do_an.backend_educheck.services.cloudinary_service;

import do_an.backend_educheck.models.avatar_entity.AvatarEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ICloudinaryService {
    AvatarEntity uploadFile(MultipartFile multipartFile, Long useId);
    AvatarEntity uploadFileClassThumbnail(MultipartFile multipartFile, Long classId);
    void deleteFile(Long userId);
    void deleteFileClassThumbnail(Long classId);

    void deleteMultipleFilesClass(List<Long> classIds);
}
