package pretzel.dreamketcherbe.S3Utils;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import pretzel.dreamketcherbe.S3Utils.exception.S3Exception;
import pretzel.dreamketcherbe.S3Utils.exception.S3ExceptionType;

@Slf4j
@RequiredArgsConstructor
@Component
@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final Set<String> uploadedFileNames = new HashSet<>();
    private final Set<Long> uploadedFileSizes = new HashSet<>();

    /**
     * 단일 이미지 파일 업로드
     */
    public String imageUpload(MultipartFile multipartFile) {
        String newFileName = generateRandomFileName(multipartFile);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try {
            amazonS3.putObject(bucketName, newFileName, multipartFile.getInputStream(), metadata);
        } catch (IOException e) {
            log.error("Failed to convert image file", e);
            throw new S3Exception(S3ExceptionType.FAILED_TO_CONVERT_IMAGE);
        } catch (AmazonS3Exception e) {
            log.error("Amazon S3 error while uploading file: {}", e.getMessage(), e);
            throw new S3Exception(S3ExceptionType.UPLOAD_FAILED);
        }
        return amazonS3.getUrl(bucketName, newFileName).toString();
    }

    /**
     * 다중 이미지 파일 업로드
     */
    public List<String> imagesUpload(List<MultipartFile> multipartFiles) {
        List<String> uploadedFileUrls = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            if (isDuplicated(multipartFile)) {
                throw new S3Exception(S3ExceptionType.DUPLICATED_FILE);
            }
            try {
                uploadedFileUrls.add(imageUpload(multipartFile));
            } catch (S3Exception e) {
                throw new S3Exception(S3ExceptionType.UPLOAD_FAILED);
            }
        }
        clearUploadFiles();

        return uploadedFileUrls;
    }

    /**
     * 이미지 파일 수정
     */
    public String imageUpdate(String oldImage, MultipartFile newImage) {
        deleteImage(oldImage);

        return imageUpload(newImage);
    }

    /**
     * 다중 이미지 업로드 부분 수정
     */
    public List<String> updatePartialImages(
        List<String> existingImageUrls,
        List<MultipartFile> newImages,
        List<Integer> replaceIndices) {

        if (newImages.size() != replaceIndices.size()) {
            throw new IllegalArgumentException(
                "The number of new images and replace indices must match.");
        }

        List<String> updatedImageUrls = new ArrayList<>(existingImageUrls);

        for (int i = 0; i < newImages.size(); i++) {
            int replaceIndex = replaceIndices.get(i);

            if (replaceIndex < 0 || replaceIndex >= existingImageUrls.size()) {
                throw new IllegalArgumentException("Invalid index: " + replaceIndex);
            }

            deleteImage(existingImageUrls.get(replaceIndex));

            String newImageUrl = imageUpload(newImages.get(i));

            updatedImageUrls.set(replaceIndex, newImageUrl);
        }

        return updatedImageUrls;
    }

    /**
     * 이미지 파일 삭제
     */
    public void deleteImage(String fileName) {
        String objectKey = extractObjectKey(fileName);

        if (!amazonS3.doesObjectExist(bucketName, objectKey)) {
            throw new S3Exception(S3ExceptionType.IMAGE_NOT_FOUND);
        }

        try {
            amazonS3.deleteObject(bucketName, objectKey);
        } catch (AmazonS3Exception e) {
            throw new S3Exception(S3ExceptionType.DELETE_FAILED);
        }
    }

    /**
     * 파일 이름 중복 확인
     */
    private boolean isDuplicated(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        Long fileSize = multipartFile.getSize();

        if (uploadedFileNames.contains(fileName) && uploadedFileSizes.contains(fileSize)) {
            return true;
        }

        uploadedFileNames.add(fileName);
        uploadedFileSizes.add(fileSize);
        return false;
    }

    /**
     * 파일 정보 초기화
     */
    private void clearUploadFiles() {
        uploadedFileNames.clear();
        uploadedFileSizes.clear();
    }

    /**
     * 랜덤 파일명 생성
     */
    private String generateRandomFileName(MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        String extension = extractExtensionValidation(originalFileName);
        return UUID.randomUUID() + "_" + extension;
    }

    /**
     * 파일 확장자 검증
     */
    private String extractExtensionValidation(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtensions.contains(fileExtension)) {
            throw new S3Exception(S3ExceptionType.INVALID_FILE_EXTENSION);
        }
        return fileExtension;
    }

    /**
     * 객체 키 추출
     */
    private String extractObjectKey(String fileUrl) {
        String[] urlParts = fileUrl.split("/");
        return String.join("/", Arrays.copyOfRange(urlParts, 3, urlParts.length));
    }
}