package pretzel.dreamketcherbe.domain.member.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.common.dto.PageReqDto;
import pretzel.dreamketcherbe.common.dto.PageResDto;
import pretzel.dreamketcherbe.domain.comment.dto.MyCommentsAndRecommentsListResDto;
import pretzel.dreamketcherbe.domain.comment.service.CommentService;
import pretzel.dreamketcherbe.domain.member.dto.InterestedWebtoonResponse;
import pretzel.dreamketcherbe.domain.member.dto.SelfInfoResponse;
import pretzel.dreamketcherbe.domain.member.dto.UpdateProfileRequest;
import pretzel.dreamketcherbe.domain.member.dto.WorkResDto;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.service.MemberService;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final CommentService commentService;

    @GetMapping("/me")
    public ResponseEntity<SelfInfoResponse> me(@Auth Long memberId) {
        return ResponseEntity.ok(memberService.getSelfInfo(memberId));
    }

    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfileWithImage(
        @Auth Long memberId,
        @RequestPart(value = "image", required = false) MultipartFile image,
        @RequestPart(value = "profileData", required = false) @Valid UpdateProfileRequest profileData
    ) {
        memberService.updateProfileWithImage(memberId, image, profileData);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Member>> getAllMembers() {
        List<Member> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/favorite/{webtoonId}")
    public ResponseEntity<Boolean> isFavoriteWebtoon(
        @Auth Long memberId,
        @PathVariable Long webtoonId) {
        boolean isFavorite = memberService.isFavoriteWebtoon(memberId, webtoonId);
        return ResponseEntity.ok(isFavorite);
    }

    @GetMapping("/favorite")
    public ResponseEntity<List<InterestedWebtoonResponse>> getAllFavoriteWebtoon(
        @Auth Long memberId) {
        return ResponseEntity.ok(memberService.getAllFavoriteWebtoon(memberId));
    }

    @DeleteMapping("/favorite/{WebtoonId}")
    public ResponseEntity<Void> deleteFavoriteWebtoon(@Auth Long memberId,
        @PathVariable Long WebtoonId) {
        memberService.deleteFavoriteWebtoon(memberId, WebtoonId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/works")
    public ResponseEntity<WorkResDto> getAllWorks(@Auth Long memberId,
        @RequestParam(defaultValue = "all") String status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "25") int size) {
        PageReqDto pageReqDto = PageReqDto.of(page, size);
        return ResponseEntity.ok(memberService.getAllWorks(memberId, status, pageReqDto));
    }

    /**
     * 내 댓글, 답글 조회
     */
    @GetMapping("/comments-and-recomments")
    public ResponseEntity<PageResDto<MyCommentsAndRecommentsListResDto>> getMyCommentsAndRecomments(
        @Auth Long memberId,
        @RequestParam int page,
        @RequestParam int size,
        @RequestParam(defaultValue = "DESC") String order,
        @RequestParam(required = false) String type) {
        PageReqDto pageReqDto = PageReqDto.of(null, page, size, order);

        PageResDto<MyCommentsAndRecommentsListResDto> response = commentService.getMyCommentsAndRecomments(
            memberId, type, pageReqDto);

        return ResponseEntity.ok(response);
    }
}
