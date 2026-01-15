package practice.expiry_rescue_app.service.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import practice.expiry_rescue_app.exception.FileUploadException;
import practice.expiry_rescue_app.service.FirebaseStorageService;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FirebaseStorageServiceImpl implements FirebaseStorageService {

    @Value("${firebase.storage.bucket}")
    private String storageBucket;

    @Override
    public String uploadImage(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("File is empty");
        }

        try {
            // Generate unique filename
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            String filePath = folder + "/" + fileName;

            // Get Firebase Storage bucket
            Bucket bucket = StorageClient.getInstance().bucket();

            // Upload file
            Blob blob = bucket.create(filePath, file.getInputStream(), file.getContentType());

            // Generate signed URL (valid for 365 days)
            String imageUrl = blob.signUrl(365, TimeUnit.DAYS).toString();

            log.info("File uploaded successfully to Firebase Storage: {}", filePath);

            return imageUrl;

        } catch (IOException e) {
            log.error("Error uploading file to Firebase Storage: {}", e.getMessage());
            throw new FileUploadException("Failed to upload file", e);
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        try {
            // Extract blob name from URL
            String blobName = extractBlobNameFromUrl(imageUrl);

            if (blobName != null) {
                Bucket bucket = StorageClient.getInstance().bucket();
                Blob blob = bucket.get(blobName);

                if (blob != null) {
                    blob.delete();
                    log.info("File deleted successfully from Firebase Storage: {}", blobName);
                } else {
                    log.warn("Blob not found for deletion: {}", blobName);
                }
            }

        } catch (Exception e) {
            log.error("Error deleting file from Firebase Storage: {}", e.getMessage());
            // Don't throw exception for delete failures
        }
    }

    @Override
    public String generateUniqueFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    private String extractBlobNameFromUrl(String imageUrl) {
        try {
            // Extract the blob name from the signed URL
            // This is a simplified version; adjust based on your URL structure
            if (imageUrl.contains(storageBucket)) {
                String[] parts = imageUrl.split(storageBucket + "/");
                if (parts.length > 1) {
                    String path = parts[1].split("\\?")[0]; // Remove query parameters
                    return path;
                }
            }
        } catch (Exception e) {
            log.error("Error extracting blob name from URL: {}", e.getMessage());
        }
        return null;
    }
}
