package pretzel.dreamketcherbe.common.utils.S3Utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import pretzel.dreamketcherbe.common.utils.S3Utils.exception.S3Exception;
import pretzel.dreamketcherbe.common.utils.S3Utils.exception.S3ErrorCode;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Service {

    private final AmazonS3 amazonS3;

    private String bucketName;

    /*
     * 이미지 단일 파일 업로드
     */
    public String imageUpload(MultipartFile image) {

        try {
            return uploadImageToS3(image);
        } catch (IOException e) {
            throw new S3Exception(S3ErrorCode.UPLOAD_FAILED);
        }
    }

    /*
     * S3에 단일 이미지 업로드
     */
    private String uploadImageToS3(MultipartFile image) throws IOException {
        String originalFileName = image.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFileName;

        InputStream inputStream = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(inputStream); // 이미지 바이트 배열로 전환

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("image/" + extension);
        objectMetadata.setContentLength(bytes.length);

        // S3에 이미지 업로드
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName,
                byteArrayInputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.AuthenticatedRead.PublicRead);
            amazonS3.putObject(putObjectRequest); // 이미지 업로드
        } catch (Exception e) {
            throw new S3Exception(S3ErrorCode.UPLOAD_FAILED);
        } finally {
            byteArrayInputStream.close();
            inputStream.close();
        }
        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }

    /*
     * 이미지 파일 검증
     */
    private String validateImageFile(MultipartFile image) {
        if (Objects.isNull(image.getOriginalFilename()) || image.isEmpty()) {
            throw new S3Exception(S3ErrorCode.EMPTY_FILE);
        }

        return imageUpload(image);
    }

    /*
     * 이미지 파일 형식 검사
     */
    private void validateImageFileFormat(String imageFileName) {
        int index = imageFileName.lastIndexOf(".");

        if (index == -1) {
            throw new S3Exception(S3ErrorCode.INVALID_FILE_EXTENSION);
        }

        String extension = imageFileName.substring(index + 1).toLowerCase();
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "JPG", "JPEG", "png", "gif");

        if (!allowedExtensions.contains(extension)) {
            throw new S3Exception(S3ErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    /*
     * 이미지 파일 크기 검증
     */
    private void validateImageFileSize(MultipartFile image) {
        if (image.getSize() > 5 * 1024 * 1024) {
            throw new S3Exception(S3ErrorCode.INVALID_FILE_SIZE);
        }
    }
}
