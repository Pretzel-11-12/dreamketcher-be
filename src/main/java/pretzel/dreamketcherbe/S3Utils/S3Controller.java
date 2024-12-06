package pretzel.dreamketcherbe.S3Utils;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/image")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    /*
     * 이미지 파일 단일 업로드
     */
    @PostMapping("/upload")
    public ResponseEntity<?> s3Upload(@RequestPart(value = "iamge") MultipartFile image) {
        String thumbnailImage = s3Service.imageUpload(image);
        return ResponseEntity.ok(thumbnailImage);
    }

    /*
     * 이미지 파일 다중 업로드
     */
    @PostMapping("/uploads")
    public ResponseEntity<?> s3Uploads(@RequestPart(value = "images") List<MultipartFile> images) {
        return ResponseEntity.ok(s3Service.uploadImages(images));
    }


    /*
     * 이미지 파일 삭제
     */
    @GetMapping("/delete")
    public ResponseEntity<?> s3Delete(@RequestParam String imageUrlKey) {
        s3Service.deleteImageFile(imageUrlKey);
        return ResponseEntity.ok(null);
    }

}
