package pretzel.dreamketcherbe.S3Utils;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/image")
public class S3Controller {

    private final S3Service s3Service;

    /*
     * 이미지 파일 단일 업로드
     */
    @PostMapping("/upload")
    public ResponseEntity<String> s3UploadImage(
        @RequestParam(value = "image") MultipartFile image) {
        String thumbnailImage = s3Service.imageUpload(image);
        return ResponseEntity.ok(thumbnailImage);
    }

    /*
     * 이미지 파일 다중 업로드
     */
    @PostMapping("/uploads")
    public ResponseEntity<?> s3UploadImages(
        @RequestParam(value = "images") List<MultipartFile> images) {
        List<String> uploadedImages = s3Service.imagesUpload(images);
        return ResponseEntity.ok(uploadedImages);
    }

    /**
     * 단일 이미지 수정
     */
    @PutMapping("/{imageUrl}")
    public ResponseEntity<String> s3UpdateImage(
        @PathVariable String imageUrl, @RequestParam("image") MultipartFile image) {
        String updatedImageUrl = s3Service.imageUpdate(imageUrl, image);
        return ResponseEntity.ok(updatedImageUrl);
    }

    /**
     * 이미지 부분 수정
     */
    @PutMapping("/images")
    public ResponseEntity<List<String>> updatePartialImages(
        @RequestParam("existingUrls") List<String> existingUrls,
        @RequestParam("newImages") List<MultipartFile> newImages,
        @RequestParam("replaceIndices") List<Integer> replaceIndices) {

        List<String> updatedImageUrls = s3Service.updatePartialImages(existingUrls, newImages,
            replaceIndices);
        return ResponseEntity.ok(updatedImageUrls);
    }

    /*
     * 이미지 파일 삭제
     */
    @GetMapping("/delete")
    public ResponseEntity<Void> s3Delete(@RequestParam("image") String imageUrl) {
        s3Service.deleteImage(imageUrl);
        return ResponseEntity.noContent().build();
    }

}
