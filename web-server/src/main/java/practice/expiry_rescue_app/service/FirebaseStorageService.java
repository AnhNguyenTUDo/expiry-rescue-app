package practice.expiry_rescue_app.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service for Firebase Storage operations
 */
public interface FirebaseStorageService {

    String uploadImage(MultipartFile file, String folder);

    void deleteImage(String imageUrl);

    String generateUniqueFileName(String originalFilename);
}
