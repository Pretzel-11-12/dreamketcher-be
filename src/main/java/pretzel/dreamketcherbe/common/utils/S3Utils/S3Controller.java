package pretzel.dreamketcherbe.common.utils.S3Utils;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/s3")
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

}
