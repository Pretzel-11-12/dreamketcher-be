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
