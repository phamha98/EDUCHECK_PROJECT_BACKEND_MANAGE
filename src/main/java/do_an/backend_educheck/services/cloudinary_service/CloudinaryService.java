package do_an.backend_educheck.services.cloudinary_service;

import com.cloudinary.Cloudinary;
import do_an.backend_educheck.exceptions.ValidateException;
import do_an.backend_educheck.models.avatar_entity.AvatarEntity;
import do_an.backend_educheck.repositories.IAvatarsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CloudinaryService implements ICloudinaryService {
    private final Cloudinary cloudinary;
    private final IAvatarsRepo iAvatarsRepo;

    @Transactional
    public AvatarEntity uploadFile(MultipartFile multipartFile, Long userId) {
        try {
            Map result = cloudinary.uploader().upload(multipartFile.getBytes(),
                                                      Map.of("name", String.valueOf(userId), "public_id",
                                                             String.valueOf(userId), "folder", "avatar/" + userId,
                                                             "overwrite", true));
            String url = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");

            AvatarEntity avatarEntity = AvatarEntity.builder().userId(userId).publicId(publicId).url(url).build();

            iAvatarsRepo.deleteAllByUserId(userId);

            iAvatarsRepo.save(avatarEntity);

            return avatarEntity;
        } catch (IOException e) {
            throw new ValidateException("Upload không thành công. Hãy thử lại!");
        }
    }

    @Transactional
    public AvatarEntity uploadFileClassThumbnail(MultipartFile multipartFile, Long classId) {
        try {
            Map result = cloudinary.uploader().upload(multipartFile.getBytes(),
                                                      Map.of("name", String.valueOf(classId), "public_id",
                                                             String.valueOf(classId), "folder", "avatar",
                                                             "overwrite", true));
            String url = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");

            AvatarEntity avatarEntity = AvatarEntity.builder().classId(classId).publicId(publicId).url(url).build();

            iAvatarsRepo.deleteAllByClassId(classId);

            iAvatarsRepo.save(avatarEntity);

            return avatarEntity;
        } catch (IOException e) {
            throw new ValidateException("Upload không thành công. Hãy thử lại!");
        }
    }

    @Transactional
    public void deleteFile(Long userId) {
        try {
            Optional<AvatarEntity> currentAvatar = iAvatarsRepo.findByUserId(userId);

            if (currentAvatar.isPresent()) {
                cloudinary.uploader().destroy(currentAvatar.get().getPublicId(), Map.of("invalidate", true));
                iAvatarsRepo.deleteAllByUserId(userId);
            }

        } catch (IOException e) {
            throw new ValidateException("Upload không thành công. Hãy thử lại!");
        }
    }

    @Transactional
    public void deleteFileClassThumbnail(Long classId) {
        try {
            Optional<AvatarEntity> currentAvatar = iAvatarsRepo.findByClassId(classId);

            if (currentAvatar.isPresent()) {
                cloudinary.uploader().destroy(currentAvatar.get().getPublicId(), Map.of("invalidate", true));
                iAvatarsRepo.deleteAllByClassId(classId);
            }

        } catch (IOException e) {
            throw new ValidateException("Upload không thành công. Hãy thử lại!");
        }
    }

    @Transactional
    public void deleteMultipleFilesClass(List<Long> classIds) {
        try {
            List<AvatarEntity> classAvatars = iAvatarsRepo.findClassAvatars(classIds);
            List<String> publicIDs = classAvatars.stream().map(AvatarEntity::getPublicId).collect(Collectors.toList());

            if (publicIDs.size() != 0) {
                Map<String, Object> map = new HashMap<>(Map.of("invalidate", true));
                map.put("type", "upload");
                cloudinary.api().deleteResources(publicIDs, map);
            }

            iAvatarsRepo.deleteClassAvatars(classIds);

        } catch (IOException e) {
            throw new ValidateException("Upload không thành công. Hãy thử lại!");
        } catch (Exception e) {
            throw new ValidateException(" không thành công. Hãy thử lại!");
        }
    }
}
