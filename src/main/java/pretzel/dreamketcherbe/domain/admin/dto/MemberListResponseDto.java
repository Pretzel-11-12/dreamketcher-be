package pretzel.dreamketcherbe.domain.admin.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.MemberStatus;

public record MemberListResponseDto(
    List<MemberDto> members,
    int totalPages,
    long totalElements,
    int pageNumber,
    int pageSize,
    boolean hasNext
) {

    @Builder
    public MemberListResponseDto {
    }

    /**
     * Spring Data의 Page<Member> 객체를 MemberListResponseDto로 변환합니다.
     *
     * @param membersPage 변환할 회원 페이지 객체
     * @return 회원 목록과 페이지네이션 정보를 포함한 MemberListResponseDto
     */
    public static MemberListResponseDto from(Page<Member> membersPage) {
        List<MemberDto> members = membersPage.getContent().stream()
            .map(MemberDto::from)
            .toList();

        return MemberListResponseDto.builder()
            .members(members)
            .totalPages(membersPage.getTotalPages())
            .totalElements(membersPage.getTotalElements())
            .pageNumber(membersPage.getNumber())
            .pageSize(membersPage.getSize())
            .hasNext(membersPage.hasNext())
            .build();
    }
}

record MemberDto(
    Long id,
    String nickname,
    String email,
    String businessEmail,
    String imageUrl,
    MemberStatus status,
    LocalDateTime suspendedUntil,
    String suspensionReason,
    LocalDateTime createdAt
) {

    @Builder
    public MemberDto {
    }

    /**
     * Member 엔티티를 MemberDto로 변환합니다.
     *
     * @param member 변환할 Member 엔티티
     * @return 해당 멤버의 정보를 담은 MemberDto 인스턴스
     */
    public static MemberDto from(Member member) {
        return MemberDto.builder()
            .id(member.getId())
            .nickname(member.getNickname())
            .email(member.getEmail())
            .businessEmail(member.getBusinessEmail())
            .imageUrl(member.getImageUrl())
            .status(member.getStatus())
            .suspendedUntil(member.getSuspendedUntil())
            .suspensionReason(member.getSuspensionReason())
            .createdAt(member.getCreatedAt())
            .build();
    }
}
