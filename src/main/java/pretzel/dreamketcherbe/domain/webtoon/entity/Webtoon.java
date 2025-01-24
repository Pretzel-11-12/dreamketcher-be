package pretzel.dreamketcherbe.domain.webtoon.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.webtoon.dto.CreateWebtoonReqDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.UpdateWebtoonReqDto;

@Table(name = "webtoons")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE webtoons SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Webtoon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String thumbnail;

    @Column(nullable = false)
    private String prologue;

    @Column(nullable = false)
    private String story;

    @ColumnDefault("'PRE_SERIES'")
    private String status;

    @ColumnDefault("0.0")
    @Column(nullable = false, name = "average_star")
    private float averageStar;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int episodeCount;

    @Column(nullable = false, name = "is_deleted")
    @ColumnDefault("false")
    private boolean isDeleted;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int interestCount;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @Builder
    private Webtoon(String title, String thumbnail, String prologue, String story,
        String status, Member member) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.prologue = prologue;
        this.story = story;
        this.status = status;
        this.member = member;
    }

    public static Webtoon addOf(CreateWebtoonReqDto dto, Member member, ObjectMapper objectMapper) {
        try {
            return Webtoon.builder()
                .title(dto.title())
                .thumbnail(dto.thumbnail())
                .prologue(objectMapper.writeValueAsString(dto.prologue()))
                .story(dto.story())
                .member(member)
                .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("직렬화에 실패하였습니다.", e);
        }
    }

    public void updateOf(UpdateWebtoonReqDto dto, ObjectMapper objectMapper) {
        try {
            this.title = dto.title();
            this.thumbnail = dto.thumbnail();
            this.prologue = objectMapper.writeValueAsString(dto.prologue());
            this.story = dto.story();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("직렬화에 실패하였습니다.", e);
        }
    }

    public void isAuthor(Long memberId) {
        if (!member.getId().equals(memberId)) {
            throw new IllegalStateException(memberId + ", 작성자가 아닙니다.");
        }
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public void incrementInterestCount(int count) {
        this.interestCount += count;
    }

    public void decrementInterestCount(int count) {
        this.interestCount -= count;
    }

    public void softDelete() {
        if (!this.isDeleted) {
            this.isDeleted = true;
        }
    }
}
