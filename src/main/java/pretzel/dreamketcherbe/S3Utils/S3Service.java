package pretzel.dreamketcherbe.S3Utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import pretzel.dreamketcherbe.S3Utils.exception.S3Exception;
import pretzel.dreamketcherbe.S3Utils.exception.S3ExceptionType;

@Slf4j
@RequiredArgsConstructor
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
    public String imageUpload(MultipartFile multipartFile, String folderName) {
        String newFileName = generateRandomFileName(multipartFile);
        String filePath = folderName + "/" + newFileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try {
            amazonS3.putObject(bucketName, filePath, multipartFile.getInputStream(), metadata);
        } catch (IOException e) {
            log.error("파일 변환에 실패했습니다.", e);
            throw new S3Exception(S3ExceptionType.FAILED_TO_CONVERT_IMAGE);
        } catch (AmazonS3Exception e) {
            log.error("업로드 중 에러가 발생했습니다.: {}", e.getMessage(), e);
            throw new S3Exception(S3ExceptionType.UPLOAD_FAILED);
        }
        return amazonS3.getUrl(bucketName, filePath).toString();
    }

    /**
     * 다중 이미지 파일 업로드
     */
    public List<String> imagesUpload(List<MultipartFile> multipartFiles, String folderName) {
        List<String> uploadedFileUrls = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            if (isDuplicated(multipartFile)) {
                throw new S3Exception(S3ExceptionType.DUPLICATED_FILE);
            }
            try {
                uploadedFileUrls.add(imageUpload(multipartFile, folderName));
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
    public String imageUpdate(String oldImage, MultipartFile newImage, String folderName) {
        String newImageUrl;
        String newImageKey = null;
        try {
            newImageUrl = imageUpload(newImage, folderName);
            newImageKey = extractObjectKey(newImageUrl);

            deleteImage(oldImage);
        } catch (S3Exception e) {
            if (newImageKey != null) {
                try {
                    deleteImage(newImageKey);
                } catch (S3Exception cleanupException) {
                    log.error("새로운 이미지를 비우는데 실패했습니다.: {}", cleanupException.getMessage(),
                        cleanupException);
                }
            }
            throw new S3Exception(S3ExceptionType.UPDATE_FAILED);
        }
        return newImageUrl;
    }

    /**
     * 다중 이미지 업로드 부분 수정
     */
    public List<String> updatePartialImages(
        List<String> existingImageUrls,
        List<MultipartFile> newImages,
        List<Integer> replaceIndices,
        String folderName) {

        if (newImages.size() != replaceIndices.size()) {
            throw new IllegalArgumentException(
                "기존 이미지 수와 대치 이미지수가 맞지 않습니다.");
        }

        List<String> updatedImageUrls = new ArrayList<>(existingImageUrls);
        List<String> successUploadedUrls = new ArrayList<>(); // 업로드 성공한 파일 저장
        List<Integer> successUpdatedIndices = new ArrayList<>(); // 성공적으로 대치된 인덱스 저장

        try {
            for (int i = 0; i < newImages.size(); i++) {
                int replaceIndex = replaceIndices.get(i);

                if (replaceIndex < 0 || replaceIndex >= existingImageUrls.size()) {
                    throw new IllegalArgumentException("Invalid index: " + replaceIndex);
                }

                String newImageUrl = imageUpload(newImages.get(i), folderName);
                successUploadedUrls.add(newImageUrl);
                successUpdatedIndices.add(replaceIndex);

                updatedImageUrls.set(replaceIndex, newImageUrl);
            }

            for (int replaceIndex : successUpdatedIndices) {
                deleteImage(existingImageUrls.get(replaceIndex));
            }
        } catch (Exception e) {
            log.error("이미지 부분 수정에 실패했습니다.: {}", e.getMessage(), e);

            // 업로드된 새 이미지 삭제
            for (String uploadedUrl : successUploadedUrls) {
                try {
                    deleteImage(extractObjectKey(uploadedUrl));
                } catch (Exception cleanupException) {
                    log.error("이미지 삭제에 실패했습니다.: {}", cleanupException.getMessage(),
                        cleanupException);
                }
            }

            throw new S3Exception(S3ExceptionType.UPDATE_FAILED);
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
        String decodedUrl = URLDecoder.decode(fileUrl, StandardCharsets.UTF_8);
        String split = ".com/";

        return decodedUrl.substring(decodedUrl.lastIndexOf(split) + split.length());
    }
}